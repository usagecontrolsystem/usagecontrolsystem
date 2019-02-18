/*******************************************************************************
 * Copyright 2018 IIT-CNR
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package it.cnr.iit.usagecontrolframework.contexthandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedTransferQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import com.datastax.driver.core.utils.UUIDs;
import com.google.gson.Gson;

import iit.cnr.it.ucs.configuration.BasicConfiguration;
import iit.cnr.it.ucsinterface.contexthandler.AbstractContextHandler;
import iit.cnr.it.ucsinterface.contexthandler.STATUS;
import iit.cnr.it.ucsinterface.contexthandler.scheduler.SchedulerInterface;
import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.PART;
import iit.cnr.it.ucsinterface.message.PDPResponse;
import iit.cnr.it.ucsinterface.message.PURPOSE;
import iit.cnr.it.ucsinterface.message.endaccess.EndAccessMessage;
import iit.cnr.it.ucsinterface.message.endaccess.EndAccessResponse;
import iit.cnr.it.ucsinterface.message.reevaluation.ReevaluationMessage;
import iit.cnr.it.ucsinterface.message.reevaluation.ReevaluationResponse;
import iit.cnr.it.ucsinterface.message.remoteretrieval.ACTION;
import iit.cnr.it.ucsinterface.message.remoteretrieval.MessagePipCh;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessMessage;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessResponse;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessMessage;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessResponse;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessResponseContent;
import iit.cnr.it.ucsinterface.pdp.PDPEvaluation;
import iit.cnr.it.ucsinterface.pip.PIPCHInterface;
import iit.cnr.it.ucsinterface.pip.exception.PIPException;
import iit.cnr.it.ucsinterface.sessionmanager.OnGoingAttribute;
import iit.cnr.it.ucsinterface.sessionmanager.Session;
import iit.cnr.it.ucsinterface.sessionmanager.SessionInterface;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLContextHandler;
import it.cnr.iit.usagecontrolframework.contexthandler.exceptions.MalformedObjectException;
import it.cnr.iit.usagecontrolframework.contexthandler.exceptions.MissingAttributeException;
import it.cnr.iit.usagecontrolframework.contexthandler.exceptions.RevokeException;
import it.cnr.iit.usagecontrolframework.contexthandler.exceptions.SessionManagerException;
import it.cnr.iit.usagecontrolframework.contexthandler.exceptions.WrongOrderException;
import it.cnr.iit.xacmlutilities.Attribute;
import it.cnr.iit.xacmlutilities.Category;
import it.cnr.iit.xacmlutilities.policy.PolicyHelper;
import it.cnr.iit.xacmlutilities.policy.utility.JAXBUtility;
import oasis.names.tc.xacml.core.schema.wd_17.AttributeType;
import oasis.names.tc.xacml.core.schema.wd_17.AttributesType;
import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

/**
 * This is the class implementing a contexthandler with low concurrency.
 * <p>
 * We will provide some different implementations of the context handler, so
 * that the user can pick up the most suitable to its case. This implementation
 * of the context handler works exactly in the same way of the first release.
 * The only difference here is how the changing of the value of an attribute is
 * managed: basically the context handler spawns a thread in charge of
 * monitoring eventual changes in the value of the attributes. This thread stays
 * in a sleeping state unless it is waken up by the calling of a proper function
 * performed by the PIP.
 * </p>
 * <p>
 * This implementation of the context handler can handle a single request per
 * time (as it was for the previous implementation). Hence here we do not have
 * to parse the configuration, because this implementation can handle only a
 * single thread. This single thread is represented by the AttributeMonitor
 * actor which implements the Callable<String> interface. We have chosen this
 * approach because we might be interested in having something to signal us the
 * result of reevaluation. This context handler has as additional parameter a
 * blocking queue that will be used to put the notification received by the
 * various PIPs, since once a notification has been received, all the PIPs will
 * be queried, then this queue MUST contain, unless something changes in the
 * architecture a single element only. Since we may have also remote attributes,
 * that queue may become very big because we don't know how many remote
 * attributes we may need to monitor, in that case the queue will be managed
 * like an hash function where the key will be the session id, so that, for each
 * session, it is possible to have a single attribute that notifies the changes.
 * <br>
 * </p>
 *
 *
 * @author antonio
 *
 */
final public class ContextHandlerLC extends AbstractContextHandler {

	// ---------------------------------------------------------------------------
	// CONSTANTS
	// ---------------------------------------------------------------------------
	private final static String TRYACCESS_POLICY = "pre";
	private final static String STARTACCESS_POLICY = "ongoing";
	private final static String ENDACCESS_POLICY = "post";
	// this is the string that, in an URI separates the PEP from the node
	// address
	private static final String PEP_ID_SEPARATOR = "#";

	// ---------------------------------------------------------------------------
	// ContextHandler components
	// ---------------------------------------------------------------------------
	// monitors if the value of an attribute changes
	private AttributeMonitor attributeMonitor = new AttributeMonitor();
	// queue in charge of storing the changing in the attributes
	private LinkedTransferQueue<MessagePipCh> attributesChanged = new LinkedTransferQueue<MessagePipCh>();
	// the thread object in charge of performing reevaluation
	private Thread thread = new Thread(attributeMonitor);
	// boolean variable that states if the thread has to run again or not
	private volatile boolean continueMonitor = true;

	private final Logger LOGGER = Logger.getLogger(ContextHandlerLC.class.getName());

	private SchedulerInterface scheduler;

	/**
	 *
	 * @param configuration
	 */
	public ContextHandlerLC(XMLContextHandler configuration) {
		super(configuration);
	}

	/**
	 * starts the thread in charge of monitoring the changes notified by PIPs
	 */
	public boolean startThread() {
		if (isInitialized()) {
			thread.start();
			return true;
		}
		return false;
	}

	/**
	 * stop the thread in charge of monitoring the changes notified by PIPs
	 */
	public boolean stopThread() {
		continueMonitor = false;
		return false;
	}

	// ---------------------------------------------------------------------------
	// TRYACCESS
	// ---------------------------------------------------------------------------
	/**
	 * tryaccess method invoked by PEP<br>
	 * The following actions are performed:
	 * <ol>
	 * <li>policy set is extracted from the received one</li>
	 * <li>ongoing attributes are retrieved from the policy ongoing</li>
	 * <li>subject id and object id, for the received request, are retrieved
	 * from the request itself</li>
	 * <li>retrieve method of each PIP is invoked</li>
	 * <li>request evaluation</li>
	 * <li>PDP response is checked</li>
	 * </ol>
	 *
	 * @param message
	 *            the message received by the PEP
	 *
	 */
	@Override
	public void tryAccess(Message message) {
		if (!isInitialized() || message == null || !(message instanceof TryAccessMessage)) {
			LOGGER.log(Level.SEVERE,
					"INVALID tryAccess " + isInitialized() + "\t" + (message instanceof TryAccessMessage));
			return;
		}

		LOGGER.log(Level.INFO, "[TIME] tryaccess received at " + System.currentTimeMillis());

		TryAccessMessage tryAccess = (TryAccessMessage) message;

		PolicyHelper policyHelper = PolicyHelper.buildPolicyHelper(tryAccess.getPolicy());

		// eventual scheduling
		List<Attribute> attributes = policyHelper.getAttributesForCondition(TRYACCESS_POLICY);
		System.out.println("[TIME] tryaccess begin scheduling " + System.currentTimeMillis());
		HashMap<String, Integer> attributesIP = retrieveAttributesIp(attributes);
		/*
		 * if (!tryAccess.getScheduled()) { String ip =
		 * scheduler.getIp(attributesIP); if (!ip.equals(getIp()) &&
		 * !ip.equals("localhost") && !ip.equals("UNAVAILABLE")) { // String
		 * request = makeRequestFull(tryAccess.getRequest(), attributes, //
		 * STATUS.TRYACCESS, false); schedule(tryAccess, tryAccess.getRequest(),
		 * ip, STATUS.TRYACCESS); System.out.println(
		 * "[TIME] tryaccess end scheduling at " + System.currentTimeMillis());
		 * return; } }
		 */

		// Assign the session id
		String sessionId = createSessionId();

		// prepare the request for the pdp
		String policy = retrievePolicy(tryAccess);
		String request = tryAccess.getRequest();

		// make the request complete before reevaluation
		String requestFull = makeRequestFull(request, attributes, STATUS.TRYACCESS, true);

		System.out.println(requestFull);
		System.out.println("-------------");
		System.out.println(policy);

		// perform the evaluation
		StringBuilder policyBuilder = new StringBuilder();
		PDPEvaluation pdpEvaluation = getPdpInterface().evaluate(requestFull, policyBuilder.append(policy),
				STATUS.TRYACCESS);

		policy = policyBuilder.toString();

		// status of the incoming request
		String status = TRY_STATUS;

		String pdpResponse = pdpEvaluation.getResponse();
		LOGGER.log(Level.INFO, "[TIME] tryaccess evaluated at " + System.currentTimeMillis() + " " + pdpResponse );

		// if access decision is PERMIT - update SM DB entry
		if (pdpResponse.equalsIgnoreCase("Permit")) {
			/**
			 * If tryAccess was scheduled, then the ip to be stored is the one
			 * of the node that has the PEP attached, otherwise it is the URL of
			 * this node
			 */
			insertInSessionManager(sessionId, policy, request, status,
					tryAccess.getScheduled() ? tryAccess.getPepUri()
							: getIp() + PEP_ID_SEPARATOR + tryAccess.getSource(),
					policyHelper, tryAccess.getScheduled() ? tryAccess.getSource() : getIp());

			LOGGER.log(Level.INFO, "[TIME] PERMIT tryaccess ends at " + System.currentTimeMillis());

			// // obligation
			// getObligationManager().translateObligations(pdpEvaluation,
			// sessionId,
			// TRY_STATUS);
			// } else { // response is DENY, INDETERMINATE or NOT APPLICABLE
			//
			// // obligation
			// getObligationManager().translateObligations(pdpEvaluation,
			// sessionId,
			// TRY_STATUS);
		}
		// obligation
		getObligationManager().translateObligations(pdpEvaluation, sessionId, TRY_STATUS);

		TryAccessResponse tryAccessResponse = new TryAccessResponse(getIp(), tryAccess.getSource(), message.getID());
		TryAccessResponseContent tryAccessResponseContent = new TryAccessResponseContent();
		tryAccessResponseContent.setSessionId(sessionId);
		tryAccessResponseContent.setStatus(pdpResponse);
		tryAccessResponseContent.setPDPEvaluation(pdpEvaluation);
		tryAccessResponse.setContent(tryAccessResponseContent);
		if (tryAccess.getScheduled()) {
			tryAccessResponse.setDestinationType();
		}
		getRequestManagerToChInterface().sendMessageToOutside(tryAccessResponse);
	}

	/**
	 * Effective call to the scheduler.
	 * <p>
	 * This is the function where the scheduling is effectively performed.
	 * Basing on the type of message received the appropriate action is executed
	 * and then the appropriate node is called.
	 * </p>
	 *
	 * @param message
	 *            the original message to be prepare to be sent to the node
	 * @param request
	 *            the request (eventual)
	 * @param ip
	 *            the ip of this node
	 * @param status
	 *            the status for which the call is issued
	 */
	private void schedule(Message message, String request, String ip, STATUS status) {
		switch (status) {
			case TRYACCESS:
				// TryAccessMessage tryAccess = (TryAccessMessage) message;
				TryAccessMessage tryAccess = new Gson().fromJson(new Gson().toJson(message), TryAccessMessage.class);
				tryAccess.getContent().setPepUri(getIp() + PEP_ID_SEPARATOR + message.getSource());
				tryAccess.getContent().setRequest(request);
				getForwardingQueue().addSwappedMessage(message.getID(), message);
				tryAccess.setSource(getIp());
				tryAccess.setDestination(ip);
				tryAccess.setScheduled();
				System.out.println("[TIME] tryAccess scheduled to other node " + System.currentTimeMillis());
				getRequestManagerToChInterface().sendMessageToOutside(tryAccess);
				return;
			case STARTACCESS:
				StartAccessMessage startAccessMessage = new Gson().fromJson(new Gson().toJson(message),
						StartAccessMessage.class);
				getForwardingQueue().addSwappedMessage(message.getID(), message);
				startAccessMessage.setSource(getIp());
				startAccessMessage.setDestination(ip);
				startAccessMessage.setScheduled();
				System.out.println("[TIME] startAccess scheduled to other node " + System.currentTimeMillis());
				getRequestManagerToChInterface().sendMessageToOutside(startAccessMessage);
				return;
			case ENDACCESS:
				EndAccessMessage endAccessMessage = new Gson().fromJson(new Gson().toJson(message), EndAccessMessage.class);
				getForwardingQueue().addSwappedMessage(message.getID(), message);
				endAccessMessage.setSource(getIp());
				endAccessMessage.setDestination(ip);
				endAccessMessage.setScheduled();
				System.out.println("[TIME] endAccess scheduled to other node " + System.currentTimeMillis());
				getRequestManagerToChInterface().sendMessageToOutside(endAccessMessage);
				return;
			case REEVALUATION:
				ReevaluationMessage reevaluationMessage = (ReevaluationMessage) message;
				System.out.println("[TIME] reevaluation scheduled to other node " + System.currentTimeMillis());
				getRequestManagerToChInterface().sendMessageToOutside(reevaluationMessage);
				return;
			default:
				return;
		}
	}

	/**
	 * Retrieves the number of attributes managed by each ip
	 *
	 * @param attributes
	 *            the list of attributes
	 * @return the map in which the key is the IP address of the node and the
	 *         value is the number of attributes managed by that node
	 */
	private synchronized HashMap<String, Integer> retrieveAttributesIp(List<Attribute> attributes) {
		HashMap<String, Integer> hashMap = new HashMap<>();
		hashMap.put("localhost", 1);
		return hashMap;
	}

	/**
	 * Attempt to make the request full in order to let the PDP evaluate
	 * correctly the request.
	 *
	 * @param request
	 *            the request in string format
	 * @param attributes
	 *            the list of attributes required to evaluate the request
	 * @param complete
	 *            states if we need to add all the attributes (if true) or only
	 *            the local ones, if false
	 * @return a String that represents the request itself
	 */
	private synchronized String makeRequestFull(String request, List<Attribute> attributes, STATUS status,
			boolean complete) {
		try {
			RequestType requestType = JAXBUtility.unmarshalToObject(RequestType.class, request);
			// handles all the cases except startaccess
			if (status == STATUS.TRYACCESS || status == STATUS.ENDACCESS || status == STATUS.REVOKE) {
				List<Attribute> external = extractExternal(attributes, requestType);
				System.out.println(external.toString());
				retrieveLocalAttributes(requestType);
				if (complete) {
					if (getPipRetrieval() != null && external.size() > 0) {
						System.out.println("[TIME] retrieve external start at " + System.currentTimeMillis());
						getPipRetrieval().retrieve(requestType, external);
						System.out.println("[TIME] retrieve external ends at " + System.currentTimeMillis());
					}

					if (getPipRetrieval() == null && external.size() > 0) {
						LOGGER.log(Level.SEVERE, "Policy requires attributes that are not accessible!!");
						return null;
					}
				}
			}
			/*
			 * handles the startaccess case which is different because in this
			 * case we have to perform the subscribe operation to signal to the
			 * PIPs to continuously monitor the attributes
			 */
			if (status == STATUS.STARTACCESS) {
				List<Attribute> external = extractExternal(attributes, requestType);
				subscribeLocalAttributes(requestType);
				if (complete) {
					if (getPipRetrieval() != null && external.size() > 0) {
						System.out.println("[TIME] Subscribe external starts at " + System.currentTimeMillis());
						getPipRetrieval().subscribe(requestType, external);
						System.out.println("[TIME] subscribe external ends at " + System.currentTimeMillis());
					}
					if (getPipRetrieval() == null && external.size() > 0) {
						try {
							throw new MissingAttributeException("Policy requires attributes that are not accessible!!");
						} catch (MissingAttributeException e) {
							e.printStackTrace();
							return null;
						}
					}
				}
			}
			String requestString = JAXBUtility.marshalToString(RequestType.class, requestType, "Request",
					JAXBUtility.SCHEMA);
			return requestString;
		} catch (JAXBException exception) {
			exception.printStackTrace();
			return null;
		}
	}

	private void retrieveLocalAttributes(RequestType requestType) {
		try {
			for (PIPCHInterface pip : getPipList()) {
				pip.retrieve(requestType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void subscribeLocalAttributes(RequestType requestType) {
		try {
			for (PIPCHInterface pip : getPipList()) {
				pip.subscribe(requestType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * From the list of attributes required by the actual policy, extract the
	 * ones related to external UCS. At first we try to see if the attribute is
	 * managed by one of the PIPs local to the ContextHandler,<b>NEW!!</b> then,
	 * if we cannot find the PIP among the local ones, we check to see if the
	 * attribute is already in the request, otherwise the attribute has to be
	 * retrieved from a remote PIP.
	 *
	 * @param attributes
	 *            the list of attributes
	 * @return the list of attributes without those attributes that can be
	 *         retrieved by internal PIPs
	 */
	private List<Attribute> extractExternal(List<Attribute> attributes, RequestType request) {
		for (int i = 0; i < attributes.size();) {
			boolean found = false;
			attributes.get(i).getAttributeValueMap().clear();
			for (PIPCHInterface pipBase : getPipList()) {
				LinkedList<String> searchList = new LinkedList<>(pipBase.getAttributeIds());
				if (searchList.contains(attributes.get(i).getAttributeId())) {
					found = true;
					break;
				}
			}
			if (found != true) {
				for (AttributesType attributeType : request.getAttributes()) {
					for (AttributeType att : attributeType.getAttribute()) {
						if (attributes.get(i).getAttributeId().equals(att.getAttributeId())) {
							found = true;
							break;
						}
					}
				}
			}
			if (found == true) {
				attributes.remove(i);
			} else {
				i += 1;
			}
		}
		return attributes;
	}

	/**
	 * It creates a new simple session id
	 *
	 * @return session id to associate to the incoming session during the
	 *         tryaccess
	 */
	private synchronized String createSessionId() {
		return UUIDs.timeBased().toString();
	}

	/**
	 * Retrieves the policy to be used to evaluate the request in string format
	 *
	 * @param tryAccess
	 *            the message received by the context handler
	 * @return the string representing the policy
	 */
	private String retrievePolicy(TryAccessMessage tryAccess) {
		String policy = tryAccess.getPolicy();
		if (policy == null) {
			String policyString = getPapInterface().retrievePolicy(tryAccess.getPolicyId());

			if (policyString == null) {
				LOGGER.log(Level.SEVERE, "UNABLE to RETRIEVE the POLICY");
				return null;
			}
			policy = policyString;
		}
		return policy;
	}

	/**
	 * this function inserts inside the session manager a new session with its
	 * relative on_going_attributes.
	 *
	 * @param sessionId
	 *            the session id
	 * @param uxacmlPol
	 *            the uxacml policy
	 * @param originalRequest
	 *            the original request, not the fat one because, whenever we
	 *            need to re-evaluate the request we will retrieval from the
	 *            various PIPs a fresh value
	 * @param status
	 *            status of the request
	 * @param pepUri
	 *            the URI of the PEP in order to allow communication between the
	 *            PEP and the context handler
	 * @param policyHelper
	 *            object representing the policy to be used in the various
	 *            evaluations the subject id
	 */
	private void insertInSessionManager(String sessionId, String uxacmlPol, String request, final String status,
			String pepUri, PolicyHelper policyHelper, String ip) {
		try {
			RequestType requestType = JAXBUtility.unmarshalToObject(RequestType.class, request);

			// retrieve the id of ongoing attributes
			List<Attribute> onGoingAttributes = policyHelper.getAttributesForCondition(STARTACCESS_POLICY);
			List<String> onGoingAttributesForSubject = getAttributesForCategory(onGoingAttributes, Category.SUBJECT);
			List<String> onGoingAttributesForResource = getAttributesForCategory(onGoingAttributes, Category.RESOURCE);
			List<String> onGoingAttributesForAction = getAttributesForCategory(onGoingAttributes, Category.ACTION);
			List<String> onGoingAttributesForEnvironment = getAttributesForCategory(onGoingAttributes,
					Category.ENVIRONMENT);

			// retrieve the values of attributes in the request
			String subjectName = requestType.extractValue(Category.SUBJECT);
			String resourceName = requestType.extractValue(Category.RESOURCE);
			String actionName = requestType.extractValue(Category.ACTION);

			// System.out.println("POLICY INSERTED: " + uxacmlPol);

			// insert all the values inside the session manager
			if (!getSessionManagerInterface().createEntry(sessionId, uxacmlPol, request, onGoingAttributesForSubject,
					onGoingAttributesForResource, onGoingAttributesForAction, onGoingAttributesForEnvironment, status,
					pepUri, ip, subjectName, resourceName, actionName)) {
				LOGGER.log(Level.SEVERE, "[Context Handler] TryAccess: some error occurred, session " + sessionId
						+ " has not been stored correctly");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

	}

	/**
	 * Retrieves the AttributeIDs of the attributes used for the ongoing
	 * evaluation
	 *
	 * @param onGoingAttributes
	 *            the list of attributes used for ongoing evaluation
	 * @param category
	 *            the category of the attributes
	 * @return the list of the string representing the IDs of the attributes
	 */
	private List<String> getAttributesForCategory(List<Attribute> onGoingAttributes, Category category) {
		ArrayList<String> attributeIds = new ArrayList<>();
		for (Attribute attribute : onGoingAttributes) {
			if (attribute.getCategory() == category) {
				attributeIds.add(attribute.getAttributeId());
			}
		}
		if (attributeIds.size() == 0)
			return null;
		return attributeIds;
	}

	// ---------------------------------------------------------------------------
	// START ACCESS
	// ---------------------------------------------------------------------------
	/**
	 * startaccess method invoked by PEP<br>
	 * The following actions are performed:
	 * <ol>
	 * <li>the session that should be reevaluated is retrieved through the
	 * session manager</li>
	 * <li>subscribe method of each PIP is invoked</li>
	 * <li>request evaluation</li>
	 * <li>PDP response is checked</li>
	 * <li>in case of "deny" response, the considered session is revoked and the
	 * revoke method is invoked</li>
	 * </ol>
	 *
	 * @param sId
	 *            : session id of the involved session
	 * @return generic string which represents PDP response
	 * @throws WrongOrderException
	 * @throws SessionManagerException
	 * @throws RevokeException
	 *
	 */
	@Override
	public void startAccess(Message message) throws Exception {

		// BEGIN parameter checking
		if (!isInitialized() || message == null || !(message instanceof StartAccessMessage)) {
			LOGGER.log(Level.SEVERE,
					"INVALID startAccess " + isInitialized() + "\t" + (message instanceof StartAccessMessage));
			return;
		}
		// END parameter checking

		System.out.println("[TIME] startaccess begins at " + System.currentTimeMillis());

		// LOGGER.log(Level.INFO,
		// "[Context Handler] Startaccess is received for session ID: " + sId);

		StartAccessMessage startAccessMessage = (StartAccessMessage) message;
		String sessionId = startAccessMessage.getSessionId();

		SessionInterface sessionToReevaluate = getSessionManagerInterface().getSessionForId(sessionId);

		if (sessionToReevaluate == null) {
			return;
		}

		PolicyHelper policyHelper = PolicyHelper.buildPolicyHelper(sessionToReevaluate.getPolicySet());

		List<Attribute> attributes = policyHelper.getAttributesForCondition(STARTACCESS_POLICY);
		System.out.println("[TIME] startaccess begin scheduling at " + System.currentTimeMillis());
		HashMap<String, Integer> attributesIP = retrieveAttributesIp(attributes);
		/*
		 * if (!startAccessMessage.getScheduled()) { String ip =
		 * scheduler.getIp(attributesIP); if (!ip.equals(getIp()) &&
		 * !ip.equals("localhost")) { schedule(startAccessMessage, null, ip,
		 * STATUS.STARTACCESS);
		 * System.out.println("[TIME] startaccess end scheduling at " +
		 * System.currentTimeMillis()); return; } }
		 */

		StartAccessResponse response = new StartAccessResponse(startAccessMessage.getDestination(),
				startAccessMessage.getSource(), message.getID());

		// check if there actually is a request to reevaluate for the received
		// session id
		if (sessionToReevaluate == null || (!sessionToReevaluate.getStatus().equals(TRY_STATUS))) {
			// no request to reevaluate(some problem occurred during request and
			// policy retrieving)
			LOGGER.log(Level.WARNING,
					"[Context Handler] Startaccess: tryaccess must be performed yet for session " + sessionId);
			throw new WrongOrderException(
					"[Context Handler] Startaccess: tryaccess must be performed yet for session " + sessionId);
		}

		String request = sessionToReevaluate.getOriginalRequest();

		// make the request complete before reevaluation
		String requestFull = makeRequestFull(request, policyHelper.getAttributesForCondition(STARTACCESS_POLICY),
				STATUS.STARTACCESS, true);

		// perform the evaluation
		PDPResponse pdpEvaluation = (PDPResponse) getPdpInterface().evaluate(requestFull,
				policyHelper.getConditionForEvaluation(STARTACCESS_POLICY));

		System.out.println("[TIME] startaccess evaluation ends at " + System.currentTimeMillis());

		response.setStatus(pdpEvaluation.getResponse());
		response.setResponse(pdpEvaluation);

		// PDP returns PERMIT
		if (pdpEvaluation.getResponse().equalsIgnoreCase("Permit")) {

			// obligation
			getObligationManager().translateObligations(pdpEvaluation, sessionId, START_STATUS);

			// update session status
			Boolean bool = getSessionManagerInterface().updateEntry(sessionId, START_STATUS);

			if (bool == false) {
				LOGGER.log(Level.INFO, "[Context Handler] Startaccess: session " + sessionId + " status not updated");
			}
			System.out.println("[TIME] PERMIT startaccess ends at " + System.currentTimeMillis());
			response.setStatus(pdpEvaluation.getResponse());
		}

		else { // PDP returns DENY, INDETERMINATE or NOT APPLICABLE

			// obligation
			getObligationManager().translateObligations(pdpEvaluation, sessionId, START_STATUS);

			// access must be revoked and the related entry from the db deleted,
			// after
			// performing unsubscribe operation for the on going attributes
			if (revoke(sessionToReevaluate, attributesIP)) {
				LOGGER.log(Level.INFO,
						"[Context Handler] Startaccess: access is revoked for session with ID: " + sessionId);
				// delete db entry for session sId
				if (!getSessionManagerInterface().deleteEntry(sessionId)) {
					LOGGER.log(Level.SEVERE,
							"[Context Handler] Startaccess: Some problem occurred during entry deletion for session "
									+ sessionId);
					throw new SessionManagerException(
							"[Context Handler] Startaccess: Some problem occurred during entry deletion for session "
									+ sessionId);
				}
			}
			LOGGER.log(Level.SEVERE,
					"[Context Handler] Startaccess: Some problem occurred during execution of revokaccess for session "
							+ sessionId);
			throw new RevokeException(
					"[Context Handler] Startaccess: Some problem occurred during execution of revokaccess for session "
							+ sessionId);
		}
		if (startAccessMessage.getScheduled()) {
			response.setDestinationType();
		}
		getRequestManagerToChInterface().sendMessageToOutside(response);
	}

	// ---------------------------------------------------------------------------
	// REVOKE
	// ---------------------------------------------------------------------------
	/**
	 * This is the code for the revoke. A revoke is always triggered by and
	 * EndAccess, in this function, all the attributes are unsubscribed TODO:
	 * handle the case in which there were also remote attributes in the policy
	 *
	 * @param session
	 *            the session for which the revoke has to occur
	 * @return true if everything goes ok, false otherwise
	 */
	private synchronized boolean revoke(SessionInterface session, HashMap<String, Integer> attributesIP) {

		System.out.println("[TIME] revoke begins at " + System.currentTimeMillis());

		boolean otherSessions = true;

		ArrayList<Attribute> attributes = new ArrayList<>();
		otherSessions = attributesToUnsubscribe(session.getId(), attributes);

		// call unsubscribe method of each involved PIP
		if (!otherSessions) {
			// unsubscribe method invocation for each PIP
			// LOGGER.info(
			// "[Context Handler] Involved attributes should be unsubscribed.
			// Every
			// PIP will be notified");
			// LOGGER.info(attributes.toString());
			for (int i = 0; i < getPipList().size(); i++) {
				try {
					getPipList().get(i).unsubscribe(attributes);
					if (getPipRetrieval() != null) {
						getPipRetrieval().unsubscribe(attributes, attributesIP);
					}
				} catch (Exception x) {
					x.printStackTrace();
					return false;
				}
			}
		}

		// database entry for the current must be deleted
		try {
			if (!getSessionManagerInterface().deleteEntry(session.getId())) {
				LOGGER.log(Level.SEVERE,
						"[Context Handler] Endaccess: Some problem occurred during entry deletion for session "
								+ session.getId());
				throw new SessionManagerException(
						"[Context Handler] Endaccess: Some problem occurred during entry deletion for session "
								+ session.getId());
			}
		} catch (SessionManagerException sme) {
			sme.printStackTrace();
			return false;
		}

		System.out.println("[TIME] revoke ends at " + System.currentTimeMillis());

		return true;
	}

	/**
	 * This function checks if there are attributes to be unsubscribed.
	 * <p>
	 * The first step is to retrieve the list of ongoing attributes, then we
	 * have to unsubscribe all those attributes that are not needed anymore.
	 * </p>
	 *
	 * @param sessionId
	 *            the id of the session we're revoking
	 * @param attributes
	 *            the json object to be filled by this function
	 * @return true if threre are attributes to unsubscribe, false otherwise
	 *         <br>
	 *
	 */
	private boolean attributesToUnsubscribe(String sessionId, ArrayList<Attribute> attributes) {
		String subjectName = "";
		String resourceName = "";
		String actionName = "";
		boolean otherSessions = true;
		// retrieve on going attributes for both subject and object
		Collection<OnGoingAttribute> onGoingAttributes = getSessionManagerInterface().getOnGoingAttributes(sessionId);
		List<OnGoingAttribute> onGoingAttributesForSubject = new LinkedList<>();
		List<OnGoingAttribute> onGoingAttributesForResource = new LinkedList<>();
		List<OnGoingAttribute> onGoingAttributesForAction = new LinkedList<>();
		List<OnGoingAttribute> onGoingAttributesForEnvironment = new LinkedList<>();

		// build attribute lists for subject, resource, action and environment
		if (onGoingAttributes != null && onGoingAttributes.size() > 0) {
			// fill the correspondent list of ongoingattributes
			for (OnGoingAttribute attribute : onGoingAttributes) {
				if (attribute.getSubjectName() != null && !attribute.getSubjectName().equals("null")) {
					onGoingAttributesForSubject.add(attribute);
					subjectName = attribute.getSubjectName();
				} else if (attribute.getResourceName() != null && !attribute.getResourceName().equals("null")) {
					onGoingAttributesForResource.add(attribute);
					resourceName = attribute.getResourceName();
				} else if (attribute.getActionName() != null && !attribute.getActionName().equals("null")) {
					onGoingAttributesForAction.add(attribute);
					actionName = attribute.getActionName();
				} else
					onGoingAttributesForEnvironment.add(attribute);
			}
		}

		// builds up the JSON object that is needed to perform unsubscribe
		if (onGoingAttributes != null && onGoingAttributes.size() > 0) {
			// ongoingattributes for object
			for (OnGoingAttribute attribute : onGoingAttributesForResource) {

				// retrieve all the active sessions which deal with the
				// considered on
				// going attribute
				List<SessionInterface> tempList = getSessionManagerInterface()
						.getSessionsForResourceAttributes(resourceName, attribute.getAttributeId());
				// check if there are not any active sessions which deal with
				// the
				// attribute
				if (tempList == null || tempList.isEmpty() || tempList.size() == 1) {
					otherSessions = false;
					Attribute tmpAttribute = new Attribute();
					tmpAttribute.createAttributeId(attribute.getAttributeId());
					tmpAttribute.setAdditionalInformations(resourceName);
					attributes.add(tmpAttribute);
				}
			}

			// verify what subject attributes must be unsubscribed
			for (OnGoingAttribute attribute : onGoingAttributesForSubject) {

				// retrieve all the active sessions which deal with the
				// considered on
				// going attribute
				List<SessionInterface> tempList = getSessionManagerInterface()
						.getSessionsForSubjectAttributes(subjectName, attribute.getAttributeId());
				// check if there are not any active sessions which deal with
				// the
				// attribute
				if (tempList == null || tempList.isEmpty() || tempList.size() == 1) {
					otherSessions = false;
					Attribute tmpAttribute = new Attribute();
					tmpAttribute.createAttributeId(attribute.getAttributeId());
					tmpAttribute.setAdditionalInformations(subjectName);
					attributes.add(tmpAttribute);
				}
			}

			// on going attributes for action
			for (OnGoingAttribute attribute : onGoingAttributesForAction) {
				List<SessionInterface> tempList = getSessionManagerInterface()
						.getSessionsForActionAttributes(actionName, attribute.getAttributeId());
				if (tempList == null || tempList.isEmpty() || tempList.size() == 1) {
					otherSessions = false;
					Attribute tmpAttribute = new Attribute();
					tmpAttribute.createAttributeId(attribute.getAttributeId());
					tmpAttribute.setAdditionalInformations(actionName);
					attributes.add(tmpAttribute);
				}
			}

			// on going attributes for environment
			for (OnGoingAttribute attribute : onGoingAttributesForEnvironment) {
				List<SessionInterface> tempList = getSessionManagerInterface()
						.getSessionsForEnvironmentAttributes(attribute.getAttributeId());
				if (tempList == null || tempList.isEmpty() || tempList.size() == 1) {
					otherSessions = false;
					Attribute tmpAttribute = new Attribute();
					tmpAttribute.createAttributeId(attribute.getAttributeId());
					attributes.add(tmpAttribute);
				}
			}
		}
		return otherSessions;
	}

	// ---------------------------------------------------------------------------
	// END ACCESS
	// ---------------------------------------------------------------------------
	@Override
	public void endAccess(Message message) {
		// BEGIN parameter checking
		if (!isInitialized()) {
			LOGGER.log(Level.SEVERE, "CH not initialized correctly");
			return;
		}
		if (message == null || !(message instanceof EndAccessMessage)) {
			LOGGER.log(Level.SEVERE, "Invalid message in endaccess");
			return;
		}
		// END parameter checking
		try {
			EndAccessMessage endAccessMessage = (EndAccessMessage) message;
			String sessionId = endAccessMessage.getSessionId();

			System.out.println("[TIME] endaccess begins at " + System.currentTimeMillis());

			// LOGGER.log(Level.INFO,
			// "[Context Handler] Endaccess is received for session ID: " +
			// sId);

			// check if an entry actually exists in db
			SessionInterface sessionToReevaluate = getSessionManagerInterface()
					.getSessionForId(endAccessMessage.getSessionId());
			if (sessionToReevaluate == null || (!sessionToReevaluate.getStatus().equals(START_STATUS)
					&& !sessionToReevaluate.getStatus().equals(REVOKE_STATUS))) {
				// no entry exists for the actual session
				LOGGER.log(Level.INFO,
						"[Context Handler] Endaccess: a tryaccess or startaccess must be performed yet for session "
								+ sessionId + ", or the related endaccess has already been executed");
				throw new WrongOrderException(
						"[Context Handler] Endaccess: a tryaccess must be performed yet for session " + sessionId
								+ ", or the related endaccess has already been executed");
			}

			PolicyHelper policyHelper = PolicyHelper.buildPolicyHelper(sessionToReevaluate.getPolicySet());

			System.out.println("[TIME] endaccess scheduler starts at " + System.currentTimeMillis());
			List<Attribute> attributes = policyHelper.getAttributesForCondition(ENDACCESS_POLICY);
			HashMap<String, Integer> attributesIP = retrieveAttributesIp(attributes);
			/*
			 * if (!endAccessMessage.getScheduled()) { String ip =
			 * scheduler.getIp(attributesIP); if (!ip.equals(getIp()) &&
			 * !ip.equals("localhost")) { schedule(endAccessMessage, null, ip,
			 * STATUS.ENDACCESS);
			 * System.out.println("[TIME] endaccess scheduler ends at " +
			 * System.currentTimeMillis()); return; } }
			 */

			String request = sessionToReevaluate.getOriginalRequest();

			// make the request complete before reevaluation
			String requestFull = makeRequestFull(request, policyHelper.getAttributesForCondition(ENDACCESS_POLICY),
					STATUS.ENDACCESS, true);

			PDPEvaluation pdpEvaluation = getPdpInterface().evaluate(requestFull,
					policyHelper.getConditionForEvaluation(ENDACCESS_POLICY));

			System.out.println("[TIME] EndAccess evaluation ends at " + System.currentTimeMillis());

			if (pdpEvaluation.getResponse().equalsIgnoreCase("Permit")) { // PDP
				// returns
				// PERMIT

				// obligation
				getObligationManager().translateObligations(pdpEvaluation, sessionId, END_STATUS);

			}

			else {
				// PDP returns DENY, INDETERMINATE or NOT APPLICABLE

				// obligation
				getObligationManager().translateObligations(pdpEvaluation, sessionId, END_STATUS);

			}

			EndAccessResponse response = new EndAccessResponse(endAccessMessage.getDestination(),
					endAccessMessage.getSource(), message.getID());
			response.setResponse((PDPResponse) pdpEvaluation);
			response.setStatus(pdpEvaluation.getResponse());

			if (endAccessMessage.getScheduled()) {
				response.setDestinationType();
			}

			// access must be revoked
			if (revoke(sessionToReevaluate, attributesIP)) {
				// LOGGER.log(Level.INFO, "[Context Handler] Endaccess: Session
				// with ID
				// "
				// + sId + " is terminated");
				System.out.println("[TIME] endaccess evaluation with revoke ends at " + System.currentTimeMillis());

			}

			getRequestManagerToChInterface().sendMessageToOutside(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ---------------------------------------------------------------------------
	// ATTRIBUTE CHANGED
	// ---------------------------------------------------------------------------
	/**
	 * API offered by the context handler to the PIP in case some attribute gets
	 * changed
	 *
	 * @param message
	 */
	@Override
	public void attributeChanged(Message message) {
		LOGGER.log(Level.INFO, "Attribute changed received " + System.currentTimeMillis());
		if (message == null || !(message instanceof MessagePipCh)) {
			LOGGER.log(Level.SEVERE, "Invalid message provided");
		}
		// non blocking insertion in the queue of attributes changed
		attributesChanged.put((MessagePipCh) message);
	}

	// ---------------------------------------------------------------------------
	// MESSAGE FOR PIP
	// ---------------------------------------------------------------------------
	/**
	 * This function gets triggered once a pip retrieval has asked for an
	 * attribute that is under the control of this UCS.
	 *
	 * <p>
	 * The purpose of this function is to trigger the PIP in order to retrieve
	 * the value and then return this value to the caller
	 * </p>
	 */
	@Override
	public Message messageForPIP(Message message) {
		MessagePipCh messagePipCh = (MessagePipCh) message;
		try {
			if (messagePipCh.getAction() == ACTION.RETRIEVE_RESPONSE
					|| messagePipCh.getAction() == ACTION.SUBSCRIBE_RESPONSE) {
				getPipRetrieval().messageArrived(messagePipCh);
				return null;
			}
			// for each attribute in the list
			for (Attribute attribute : messagePipCh.getAttributes()) {
				for (PIPCHInterface pip : getPipList()) {
					// once you find the pip that manages that attribute,
					// perform the
					// right action
					LinkedList<String> searchList = new LinkedList<>(pip.getAttributeIds());
					if (searchList.contains(attribute.getAttributeId())) {

						switch (messagePipCh.getAction()) {
						case RETRIEVE:
							attribute.setValue(pip.getAttributesCharacteristics().get(attribute.getAttributeId())
									.getAttributeDataType(), pip.retrieve(attribute));
							// System.out.println("Attribute" + new
							// Gson().toJson(attribute));
							break;
						case SUBSCRIBE:
							attribute.setValue(attribute.getAttributeDataType(), pip.subscribe(attribute));
							break;
						default:
							break;
						}
					}
				}
			}
			switch (messagePipCh.getAction()) {
			case UNSUBSCRIBE:
				for (PIPCHInterface pip : getPipList()) {
					pip.unsubscribe(messagePipCh.getAttributes());
				}
				MessagePipCh response = new MessagePipCh(getIp(), messagePipCh.getSource());
				response.setPurpose(PURPOSE.ATTRIBUTE_RETRIEVAL_RESPONSE);
				response.setAction(ACTION.UNSUBSCRIBE_RESPONSE);
				return response;
			default:
				break;
			}
			return messagePipCh;
		} catch (PIPException pip) {
			pip.printStackTrace();
			return null;
		}
	}

	/****************************************************************************/
	/**																																				 **/
	/**																																				 **/
	/****************************************************************************/

	// ===========================================================================
	// AttributeMonitor class
	// ===========================================================================
	/**
	 * This class represents the object in charge of performing reevaluation.
	 * <p>
	 * Basically this thread waits for notifications coming from PIPs, when it
	 * receives a notification, it starts reevaluating all the sessions that are
	 * interested in that attribute. For this reason this thread will have to
	 * accomplish the following tasks:
	 * <ol type="i">
	 * <li>Retrieve all the sessions that are interested into that attribute. If
	 * the attribute contains any additional information (e.g. the name of the
	 * subject) obviously check if the additional information stored in the
	 * policy is the same.</li>
	 * <li>For each session: ask the scheduler if it has to be evaluated locally
	 * or by a remote site, in the latter send the request with the values of
	 * the local attributes to the remote site.</li>
	 * </ol>
	 * <br>
	 * </p>
	 * <p>
	 * <b>Implemented behavior: </b> <br>
	 * This thread waits on the list named attributesChanged for a message
	 * coming from a PIP. When this happens the reevaluation process described
	 * above can start. Obviously it has to check if the considered session is
	 * already being evaluated, for eample the PEP may ask to terminate a
	 * session and in the meanwhile an attribute has changed, but the session
	 * has to be evaluated only once.
	 * </p>
	 *
	 * @author antonio
	 *
	 */
	final private class AttributeMonitor implements Runnable {

		@Override
		public void run() {
			LOGGER.log(Level.INFO, "Attribute monitor started");
			while (continueMonitor) {
				try {
					MessagePipCh message = attributesChanged.take();
					List<Attribute> attributes = message.getAttributes();

					if (attributes == null) {
						LOGGER.log(Level.SEVERE, "Attributes list in the message is null");
						return;
					}
					if (!manageChanges(attributes, !message.getDestination().equals(PART.CH.toString()))) {
						LOGGER.log(Level.SEVERE, "Unable to handle all the changes");
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 *
		 * @param attributes
		 * @return
		 */
		private boolean manageChanges(List<Attribute> attributes, boolean isRemote) {
			for (Attribute attribute : attributes) {
				if (!reevaluateSessions(attribute, isRemote)) {
					return false;
				}
			}
			return true;
		}

		/**
		 * This is the function where the effective reevaluation takes place.
		 * <p>
		 * The reevaluation process is divided in the following steps:
		 * <ol>
		 * <li>Retrieve all the required informations [attributeID, eventual
		 * additional informations] from the json object passed as
		 * parameter</li>
		 * <li>From these informations extract the list of interested sessions
		 * from the sessionmanager</li>
		 * <li>Divide the remote sessions, i.e., the sessions that belong to
		 * another context handler to the one this context handler is in charge
		 * of reevaluating</li>
		 * <ul>
		 * <li>Notify all the other context handlers of the changing just
		 * occurred</li>
		 * <li>Reevaluate the sessions assigned to this context handler</li>
		 * </ul>
		 * </ol>
		 * </p>
		 *
		 * @param jsonObject
		 *            the jsonObject inserted by the PIP which attribute has
		 *            changed
		 * @return true if everything goes ok, false if some exception occurs
		 */
		private boolean reevaluateSessions(Attribute attribute, boolean isRemote) {

			try {
				// retrieve the attribute retrieval object from the JSON

				// retrieve the list of interested sessions
				LOGGER.log(Level.INFO, attribute.getAttributeId());
				List<SessionInterface> interestedSessions = retrieveSessions(attribute);
				if (interestedSessions == null || interestedSessions.size() == 0) {
					LOGGER.log(Level.INFO, "There are no sessions");
					return true;
				}
				if (isRemote) {
					// split remote and local sessions
					HashMap<String, ArrayList<String>> remote = removeRemoteSessions(interestedSessions);
					// notify remote sessions
					if (remote != null && remote.size() > 0) {
						notifyRemote(remote);
					}
					// if there aren't other sessions to be reevaluated, perform
					// a notify
					if (interestedSessions.size() == 0) {
						LOGGER.log(Level.INFO, "There are no other sessions");
						return true;
					}
				}
				// if (isRemote) {
				// addAttributeToRequest(attribute, interestedSessions);
				// }
				// reevaluate local sessions and notify the PEP
				for (SessionInterface session : interestedSessions) {
					reevaluateSession(session, attribute);
				}
				return true;
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "[Reevaluate sessions]" + e.getMessage());
				e.printStackTrace();
				return false;
			}
		}

		/**
		 * Adds the attribute that has just changed to the request, in this way,
		 * if the session will be reevaluated remotely, it won't be necessary to
		 * retrieve that attribute again
		 *
		 * @param attribute
		 *            the attribute we want to add to the request
		 * @param interestedSessions
		 *            the sessions interested by the reevaluation
		 * @throws JAXBException
		 * @deprecated
		 */
		@Deprecated
		private void addAttributeToRequest(Attribute attribute, List<SessionInterface> interestedSessions)
				throws JAXBException {
			for (SessionInterface session : interestedSessions) {
				String request = session.getOriginalRequest();
				RequestType requestType = JAXBUtility.unmarshalToObject(RequestType.class, request);
				requestType.addAttribute(attribute.getCategory().toString(),
						attribute.getAttributeDataType().toString(), attribute.getAttributeId(),
						attribute.getAttributeValues(attribute.getAttributeDataType()).get(0));
				session.setRequest(
						JAXBUtility.marshalToString(RequestType.class, requestType, "Request", JAXBUtility.SCHEMA));
			}
		}

		/**
		 * Given a certain attribute retrieval, extract from it two basics
		 * informations: the attributeId and the eventual additional
		 * informations in order to retrieve all the sessions related to that
		 * particular attributeid with that particular additionalInformations.
		 * We need also the additional informations in order to perform a
		 * prefiltering, in fact it may happen, for example, that during time
		 * the role of a person changes, but we don't want to reevaluate all the
		 * sessions that use the attribute role, but only those sessions that
		 * are interested in that aprticular person.
		 *
		 * @param attributeRetrieval
		 *            the attribute retrieval oject that represents the
		 *            attribute that has changed
		 * @return the list of sessions interested
		 */
		private List<SessionInterface> retrieveSessions(Attribute attributeRetrieval) {
			boolean valid = false;
			// System.out
			// .println("AttributeRetrieval " +
			// attributeRetrieval.getAttributeId()
			// + "\t" + attributeRetrieval.getAdditionalInformations() + "\t"
			// + attributeRetrieval.getCategory());
			List<SessionInterface> sessions = null;
			if (attributeRetrieval.getCategory() == Category.RESOURCE) {
				valid = true;
				sessions = getSessionManagerInterface().getSessionsForResourceAttributes(
						attributeRetrieval.getAdditionalInformations(), attributeRetrieval.getAttributeId());
			}
			if (attributeRetrieval.getCategory() == Category.SUBJECT) {
				valid = true;
				sessions = getSessionManagerInterface().getSessionsForSubjectAttributes(
						attributeRetrieval.getAdditionalInformations(), attributeRetrieval.getAttributeId());
			}
			if (attributeRetrieval.getCategory() == Category.ACTION) {
				valid = true;
				sessions = getSessionManagerInterface().getSessionsForActionAttributes(
						attributeRetrieval.getAdditionalInformations(), attributeRetrieval.getAttributeId());
			}
			if (attributeRetrieval.getCategory() == Category.ENVIRONMENT) {
				valid = true;
				sessions = getSessionManagerInterface()
						.getSessionsForEnvironmentAttributes(attributeRetrieval.getAttributeId());
			}
			if (valid) {
				return sessions;
			} else {
				LOGGER.log(Level.SEVERE, "Invalid attribute passed");
				return null;
			}
		}

		/**
		 * Removes the remote sessions from the list of interested sessions
		 *
		 * @param interestedSessions
		 *            the list of sessions interested by the changing of that
		 *            attribute
		 * @return the list of remote sessions, or an empty list if there are no
		 *         remote sessions, with the corresponding IPs in charge of
		 *         handling the reevaluation
		 */
		private HashMap<String, ArrayList<String>> removeRemoteSessions(List<SessionInterface> interestedSessions) {
			HashMap<String, ArrayList<String>> remoteSessions = new HashMap<>();
			for (int i = 0; i < interestedSessions.size();) {
				SessionInterface session = interestedSessions.get(i);
				// LOGGER.log(Level.INFO, session.getMyIP() + "\t" +
				// properties.getMyIP());

				/**
				 * Calling scheduler to know which will be the node in charge of
				 * handling this session
				 */
				PolicyHelper policyHelper = PolicyHelper.buildPolicyHelper(session.getPolicySet());

				List<Attribute> attributes = policyHelper.getAttributesForCondition(STARTACCESS_POLICY);
				HashMap<String, Integer> attributesIP = retrieveAttributesIp(attributes);
				/*
				 * String ip = scheduler.getIp(attributesIP); if
				 * (!ip.equals(getIp()) && !ip.equals("localhost")) { if
				 * (!remoteSessions.containsKey(ip)) { remoteSessions.put(ip,
				 * new ArrayList<>()); }
				 * remoteSessions.get(ip).add(interestedSessions.remove(i).getId
				 * ()); } else { i++; }
				 */
			}

			return remoteSessions;
		}

		/**
		 * Notifies the remote sessions interested by the remote attribute that
		 * that attribute has changed, hence it is necessary for them to
		 * re-evaluate the sessions. As parameter it will be passed only the
		 * number of the sessions related to the attribute that has changed
		 *
		 *
		 * @param remote
		 *            the remote sessions to be notified
		 */
		private void notifyRemote(HashMap<String, ArrayList<String>> remote) {
			if (remote != null && remote.size() > 0) {

				// way to send a single remote notify for each CH
				for (Map.Entry<String, ArrayList<String>> entry : remote.entrySet()) {
					String destination = entry.getKey();
					ReevaluationMessage reevaluationMessage = new ReevaluationMessage(getIp(), destination);
					reevaluationMessage.setSessionId(entry.getValue());
					getRequestManagerToChInterface().sendMessageToOutside(reevaluationMessage);
				}
			}

		}

		/**
		 * Reevaluates the request related to the session which attribute has
		 * changed.
		 * <p>
		 * Basically from each session it retrieves the original request and
		 * then asks to the PIPs to fill it with their attributes. Once the
		 * request is completed then it evaluates it in order to see which is
		 * the answer of the PDP.
		 * </p>
		 * After reevaluation of "local" sessions eventually notify the PEP of
		 * the changing happened, basically if the Request is not compliant with
		 * the policy anymore.
		 *
		 * @param session
		 *            the session to be reevaluated
		 * @return a String representing the answer of the PDP.
		 *
		 */
		private String reevaluateSession(SessionInterface session, Attribute attribute) {
			try {
				LOGGER.log(Level.INFO, "[TIME] reevaluation begins at " + System.currentTimeMillis());

				PolicyHelper policyHelper = PolicyHelper.buildPolicyHelper(session.getPolicySet());
				if (getSessionManagerInterface().checkSession(session.getId(),
						null) != iit.cnr.it.ucsinterface.sessionmanager.ReevaluationTableInterface.STATUS.IN_REEVALUATION) {
					getSessionManagerInterface().insertSession(session, attribute);
				} else {
					LOGGER.log(Level.INFO, "Session is already under evaluation");
					return null;
				}

				List<Attribute> attributes = policyHelper.getAttributesForCondition(STARTACCESS_POLICY);
				System.out.println("[TIME] reevaluation scheduler starts at " + System.currentTimeMillis());
				HashMap<String, Integer> attributesIP = retrieveAttributesIp(attributes);
				// String ip = scheduler.getIp(attributesIP);
				/*
				 * if (!ip.equals(getIp()) && !ip.equals("localhost") &&
				 * !ip.equals("UNAVAILABLE")) { ReevaluationMessage
				 * reevaluationMessage = new ReevaluationMessage(
				 * BasicConfiguration.getBasicConfiguration().getIp(), ip);
				 * reevaluationMessage.setSession((Session) session);
				 * schedule(reevaluationMessage, null, ip, STATUS.REEVALUATION);
				 * System.out.println("[TIME] reevaluation scheduler ends at " +
				 * System.currentTimeMillis()); return null; } else {
				 */
				ReevaluationMessage reevaluationMessage = new ReevaluationMessage(
						BasicConfiguration.getBasicConfiguration().getIp(),
						BasicConfiguration.getBasicConfiguration().getIp());
				reevaluationMessage.setSession((Session) session);
				LOGGER.log(Level.INFO, "[TIME] reevaluation starts at " + System.currentTimeMillis());
				reevaluate(reevaluationMessage);
				LOGGER.log(Level.INFO, "[TIME] reevaluation ends at " + System.currentTimeMillis());
				getSessionManagerInterface().stopSession(session);
				// }

			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Error in PIP retrieve");
				e.printStackTrace();
			}
			return null;
		}
	}

	@Override
	public synchronized void reevaluate(Message message) {
		// BEGIN parameter checking
		if (message == null || !(message instanceof ReevaluationMessage)) {
			LOGGER.log(Level.SEVERE, "Invalid message received for reevaluation");
			return;
		}
		// END parameter checking

		ReevaluationMessage reevaluationMessage = (ReevaluationMessage) message;

		SessionInterface session = reevaluationMessage.getSession();

		String request = reevaluationMessage.getSession().getOriginalRequest();
		PolicyHelper policyHelper = PolicyHelper.buildPolicyHelper(reevaluationMessage.getSession().getPolicySet());
		// make the request complete before reevaluation
		String requestFull = makeRequestFull(request, policyHelper.getAttributesForCondition(STARTACCESS_POLICY),
				STATUS.STARTACCESS, true);

		// perform the evaluation
		PDPEvaluation pdpEvaluation = getPdpInterface().evaluate(requestFull,
				policyHelper.getConditionForEvaluation(STARTACCESS_POLICY));

		// obligation
		getObligationManager().translateObligations(pdpEvaluation, reevaluationMessage.getSession().getId(),
				START_STATUS);

		// UXACMLPolicySet policySet = evaluate(request, uxacmlPol,
		// STARTACCESS_POLICY);

		System.out
				.println("[TIME] decision " + pdpEvaluation.getResponse() + " taken at " + System.currentTimeMillis());
		String destination;
		String[] uriSplitted = session.getPEPUri().split(PEP_ID_SEPARATOR);
		destination = session.getPEPUri().split(PEP_ID_SEPARATOR)[0];
		System.out.println("DESTINATION: " + destination + "\t" + session.getStatus());
		ReevaluationResponse chPepMessage = new ReevaluationResponse(getIp(), destination);
		pdpEvaluation.setSessionId(session.getId());
		chPepMessage.setPDPEvaluation((PDPResponse) pdpEvaluation);
		chPepMessage.setPepID(uriSplitted[uriSplitted.length - 1]);
		getSessionManagerInterface().stopSession(session);
		if ((session.getStatus().equals(START_STATUS) || session.getStatus().equals(TRY_STATUS))
				&& pdpEvaluation.getResponse().contains("Deny")) {
			LOGGER.log(Level.INFO, "[TIME] Sending revoke " + System.currentTimeMillis());
			getSessionManagerInterface().updateEntry(session.getId(), REVOKE_STATUS);
			getRequestManagerToChInterface().sendMessageToOutside(chPepMessage);
		}

		if (session.getStatus().equals(REVOKE_STATUS) && pdpEvaluation.getResponse().contains("Permit")) {
			LOGGER.log(Level.INFO, "[TIME] Sending resume " + System.currentTimeMillis());
			getSessionManagerInterface().updateEntry(session.getId(), START_STATUS);
			getRequestManagerToChInterface().sendMessageToOutside(chPepMessage);
		}
	}
}

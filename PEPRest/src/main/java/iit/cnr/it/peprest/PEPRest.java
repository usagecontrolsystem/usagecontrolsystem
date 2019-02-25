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
package iit.cnr.it.peprest;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;

import iit.cnr.it.peprest.configuration.Configuration;
import iit.cnr.it.peprest.configuration.PEPConf;
import iit.cnr.it.peprest.proxy.ProxyRequestManager;
import iit.cnr.it.ucsinterface.message.MEAN;
import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.endaccess.EndAccessMessage;
import iit.cnr.it.ucsinterface.message.endaccess.EndAccessResponse;
import iit.cnr.it.ucsinterface.message.reevaluation.ReevaluationResponse;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessMessage;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessResponse;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessMessage;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessMessageBuilder;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessResponse;
import iit.cnr.it.ucsinterface.pep.PEPInterface;
import iit.cnr.it.ucsinterface.requestmanager.RequestManagerToExternalInterface;
import iit.cnr.it.utility.Utility;

/**
 * This is the PEP using rest
 *
 * @author antonio
 *
 */
@Component
public class PEPRest implements PEPInterface, Runnable {

	protected static final Logger LOGGER = Logger.getLogger(PEPRest.class.getName());
	private static final String DENY = "Deny";
	private static final String PERMIT = "Permit";

	private Configuration configuration;
	private PEPConf pepConf;

	private RequestManagerToExternalInterface requestManager;

	// map of unanswered messages, the key is the id of the message
	@VisibleForTesting
	ConcurrentHashMap<String, Message> unanswered = new ConcurrentHashMap<>();

	@VisibleForTesting
	ConcurrentHashMap<String, Message> responses = new ConcurrentHashMap<>();

	private Object mutex = new Object();

	private volatile boolean initialized = false;

	public PEPRest() {
		configuration = Utility.retrieveConfiguration("conf.xml", Configuration.class);
		if ( configuration == null ) {
			LOGGER.log(Level.SEVERE, "Unable to load configuration.");
			return;
		}
		pepConf = configuration.getPepConf();
		requestManager = new ProxyRequestManager(configuration.getRMConf());
		initialized = true;
	}

	public String tryAccess() {
		//TODO: need a more efficient solution - begin block
		String request;
		String policy;
		int random = 0;
		if (random % 2 == 0) {
			policy = Utility.readFileAbsPath(pepConf.getPolicyPath());
			request = Utility.readFileAbsPath(pepConf.getRequestPath());
		} else {
			policy = Utility.readFileAbsPath(pepConf.getPolicyPath());
			request = Utility.readFileAbsPath(pepConf.getRequestPath());
		}
		//TODO: need a more efficient solution - end block

		TryAccessMessageBuilder tryAccessBuilder = new TryAccessMessageBuilder(configuration.getPepConf().getId(),
				configuration.getPepConf().getIp());
		tryAccessBuilder.setPepUri(buildOnGoingEvaluationInterface()).setPolicy(policy).setRequest(request);
		TryAccessMessage tryAccessMessage = tryAccessBuilder.build();
		tryAccessMessage.setCallback(buildResponseInterface("tryAccessResponse"), MEAN.REST);
		LOGGER.log(Level.INFO, "[TIME] TRYACCESS " + System.currentTimeMillis());
		Message message = requestManager.sendMessageToCH(tryAccessMessage);
		if (message.isDeliveredToDestination()) {
			unanswered.put(tryAccessMessage.getID(), tryAccessMessage);
			return tryAccessMessage.getID();
		} else {
			LOGGER.log(Level.WARNING,"isDeliveredToDestination: "+ message.isDeliveredToDestination());
			throw Throwables.propagate(new IllegalAccessException("Unable to deliver messsage to UCS"));
		}
	}

	public String startAccess(String sessionId) {
		StartAccessMessage startAccessMessage = new StartAccessMessage(configuration.getPepConf().getId(),
				configuration.getPepConf().getIp());
		startAccessMessage.setSessionId(sessionId);
		startAccessMessage.setCallback(buildResponseInterface("startAccessResponse"), MEAN.REST);
		try {
			LOGGER.log(Level.INFO, "[TIME] STARTACCESS " + System.currentTimeMillis());
			Message message = requestManager.sendMessageToCH(startAccessMessage);
			if (message.isDeliveredToDestination()) {
				unanswered.put(startAccessMessage.getID(), startAccessMessage);
				return startAccessMessage.getID();
			} else {
				LOGGER.log(Level.WARNING, "isDeliveredToDestination: "+ message.isDeliveredToDestination());
				throw Throwables.propagate(new IllegalAccessException("Unable to deliver messsage to UCS"));
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage());
			throw Throwables.propagate(e); 
		}
	}

	@VisibleForTesting
	public String endAccess(String sessionId) {
		EndAccessMessage endAccessMessage = new EndAccessMessage(configuration.getPepConf().getId(),
				configuration.getPepConf().getIp());
		endAccessMessage.setSessionId(sessionId);
		endAccessMessage.setCallback(buildResponseInterface("endAccessResponse"), MEAN.REST);
		try {
			System.out.println("[TIME] ENDACCESS " + System.currentTimeMillis());
			Message message = requestManager.sendMessageToCH(endAccessMessage);
			if (message.isDeliveredToDestination()) {
				unanswered.put(endAccessMessage.getID(), endAccessMessage);
				return endAccessMessage.getID();
			} else {
				LOGGER.log(Level.INFO, "isDeliveredToDestination: "+ message.isDeliveredToDestination());
				throw Throwables.propagate(new IllegalAccessException("Unable to deliver messsage to UCS"));
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage());
			throw Throwables.propagate(e); 
		}
	}

	@Override
	@Async
	public Message onGoingEvaluation(Message message) {
		// BEGIN parameter checking
		if (message == null || !(message instanceof ReevaluationResponse)) {
			LOGGER.log(Level.SEVERE, "Message not valid");
			throw Throwables.propagate(new IllegalArgumentException("Invalid message type'")); 
		}
		if (!initialized) {
			LOGGER.log(Level.SEVERE, "Cannot answer the message due to not properly initilization.");
			throw Throwables.propagate(new IllegalStateException("PEP is not properly initilization")); 
		}
		// END parameter checking
		responses.put(message.getID(), message);

		LOGGER.log(Level.INFO, "[TIME] ON_GOING_EVAL " + System.currentTimeMillis());

		ReevaluationResponse chPepMessage = (ReevaluationResponse) message;
		if ( pepConf.getRevoke().equals("HARD")) { 
			LOGGER.log(Level.INFO, "[TIME] sending endacces " + System.currentTimeMillis());
			EndAccessMessage endAccess = new EndAccessMessage(configuration.getPepConf().getId(),
					configuration.getPepConf().getIp());
			endAccess.setCallback(null, MEAN.REST);
			endAccess.setSessionId(chPepMessage.getPDPEvaluation().getSessionId());

			message = requestManager.sendMessageToCH(endAccess);
			if (message.isDeliveredToDestination()) {
				unanswered.put(message.getID(), message);
			}else {
				LOGGER.log(Level.INFO, "isDeliveredToDestination: "+ message.isDeliveredToDestination());
				return message; //TODO: perhaps an exception
			}
		} else {
			// generic to cater for multiple scenarios, e.g. pause/resume/pause/end etc...
			// TODO: How do you resume or stop?
			if (chPepMessage.getPDPEvaluation().getResponse().contains(PERMIT)) {
				LOGGER.log(Level.INFO, "RESUME EXECUTION");
			}
			if (chPepMessage.getPDPEvaluation().getResponse().contains(DENY)) {
				LOGGER.log(Level.INFO, "STOP EXECUTION");
			}
			//TODO: something should happen at the end, e.g. audit log, so we can assert for success
		}

		message.setMotivation("OK");
		return message;
	}

	@Async
	public void receiveResponseOld(Message message) {
		responses.put(message.getID(), message);
		synchronized (mutex) {
			mutex.notifyAll();
		}
	}

	@Override
	@Async
	public String receiveResponse(Message message) {
		try {
			responses.put(message.getID(), message);
			unanswered.remove(message.getID());
			return handleResponse(message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw Throwables.propagate(e);
		}
	}

	private String handleResponse(Message message) throws Exception {
		if(message instanceof TryAccessResponse) {
			return handleTryAccessResponse((TryAccessResponse) message);
		} else if(message instanceof StartAccessResponse) {
			return handleStartAccessResponse((StartAccessResponse) message);	
		} else if(message instanceof ReevaluationResponse) {
			return handleReevaluationResponse((ReevaluationResponse) message);
		} else if(message instanceof EndAccessResponse) {
			return handleEndAccessResponse((EndAccessResponse) message);
		} else {
			throw new Exception("INVALID MESSAGE: " + message.toString());
		}
	}

	private String handleTryAccessResponse(TryAccessResponse response) {
		LOGGER.info(response.getID() + " Evaluation " + response.getPDPEvaluation().getResponse());
		if(response.getPDPEvaluation().getResponse().contains("Permit")) {
			//TRIGGER STARTACCESS AUTOMATICALLY (?)
		}
		return response.getPDPEvaluation().getResponse();
	}

	private String handleStartAccessResponse(StartAccessResponse response) {
		LOGGER.info(response.getID() + " Evaluation " + response.getPDPEvaluation().getResponse());
		return response.getPDPEvaluation().getResponse();
	}

	private String handleReevaluationResponse(ReevaluationResponse response) {
		onGoingEvaluation(response);
		return response.getPDPEvaluation().getResponse();
	}

	private String handleEndAccessResponse(EndAccessResponse response) {
		LOGGER.info(response.getID() + " Evaluation " + response.getPDPEvaluation().getResponse());
		return response.getPDPEvaluation().getResponse();
	}
	

	@Override
	public void run() { //TODO: this method is for local demo tests and needs to be re-coded for PROD
		try {
			String id = tryAccess();
			TryAccessResponse tryAccessResponse = (TryAccessResponse) waitForResponse(id);
			LOGGER.log(Level.INFO, "[TIME] TRYACCESS END " + System.currentTimeMillis());
			if (tryAccessResponse.getPDPEvaluation().getResponse().contains(PERMIT)) {
				id = startAccess(tryAccessResponse.getSessionId());
				StartAccessResponse startAccessResponse = (StartAccessResponse) waitForResponse(id);
				LOGGER.log(Level.INFO, "[TIME] STARTACCESS END " + System.currentTimeMillis());
				if (startAccessResponse.getPDPEvaluation().getResponse().contains(PERMIT)) {
				} else {
					LOGGER.log(Level.SEVERE, "[TIME] STARTACCESS DENIED " + System.currentTimeMillis());
				}
			} else {
				LOGGER.log(Level.SEVERE, "[TIME] TRYACCESS DENIED " + System.currentTimeMillis());
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage());
		}
	}

	@VisibleForTesting
	Message waitForResponse(String id) throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		TaskBody taskBody = new TaskBody(id);
		return executor.submit(taskBody).get();
	}

	private class TaskBody implements Callable<Message> {

		private String id;

		public TaskBody(String id) {
			this.id = id;
		}

		@Override
		public Message call() {
			try {
				while (!responses.containsKey(id)) {
					LOGGER.log(Level.INFO, "First wait");
					synchronized (mutex) {
						mutex.wait();
					}
				}
				LOGGER.log(Level.INFO, "WAKE UP!");
				return responses.remove(id);
			} catch (InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.getLocalizedMessage());
				return null;
			}
		}
	}

	public ConcurrentHashMap<String, Message> getResponses() {
		return responses;
	}

	private final String buildResponseInterface(String name) {
		StringBuilder response = new StringBuilder();
		response.append("http://" + pepConf.getIp() + ":");
		response.append(pepConf.getPort() + "/");
		response.append(name);
		return response.toString();
	}

	private String buildOnGoingEvaluationInterface() {
		return buildResponseInterface(pepConf.getStatusChanged());
	}

	public void end(String sessionId) throws InterruptedException, ExecutionException {
		LOGGER.log(Level.INFO, "[TIME] Sending endAccess " + System.currentTimeMillis());
		String id = endAccess(sessionId);
		EndAccessResponse endAccessResponse = (EndAccessResponse) waitForResponse(id);
		endAccessResponse.getID();
		LOGGER.log(Level.INFO, "[TIME] END ACCESS RESPONSE: " + System.currentTimeMillis());
		//TODO: something should happen at the end, e.g. audit log, so we can assert that it succeeded
	}

	public ConcurrentHashMap<String, Message> getUnanswered() {
		return unanswered;
	}
}

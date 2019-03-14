package it.cnr.iit.pip.piprole;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.cnr.iit.ucsinterface.obligationmanager.ObligationInterface;
import it.cnr.iit.ucsinterface.pip.PIPBase;
import it.cnr.iit.ucsinterface.pip.exception.PIPException;
import it.cnr.iit.pip.piprole.table.DatabaseConfiguration;
import it.cnr.iit.pip.piprole.table.UserTable;
import it.cnr.iit.sqlmiddleware.SQLMiddleware;
import it.cnr.iit.sqlmiddlewareinterface.SQLMiddlewarePIPInterface;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLPip;
import it.cnr.iit.utility.JAXBUtility;
import it.cnr.iit.xacmlutilities.Attribute;
import it.cnr.iit.xacmlutilities.Category;
import it.cnr.iit.xacmlutilities.DataType;

import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

/**
 * This is the PIP in charge of reading the role of a useer from a database.
 * <p>
 * </p>
 * 
 * @author antonio
 *
 */
public class PIPRole extends PIPBase {
	
	/**
	 * Whenever a PIP has to retrieve some informations related to an attribute
	 * that is stored inside the request, it has to know in advance all the
	 * informations to retrieve that atrtribute. E.g. if this PIP has to retrieve
	 * the informations about the subject, it has to know in advance which is the
	 * attribute id qualifying the subject, its category and the datatype used,
	 * otherwise it is not able to retrieve the value of that attribute, hence it
	 * would not be able to communicate with the AM properly
	 */
	private Category													expectedCategory;
	
	private boolean														initialized		= false;
	
	private SQLMiddlewarePIPInterface					sqlMiddlewarePIPInterface;
	
	private static final Logger								LOGGER				= Logger
	    .getLogger(PIPRole.class.getName());
	
	private PSQLSubscriberTimer								subscriberTimer;
	
	// timer to be used to instantiate the subscriber timer
	private Timer															timer					= new Timer();
	
	// list that stores the attributes on which a subscribe has been performed
	protected final BlockingQueue<Attribute>	subscriptions	= new LinkedBlockingQueue<>();
	
	public PIPRole(String xmlPip) {
		super(xmlPip);
		if (!super.isInitialized()) {
			
		}
		if (initialize(xmlPip)) {
			subscriberTimer = new PSQLSubscriberTimer(contextHandlerInterface,
			    subscriptions, sqlMiddlewarePIPInterface);
			timer.scheduleAtFixedRate(subscriberTimer, 0, 10 * 1000);
		}
		
	}
	
	/**
	 * Initializes the various fields of this PIP
	 * 
	 * @param xmlPip
	 *          the xml configuration
	 * @return true if everything goes fine, false otherwise
	 */
	private boolean initialize(String xmlPip) {
		try {
			XMLPip xml = JAXBUtility.unmarshalToObject(XMLPip.class, xmlPip);
			Map<String, String> arguments = xml.getAttributes().get(0).getArgs();
			Attribute attribute = new Attribute();
			if (!attribute.createAttributeId(arguments.get(ATTRIBUTE_ID))) {
				LOGGER.log(Level.SEVERE, "[PIPReader] wrong set Attribute");
				return false;
			}
			if (!attribute
			    .setCategory(Category.toCATEGORY(arguments.get(CATEGORY)))) {
				LOGGER.log(Level.SEVERE,
				    "[PIPReader] wrong set category " + arguments.get(CATEGORY));
				return false;
			}
			if (!attribute.setAttributeDataType(
			    DataType.toDATATYPE(arguments.get(DATA_TYPE)))) {
				LOGGER.log(Level.SEVERE, "[PIPReader] wrong set datatype");
				return false;
			}
			if (attribute.getCategory() != Category.ENVIRONMENT) {
				if (!setExpectedCategory(arguments.get(EXPECTED_CATEGORY))) {
					return false;
				}
			}
			DatabaseConfiguration configurationInterface = DatabaseConfiguration
			    .createDBConfiguration(xml);
			configurationInterface.setClass(UserTable.class);
			sqlMiddlewarePIPInterface = SQLMiddleware
			    .createMiddleware(configurationInterface);
			if (sqlMiddlewarePIPInterface != null) {
				System.out.println("CORRECT INIT");
				addAttribute(attribute);
				initialized = true;
				return true;
			} else {
				System.out.println("WRONG INIT");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public void subscribe(RequestType accessRequest) throws PIPException {
		// BEGIN parameter checking
		if (accessRequest == null || !initialized || !isInitialized()) {
			LOGGER.log(Level.SEVERE, "[PIPREader] wrong initialization" + initialized
			    + "\t" + isInitialized());
			return;
		}
		// END parameter checking
		
		subscriberTimer.setContextHandlerInterface(contextHandlerInterface);
		
		if (subscriberTimer.getContextHandler() == null
		    || contextHandlerInterface == null) {
			LOGGER.log(Level.SEVERE, "Context handler not set");
			return;
		}
		
		// create the new attribute
		Attribute attribute = getAttributes().get(0);
		
		String value;
		String filter;
		// read the value of the attribute, if necessary extract the additional info
		if (attribute.getCategory() == Category.ENVIRONMENT) {
			value = read();
		} else {
			filter = accessRequest.extractValue(expectedCategory);
			attribute.setAdditionalInformations(filter);
			value = read(filter);
		}
		if (value != null && value.length() > 0) {
			attribute.setValue(attribute.getAttributeDataType(), value);
			
			// add the attribute to the access request
			accessRequest.addAttribute(attribute.getCategory().toString(),
			    attribute.getAttributeDataType().toString(),
			    attribute.getAttributeId(), value);
			
			// add the attribute to the subscription list
			if (!subscriptions.contains(attribute)) {
				subscriptions.add(attribute);
			}
		}
		
	}
	
	@Override
	public void retrieve(RequestType accessRequest) throws PIPException {
		// BEGIN parameter checking
		if (accessRequest == null || !initialized || !isInitialized()) {
			LOGGER.log(Level.SEVERE, "[PIPREader] wrong initialization" + initialized
			    + "\t" + isInitialized());
			return;
		}
		// END parameter checking
		
		String value;
		Attribute attribute = getAttributes().get(0);
		
		if (getAttributes().get(0).getCategory() == Category.ENVIRONMENT) {
			value = read();
		} else {
			String filter = accessRequest.extractValue(expectedCategory);
			value = read(filter);
		}
		
		accessRequest.addAttribute(attribute.getCategory().toString(),
		    attribute.getAttributeDataType().toString(), attribute.getAttributeId(),
		    value);
	}
	
	@Override
	public boolean unsubscribe(List<Attribute> attributes) throws PIPException {
		// BEGIN parameter checking
		if (attributes == null || !initialized || !isInitialized()) {
			LOGGER.log(Level.SEVERE, "[PIPREader] wrong initialization" + initialized
			    + "\t" + isInitialized());
			return false;
		}
		// END parameter checking
		
		for (Attribute attribute : attributes) {
			if (attribute.getAttributeId().equals(getAttributeIds().get(0))) {
				for (Attribute attributeS : subscriptions) {
					if (attributeS.getAdditionalInformations()
					    .equals(attribute.getAdditionalInformations())) {
						subscriptions.remove(attributeS);
						LOGGER.info("UNSUB " + subscriptions.size());
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public String retrieve(Attribute attributeRetrievals) throws PIPException {
		String value;
		
		if (getAttributes().get(0).getCategory() == Category.ENVIRONMENT) {
			value = read();
		} else {
			String filter = attributeRetrievals.getAdditionalInformations();
			value = read(filter);
		}
		return value;
	}
	
	@Override
	public String subscribe(Attribute attributeRetrieval) throws PIPException {
		subscriberTimer.setContextHandlerInterface(contextHandlerInterface);
		
		if (subscriberTimer.getContextHandler() == null
		    || contextHandlerInterface == null) {
			LOGGER.log(Level.SEVERE, "Context handler not set");
			return null;
		}
		
		String value;
		if (getAttributes().get(0).getCategory() == Category.ENVIRONMENT) {
			value = read();
		} else {
			String filter = attributeRetrieval.getAdditionalInformations();
			value = read(filter);
		}
		if (value != null && value.length() > 0) {
			attributeRetrieval.setValue(getAttributes().get(0).getAttributeDataType(),
			    value);
			if (!subscriptions.contains(attributeRetrieval)) {
				subscriptions.add(attributeRetrieval);
			}
		}
		return value;
	}
	
	@Override
	public void retrieve(RequestType request,
	    List<Attribute> attributeRetrievals) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void subscribe(RequestType request,
	    List<Attribute> attributeRetrieval) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void updateAttribute(String json) throws PIPException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void performObligation(ObligationInterface obligation) {
		// TODO Auto-generated method stub
		
	}
	
	final private boolean setExpectedCategory(String category) {
		// BEGIN parameter checking
		if (!isInitialized() || category == null || category.isEmpty()) {
			initialized = false;
			return false;
		}
		// END parameter checking
		Category categoryObj = Category.toCATEGORY(category);
		if (categoryObj == null) {
			initialized = false;
			return false;
		}
		this.expectedCategory = categoryObj;
		return true;
	}
	
	private String read(String filter) {
		String condition = "where username = '" + filter + "'";
		UserTable userTable = sqlMiddlewarePIPInterface
		    .performQuerySingleRecord("UserTable", condition, UserTable.class);
		if (userTable == null) {
			return "";
		}
		String role = userTable.getRole();
		System.out.println("RETRIEVED: " + role);
		return role;
	}
	
	private String read() {
		UserTable userTable = sqlMiddlewarePIPInterface
		    .performQuerySingleRecord("UserTable", "", UserTable.class);
		return userTable.getRole();
	}
	
}

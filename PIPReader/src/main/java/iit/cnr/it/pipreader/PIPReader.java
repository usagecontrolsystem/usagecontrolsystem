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
package iit.cnr.it.pipreader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import iit.cnr.it.ucsinterface.obligationmanager.ObligationInterface;
import iit.cnr.it.ucsinterface.pip.PIPBase;
import iit.cnr.it.ucsinterface.pip.exception.PIPException;
import iit.cnr.it.utility.Utility;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLPip;
import it.cnr.iit.xacmlutilities.Attribute;
import it.cnr.iit.xacmlutilities.Category;
import it.cnr.iit.xacmlutilities.DataType;
import it.cnr.iit.xacmlutilities.policy.utility.JAXBUtility;
import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

/**
 * This is a PIPReader.
 * <p>
 * It is the first NEW PIP designed from the structure stated in the
 * UCSInterface project. The only task this PIP will perform is to read some
 * informations from a file. The Path to reach the file is passed as parameter
 * to the pip. <br>
 * <b>This attributeID has a single value</b>
 * </p>
 * 
 * @author antonio
 *
 */
final public class PIPReader extends PIPBase {
	
	// ---------------------------------------------------------------------------
	// Class attributes
	// ---------------------------------------------------------------------------
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
	
	// this is the attribute manager of this pip
	private String														filePath;
	
	// states if the pip has been correctly initialized
	public volatile boolean									initialized		= false;
	
	// list that stores the attributes on which a subscribe has been performed
	protected final BlockingQueue<Attribute>	subscriptions	= new LinkedBlockingQueue<>();
	
	// logger
	private Logger														LOGGER				= Logger
	    .getLogger(PIPReader.class.getName());
	
	// the subscriber timer in charge of performing the polling of the values
	private PRSubscriberTimer									subscriberTimer;
	// timer to be used to instantiate the subscriber timer
	private Timer															timer					= new Timer();
	
	// ---------------------------------------------------------------------------
	// CONSTRUCTOR
	// ---------------------------------------------------------------------------
	/**
	 * Constructor for the PIP reader
	 * 
	 * @param xmlPip
	 *          the xml describing the pipreader in string format
	 */
	public PIPReader(String xmlPip) {
		super(xmlPip);
		if (!isInitialized()) {
			return;
		}
		if (initialize(xmlPip)) {
			initialized = true;
			subscriberTimer = new PRSubscriberTimer(contextHandlerInterface,
			    subscriptions, filePath);
			timer.scheduleAtFixedRate(subscriberTimer, 0, 10 * 1000);
		} else {
			;
		}
	}
	
	/**
	 * Performs the effective initialization of the PIP.
	 * 
	 * @param xmlPip
	 *          the xml of the pip in string format
	 * @return true if everything goes ok, false otherwise
	 */
	private boolean initialize(String string) {
		try {
			XMLPip xmlPip = JAXBUtility.unmarshalToObject(XMLPip.class, string);
			Map<String, String> arguments = xmlPip.getAttributes().get(0).getArgs();
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
			addAttribute(attribute);
			if (!setFilePath(arguments.get(FILE_PATH))) {
				LOGGER.log(Level.SEVERE, "[PIPReader] wrong set file");
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	// ---------------------------------------------------------------------------
	// FUNCTIONS
	// ---------------------------------------------------------------------------
	/**
	 * Performs the retrieve operation.
	 * <p>
	 * The retrieve operation is a very basic operation in which the PIP simply
	 * asks to the AttributeManager the value in which it is interested into. Once
	 * that value has been retrieved, the PIP will fatten the request.
	 * </p>
	 * 
	 * @param accessRequest
	 *          this is an in/out parameter
	 */
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
		
		if (getAttributes().get(0).getCategory() == Category.ENVIRONMENT) {
			value = read();
		} else {
			String filter = accessRequest.extractValue(expectedCategory);
			value = read(filter);
		}
		
		accessRequest.addAttribute(getAttributes().get(0).getCategory().toString(),
		    getAttributes().get(0).getAttributeDataType().toString(),
		    getAttributes().get(0).getAttributeId(), value);
		
	}
	
	/**
	 * Performs the subscribe operation. This operation is very similar to the
	 * retrieve operation. The only difference is that in this case we have to
	 * signal to the thread in charge of performing the polling that it has to
	 * poll a new attribute
	 * 
	 * @param accessRequest
	 *          IN/OUT parameter
	 */
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
		attribute.setValue(attribute.getAttributeDataType(), value);
		
		// add the attribute to the access request
		accessRequest.addAttribute(getAttributes().get(0).getCategory().toString(),
		    getAttributes().get(0).getAttributeDataType().toString(),
		    getAttributes().get(0).getAttributeId(), value);
		
		// add the attribute to the subscription list
		if (!subscriptions.contains(attribute)) {
			subscriptions.add(attribute);
		}
		
	}
	
	@Override
	public void updateAttribute(String json) throws PIPException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Checks if it has to remove an attribute (the one passed in the list) from
	 * the list of subscribed attributes
	 * 
	 * @param attributes
	 *          the list of attributes that must be unsubscribed
	 */
	@Override
	public void unsubscribe(List<Attribute> attributes) throws PIPException {
		// BEGIN parameter checking
		if (attributes == null || !initialized || !isInitialized()) {
			LOGGER.log(Level.SEVERE, "[PIPREader] wrong initialization" + initialized
			    + "\t" + isInitialized());
			return;
		}
		// END parameter checking
		
		for (Attribute attribute : attributes) {
			if (attribute.getAttributeId().equals(getAttributeIds().get(0))) {
				for (Attribute attributeS : subscriptions) {
					if (attributeS.getAdditionalInformations()
					    .equals(attribute.getAdditionalInformations())) {
						subscriptions.remove(attributeS);
						System.out.println("UNSUB " + subscriptions.size());
						return;
					}
				}
			}
		}
	}
	
	/**
	 * This is the function called by the context handler whenever we have a
	 * remote retrieve request
	 */
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
	
	/**
	 * This is the function called by the context handler whenever we have a
	 * remote retrieve request
	 */
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
		attributeRetrieval.setValue(getAttributes().get(0).getAttributeDataType(),
		    value);
		if (!subscriptions.contains(attributeRetrieval)) {
			subscriptions.add(attributeRetrieval);
		}
		return value;
		
	}
	
	@Override
	public void retrieve(RequestType request,
	    List<Attribute> attributeRetrievals) {
		LOGGER.log(Level.SEVERE, "Wrong method called");
		return;
		
	}
	
	@Override
	public void subscribe(RequestType request,
	    List<Attribute> attributeRetrieval) {
		LOGGER.log(Level.SEVERE, "Wrong method called");
		return;
		
	}
	
	@Override
	public void performObligation(ObligationInterface obligation) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Effective retrieval of the monitored value, before this retrieval many
	 * checks may have to be performed
	 * 
	 * @return the requested string
	 * @throws PIPException
	 */
	private String read() throws PIPException {
		return Utility.readFileAbsPath(filePath);
	}
	
	/**
	 * Reads the file looking for the line containing the filter we are passing as
	 * argument and the role stated as other parameter
	 * 
	 * <br>
	 * NOTE we suppose that in the file each line has the following structure:
	 * filter\tattribute.
	 * 
	 * @param filter
	 *          the string to be used to search for the item we're interested into
	 * @param role
	 *          the role of the string
	 * @return the string or null
	 * 
	 * 
	 * @throws PIPException
	 */
	private String read(String filter) throws PIPException {
		try (			
			Scanner fileInputStream = new Scanner(new File(filePath));
		){
			// BufferedInputStream fileInputStream = new BufferedInputStream(
			// new FileInputStream(new File("/home/antonio/temperature.txt")));
			String line = "";
			while (fileInputStream.hasNextLine()) {
				String tmp = fileInputStream.nextLine();
				if (tmp.contains(filter)) {
					line = tmp;
					break;
				}
			}
			// LOGGER.log(Level.INFO,
			// "[PIPReader] value read is " + line.split("\t")[1]);
			return line.split("\t")[1];
		} catch (IOException ioException) {
			throw new PIPException(ioException.getMessage());
		}
	}
	
	// ---------------------------------------------------------------------------
	// SETTERS
	// ---------------------------------------------------------------------------
	
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
	
	final private boolean setFilePath(String filePath) {
		// BEGIN parameter checking
		if (!isInitialized() || filePath == null || filePath.isEmpty()) {
			LOGGER.log(Level.SEVERE, "Wrong init: " + filePath + "\t" + initialized);
			initialized = false;
			
			return false;
		}
		// END parameter checking
		String absFilePath = Utility.findFileAbsPathUsingClassLoader(filePath);
		if (absFilePath != null){
			this.filePath = absFilePath;
		}else{
			this.filePath = filePath;
			
		}
		LOGGER.info("FilePath: " + this.filePath);
		return true;
	}
	
}

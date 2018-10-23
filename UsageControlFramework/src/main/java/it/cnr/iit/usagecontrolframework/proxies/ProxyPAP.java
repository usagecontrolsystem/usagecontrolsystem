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
package it.cnr.iit.usagecontrolframework.proxies;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import iit.cnr.it.ucsinterface.constants.CONNECTION;
import iit.cnr.it.ucsinterface.pap.PAPInterface;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLPap;

/**
 * This is the proxy to deal with the PAP.
 * <p>
 * The core of this class is basically the same of the ProxyPDP. Also here we
 * have to manage all the possible ways in which we can communicate with the
 * PAP. <br>
 * For now the only implemented way of communication is the local one, hence the
 * ContextHandler can call the PAP via API. <br>
 * TODO: Since it is possible not to have a PAP we have to implement also that
 * feature
 * </p>
 * 
 * @author antonio
 *
 */
final public class ProxyPAP extends Proxy implements PAPInterface {
	// interface to deal with the PAP (if the PAP runs locally)
	PAPInterface					papInterface;
	// volatile variable to avoid wrong initialization
	private boolean				initialized	= false;
	// logger object
	private final Logger	LOGGER			= Logger
	    .getLogger(ProxyPAP.class.getName());
	private String				configuration;
	
	/**
	 * This is the constructor of the proxy to the PAP.
	 * <p>
	 * Basically here we have to distinguish between the various possibilities we
	 * have to implement the pap and act accordingli. There are 3 different
	 * options:
	 * <ol>
	 * <li>API: in this case the PAP is in the same virtual machine of the
	 * UCS</li>
	 * <li>REST_API: in this case the PAP can be queried using REST_API</li>
	 * <li>SOCKET: in this case the PAP can be queried by passing to it messages
	 * via socket</li>
	 * </ol>
	 * </p>
	 * 
	 * @param xmlPap
	 *          the configuration of the PAP
	 */
	public ProxyPAP(XMLPap xmlPap) {
		
		// BEGIN parameter checking
		if (xmlPap == null) {
			return;
		}
		configuration = xmlPap.getCommunication();
		if (configuration == null) {
			return;
		}
		// END parameter checking
		
		CONNECTION connection = CONNECTION.getCONNECTION(xmlPap.getCommunication());
		switch (connection) {
			case API:
				if (localPAP(xmlPap)) {
					initialized = true;
				}
				break;
			case SOCKET:
				if (connectSocket(xmlPap)) {
					initialized = true;
				}
				break;
			case REST_API:
				if (connectRest(xmlPap)) {
					initialized = true;
				}
				break;
			default:
				LOGGER.log(Level.SEVERE,
				    "WRONG communication " + xmlPap.getCommunication());
				return;
		}
	}
	
	/**
	 * This is the implementation of the local PAP.
	 * 
	 * @param xmlPap
	 *          the configuration of the PAP in xml format
	 * @return true if everything goes ok, false otherwise
	 */
	private boolean localPAP(XMLPap xmlPap) {
		String className = xmlPap.getClassName();
		// BEGIN parameter checking
		if (className == null || className.equals("")) {
			return false;
		}
		// END parameter checking
		
		try {
			Constructor<?> constructor = Class.forName(className)
			    .getConstructor(XMLPap.class);
			papInterface = (PAPInterface) constructor.newInstance(xmlPap);
			return true;
		} catch (InstantiationException | IllegalAccessException
		    | ClassNotFoundException | NoSuchMethodException | SecurityException
		    | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * TODO
	 * 
	 * @param xmlPap
	 * @return
	 */
	private boolean connectSocket(XMLPap xmlPap) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * TODO
	 * 
	 * @param xmlPap
	 * @return
	 */
	private boolean connectRest(XMLPap xmlPap) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String retrievePolicy(String policyId) {
		// BEGIN parameter checking
		if (initialized != true) {
			return null;
		}
		if (policyId == null || policyId.equals("")) {
			return null;
		}
		// END parameter checking
		CONNECTION connection = CONNECTION.getCONNECTION(configuration);
		switch (connection) {
			case API:
				return papInterface.retrievePolicy(policyId);
			case SOCKET:
				return null;
			case REST_API:
				return null;
		}
		return null;
	}
	
	@Override
	public boolean addPolicy(String policy) {
		// BEGIN parameter checking
		if (initialized != true) {
			return false;
		}
		if (policy == null || policy.equals("")) {
			return false;
		}
		// END parameter checking
		CONNECTION connection = CONNECTION.getCONNECTION(configuration);
		switch (connection) {
			case API:
				return papInterface.addPolicy(policy);
			case SOCKET:
				return false;
			case REST_API:
				return false;
		}
		return false;
	}
	
	@Override
	public List<String> listPolicies() {
		// BEGIN parameter checking
		if (initialized != true) {
			return null;
		}
		// END parameter checking
		CONNECTION connection = CONNECTION.getCONNECTION(configuration);
		switch (connection) {
			case API:
				return papInterface.listPolicies();
			case SOCKET:
				return null;
			case REST_API:
				return null;
		}
		return null;
	}
	
	/**
	 * Checks if the proxy was correctly initialized
	 * 
	 * @return the value of the initialized volatile variable
	 */
	public boolean isInitialized() {
		return initialized;
	}
	
	@Override
	public boolean ping() {
		if (initialized) {
			LOGGER.log(Level.INFO, "PAPProxy correctly configured");
			return true;
		} else {
			LOGGER.log(Level.SEVERE, "PAPProxy wrongly configured");
			return false;
		}
	}
	
}

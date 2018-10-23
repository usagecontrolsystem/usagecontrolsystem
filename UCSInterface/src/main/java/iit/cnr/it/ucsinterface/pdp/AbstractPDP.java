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
package iit.cnr.it.ucsinterface.pdp;

import iit.cnr.it.ucsinterface.obligationmanager.ObligationManagerInterface;
import iit.cnr.it.ucsinterface.pap.PAPInterface;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLPdp;

/**
 * This is the abstract class providing a schema for the PDP.
 * 
 * <p>
 * The PDP requires an interface to deal with the obligation manager and another
 * to deal with the PAP. <br>
 * Also in this case, following the CERT guidelines, we will implement this
 * class using a volatile variable in charge of saying if the object has been
 * correctly created or not.
 * </p>
 * 
 * @author antonio
 *
 */
public abstract class AbstractPDP implements PDPInterface {
	// the interface to the obligation manager
	private ObligationManagerInterface	obligationManagerInterface;
	// the interface to the pap
	private PAPInterface								papInterface;
	// variable in charge of storing the status of this class.
	private volatile boolean						initialized		= false;
	// configuration of the pdp
	private XMLPdp											configuration	= null;
	
	/**
	 * The constructor for the abstrasct class is empty
	 */
	public AbstractPDP(XMLPdp configuration) {
		// BEGIN parameter checking
		if (configuration == null)
			return;
		// END parameter checking
		this.configuration = configuration;
	}
	
	// ---------------------------------------------------------------------------
	// GETTER and SETTERS
	// ---------------------------------------------------------------------------
	final protected ObligationManagerInterface getObligationManager() {
		if (initialized == true) {
			return obligationManagerInterface;
		}
		return null;
	}
	
	final public void setObligationManagerInterface(
	    ObligationManagerInterface obligationManagerInterface) {
		// BEGIN parameter checking
		if (obligationManagerInterface == null) {
			return;
		}
		// END parameter checking
		this.obligationManagerInterface = obligationManagerInterface;
		verify();
	}
	
	final protected PAPInterface getPAPInterface() {
		if (initialized == true) {
			return papInterface;
		}
		return null;
	}
	
	final public void setPAPInterface(PAPInterface papInterface) {
		// BEGIN parameter checking
		if (papInterface == null) {
			return;
		}
		// END parameter checking
		this.papInterface = papInterface;
		verify();
	}
	
	/**
	 * This function is in charge of verifying that the object is consistent. If
	 * the object is consistent the initialized flag will be updated
	 */
	private void verify() {
		if (obligationManagerInterface != null && papInterface != null
		    && configuration != null) {
			initialized = true;
		}
	}
	
	/**
	 * Checks if the object has been correctly initialized
	 * 
	 * @return true if the object has been correctly initialized, false otherwise
	 */
	final public boolean isInitialized() {
		return initialized;
	}
	
}

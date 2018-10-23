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
package iit.cnr.it.ucsinterface.pip;

import iit.cnr.it.ucsinterface.obligationmanager.ObligationInterface;
import iit.cnr.it.ucsinterface.pip.exception.PIPException;

/**
 * This is the interface offered by the PIP to the obligation manager
 * <p>
 * The obligation manager, even if is a component that can be considered inside
 * the ContextHandler, has an interface to force the PIPs to update the
 * attribute they're monitoring
 * </p>
 * 
 * @author antonio
 *
 */
public interface PIPOMInterface extends PIPBaseInterface {
	/**
	 * Updates attribute values for a certain user in the LDAP server
	 * 
	 * @param json
	 *          a JSON string containing the user and his updated attributes
	 * @throws PIPException
	 */
	public void updateAttribute(String json) throws PIPException;
	
	/**
	 * Function to be used by the ObligationManager when it needs to ask the PIP
	 * to perform an obligation. The obliagtion is usually written in xml or json,
	 * hence this function will accept a String defining the obligation to be
	 * performed.
	 * 
	 * @param obligation
	 *          the obligation in json format
	 */
	public void performObligation(ObligationInterface obligation);
}

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
package iit.cnr.it.ucsinterface.obligationmanager;

/**
 * This is the general interface of an obligation
 * 
 * @author antonio
 *
 */
public interface ObligationInterface {
	
	/**
	 * Retrieves the attributeId to which this obligation is referred
	 * 
	 * @return the string representing the attributeid
	 */
	public String getAttributeId();
	
	/**
	 * Sets the sessionId in which this obligation is being performed, this may be
	 * necessary to distinguish between the various sessions
	 * 
	 * @param sessionId
	 *          the id of the session
	 */
	public void setSessionId(String sessionId);
	
	/**
	 * Sets the step of the evaluation in which the obligation has to be performed
	 * 
	 * @param step
	 *          the step
	 */
	public void setStep(String step);
	
}

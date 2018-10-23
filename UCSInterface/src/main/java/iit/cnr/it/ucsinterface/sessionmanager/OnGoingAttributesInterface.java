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
package iit.cnr.it.ucsinterface.sessionmanager;

/**
 * This is the interface the ContextHandler and the SessionManager use to
 * communicate informations about ongoing attributes.
 * 
 * @author antonio
 *
 */
public interface OnGoingAttributesInterface {
	
	/**
	 * Retrieves the id of the OnGoingAttribute
	 * 
	 * @return a String representing the ongoingAttribute
	 */
	public String getId();
	
	/**
	 * Retrieves the session id to which the on going attribute is related
	 * 
	 * @return the session id to which the on going attribute is related
	 */
	public String getSessionId();
	
	/**
	 * Retrieves the attribute id of the on going attribute
	 * 
	 * @return the attribute id of the on going attribute
	 */
	public String getAttributeId();
	
	/**
	 * Retrieves the name of the subject to which the attribute is related <br>
	 * 
	 * @return the name of the subject if the attribute is related to subject,
	 *         null otherwise
	 */
	public String getSubjectName();
	
	/**
	 * Retrieves the name of the resource to which the attribute is related
	 * 
	 * @return the name of the resource if the attribute is related to object,
	 *         null otherwise
	 */
	public String getResourceName();
	
	/**
	 * Retrieves the name of the action to which the attribute is related
	 * 
	 * @return the name of the action if the attribute is related to action, null
	 *         otherwise
	 */
	public String getActionName();
}

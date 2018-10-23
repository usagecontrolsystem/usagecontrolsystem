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
package iit.cnr.it.ucsinterface.contexthandler;

import iit.cnr.it.ucsinterface.message.Message;

/**
 * This is the interface provided by the ContextHandler.
 * 
 * <p>
 * The contexthandler allows other parties to communicate with it using these
 * functions::
 * <ol>
 * <li>tryAccess: function called when we want to ask the contexthandler if it
 * is possible for a certain subject to perform a certain action upon a certain
 * resource, it will be called before the effective access to the resource</li>
 * <li>startAccess: function called when the subject effectively tries to access
 * the resource, at first we check if it is still possible for the subject to
 * perform the operation, then we demand the various PIPs the continuous
 * monitoring of the environment in order to notify the CH of any bad event that
 * may happen</li>
 * <li>endAccess: this function can be called autonomously by the PEP or can be
 * triggered by the CH when something bad happens. Whenever this function will
 * be called, the session will be terminated. The request will be evaluated
 * again looking for eventual obligations to be performed.</li>
 * <li>attributeChanged: this function is called by the PIPBarrierMonitor
 * whenever a PIP notifies it that an attribute has changed. It triggers the
 * reevaluation of all the sessions that take into account that particular
 * attribute.</li>
 * <li>messageForPIP: this function is triggered from the RequestManager
 * whenever there is a message that asks something from a PIP. The
 * ContextHandler is in fact the only entity in hcarge of allowing the various
 * PIPs to communicate with the external world</li>
 * </ol>
 * </p>
 * 
 * @author antonio
 *
 */
public interface ContextHandlerInterface extends ContextHandlerPIPInterface {
	
	/**
	 * This is the tryAccess function. In this case the RequestManager passes to
	 * the ContextHandler a message in which it has been specified the request
	 * received, the policy to be used and the uri of the PEP. It will also send
	 * eventual Obligations provided by the ObligationManager
	 * 
	 * @param message
	 *          message received by the RequestManager
	 * @return a message stating the response of the PDP to the request.
	 */
	public void tryAccess(Message message);
	
	/**
	 * This is the startAccess function. In this case a tryAccess has already been
	 * performed with a PERMIT status. Hence with this function the ContextHandler
	 * performs a re-evaluation of the context in order to be sure that the access
	 * can go on, moreover this triggers the contiunous monitoring of the
	 * attributes of the policy.
	 * 
	 * @param message
	 *          message received by the RequestManager
	 * @return a message stating the response of the PDP to the request
	 * @throws Exception
	 */
	public void startAccess(Message message) throws Exception;
	
	/**
	 * This is the endAcces function. In this case the PEP doesn't require the
	 * resource anymore or the access to the resource has been denied, hence the
	 * PEP notifies the CH to stop monitoring that session. The CH answers by
	 * sending to the PEP the response to the ENDAccess. It will be the
	 * ObligationManager in charge of
	 * 
	 * @param message
	 *          the message received by the RequestManager
	 * @return a message stating the response of the PDP to the request
	 */
	public void endAccess(Message message);
	
	/**
	 * This is the reevaluation function. This function is triggered by a remote
	 * node querying the actual ContextHandler to perform the reevaluation of a
	 * certain session. This is an API offered by a ContextHandler to another.
	 * 
	 * @param message
	 *          the reevaluation message
	 */
	public void reevaluate(Message message);
	
	/**
	 * This is the messageForPIP function.
	 * <p>
	 * In this function fall all the function related to requests performed by
	 * remote UCS to local PIP {remote and local with respect to the CH taken into
	 * consideration}. In this case the request will be sent to the various PIP
	 * and the response will be sent back as soon as available.
	 * </p>
	 * 
	 * @param message
	 *          the message received by the RequestManager
	 * @return a message stating the response of the CH to the request
	 */
	public Message messageForPIP(Message message);
	
	public static final String	TRY_STATUS		= "t";
	public static final String	START_STATUS	= "s";
	public static final String	REVOKE_STATUS	= "r";
	public static final String	END_STATUS		= "e";
	
}

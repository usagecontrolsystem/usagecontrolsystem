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
package iit.cnr.it.ucsinterface.requestmanager;

import iit.cnr.it.ucsinterface.message.Message;

/**
 * This is the interface provided by the request manager to the external world.
 * 
 * <p>
 * This interface will be used from the external world to send messages to the
 * context handler. Basically each external entity will send a message to the
 * Request manager which will then forwards the request to the context handler.
 * </p>
 * 
 * @author antonio
 *
 */
public interface RequestManagerToExternalInterface {
	
	/**
	 * Sends a message from the external world to the context handler
	 * 
	 * @param message
	 *          the message to be sent
	 * @return the response of the ContextHandler
	 */
	public Message sendMessageToCH(Message message);
	
}

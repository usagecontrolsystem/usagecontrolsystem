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
package iit.cnr.it.ucsinterface.forwardingqueue;

import iit.cnr.it.ucsinterface.message.Message;

/**
 * This is the interface offered by the ForwardingQueue to the RequestManager.
 * <p>
 * This interface is the dual of {@link ForwardingQueueToCHInterface}. Via this
 * interface, in fact, the RequestManager, using the ID of the response message
 * it has received, is able to identify which was the original source of certain
 * message. In case the message source was not swapped then no results will be
 * returned.
 * </p>
 * 
 * @author testucs
 *
 */
public interface ForwardingQueueToRMInterface {

  /**
   * Retrieves the original source of a message using the ID of the response to
   * that message.
   * 
   * @param messageID
   *          the ID of the message, as we have decided, the ID of a message is
   *          the same in the request and in the response
   * @return the original message, null if the messageID is not present in the
   *         queue
   */
  public Message getOriginalSource(String messageID);
}

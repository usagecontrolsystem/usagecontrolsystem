/*******************************************************************************
 * Copyright 2018 IIT-CNR
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package it.cnr.iit.ucsinterface.forwardingqueue;

import it.cnr.iit.ucsinterface.message.Message;

/**
 * This is the interface offered by the ForwardingQueue to the ContextHandler.
 * <p>
 * The ContextHandler is the only component allowed to put informations inside
 * the forwarding queue. This is an interface with a single function.
 * </p>
 *
 * @author testucs
 *
 */
public interface ForwardingQueueToCHInterface {
    /**
     * This is the only function provided by this interface.
     * <p>
     * Basically with this function the ContextHandler inserts in the
     * ForwardingQueue the ID of the message and the original Source, in this way,
     * when the response arrives, they will be able to understand which was the
     * original source of the message by looking at the MessageID that MUST be
     * unique.
     * </p>
     *
     * @param messageId
     *          the id of the message we want to insert inside this queue
     * @param originalMessage
     *          the original message from which we can extract useful infos
     */
    public void addSwappedMessage( String messageId, Message originalMessage );
}

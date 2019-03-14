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

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import it.cnr.iit.ucsinterface.message.Message;

/**
 * This is the ForwardingQueue object.
 * <p>
 * The ForwardingQueue is a shared object between the ContextHandler and the
 * RequestManager. The ContextHandler exploits the ForwardingQueue to keep track
 * of messages that have been forwarded to other nodes, in order to signal it to
 * the RequestManager. This components, once a response has arrived, has to
 * check if the response was due to a message that was forwarded. In this case
 * it simply has to forward the message to the original source. <br>
 * This components implements the {@link ForwardingQueueToCHInterface} in order
 * to let the ContextHandler to communicate with it and the
 * {@link ForwardingQueueToRMInterface} in order to let the RequestManager to
 * communicate with it.
 * </p>
 *
 * @author testucs
 *
 */
public class ForwardingQueue
        implements ForwardingQueueToCHInterface, ForwardingQueueToRMInterface {
    private static final Logger LOGGER = Logger.getLogger( ForwardingQueue.class.getName() );

    private ConcurrentHashMap<String, Message> forwardingMap = new ConcurrentHashMap<>();

    @Override
    public Message getOriginalSource( String messageID ) {
        // BEGIN parameter checking
        if( messageID == null || messageID.isEmpty() ) {
            LOGGER.severe( "MessageID" + messageID + " is not valid" );
            return null;
        }
        // END parameter checking
        return forwardingMap.remove( messageID );
    }

    @Override
    public void addSwappedMessage( String messageID, Message originalMessage ) {
        // BEGIN parameter checking
        if( messageID == null || messageID.isEmpty() ) {
            LOGGER.severe( "MessageID" + messageID + " is not valid" );
        }
        if( originalMessage == null ) {
            LOGGER.severe( "Source" + originalMessage + " is not valid" );
        }
        // END parameter checking
        forwardingMap.put( messageID, originalMessage );
    }

}

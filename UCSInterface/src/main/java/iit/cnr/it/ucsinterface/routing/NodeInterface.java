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
package iit.cnr.it.ucsinterface.routing;

import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.requestmanager.RequestManagerToExternalInterface;

/**
 * This is the interface that every routing schema has to implement in order to
 * allow a message to be sent from an UCS to another.
 * <p>
 * UCSs may exchange different types of messages and also the network topology
 * may change depending on the environment. For sure the most simple case is the
 * one of single-hop networks, but we can have different network topology and
 * also heterogeneous networks.
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @deprecated
 * @author antonio
 *
 */
public interface NodeInterface {
    /**
     * This is the only API this interface has to provide.
     * <p>
     * Basically this is the way in which an UCS sends a message to another UCS.
     * Since everything is asynchronous the UCS sends the message and then leaves a
     * thread in charge of waiting for the response to arrive. The response will be
     * sent from the other UCS using this same api. <br>
     * Each message will be identified by its ID that we assume to be unique among
     * the communication link.
     * </p>
     * 
     * @param message
     *          the message we want to send. In this case we basically wrap the
     *          message we want to send, after having converted it in a json String
     *          into a container message.
     */
    public void sendMessage( Message message );

    /**
     * This is the interface offered by the request manager to the external word.
     * 
     * @return true if everything goes ok, false otherwise
     */
    public boolean setRequestManangerInterface( RequestManagerToExternalInterface requestManager );
}

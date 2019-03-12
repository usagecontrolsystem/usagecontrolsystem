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
package iit.cnr.it.ucsinterface.node;

import iit.cnr.it.ucsinterface.message.Message;

/**
 * This interface is used to make it possible to have the various nodes
 * communicate with each other.
 * 
 * <p>
 * Basically this is an interface offered by each node to every other node, the
 * requestManager stores a list of nodes interface. When it has to send a
 * message to a particular node it simply has to call the proxy asking to it to
 * send the message to the node in the proper way. Thus the proxy acts like a
 * translator, making it easier to build various communication strategies.
 * </p>
 * 
 * @author antonio
 *
 */
public interface NodeInterface {

    public final static String TRYACCESS_REST = "/tryAccess";
    public final static String STARTACCESS_REST = "/startAccess";
    public final static String ENDACCESS_REST = "/endAccess";
    public final static String ONGOING_REST = "/onGoing";
    public final static String TRYACCESSRESPONSE_REST = "/tryAccessResponse";
    public final static String STARTACCESSRESPONSE_REST = "/startAccessResponse";
    public final static String ENDACCESSRESPONSE_REST = "/endAccessResponse";
    public final static String ONGOINGRESPONSE_REST = "/onGoingResponse";

    /**
     * Sends a message to the other nodes. Since we want the architecture to be as
     * modular as possible, this function might be implemented by a proxy, so that
     * it would be possible to forward a message using sockets, restAPI or similar
     * without having to change the implementation of the other modules.
     * 
     * @param message
     *          the message to be sent.
     */
    public void sendMessage( Message message );
}

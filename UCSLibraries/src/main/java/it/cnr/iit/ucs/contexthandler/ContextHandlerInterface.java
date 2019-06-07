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
package it.cnr.iit.ucs.contexthandler;

import it.cnr.iit.ucs.exceptions.StatusException;
import it.cnr.iit.ucs.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucs.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucs.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucs.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessResponse;

/**
 * This is the interface provided by the ContextHandler.
 *
 * @author Antonio La Marra
 */
public interface ContextHandlerInterface extends ContextHandlerPIPInterface {

    /**
     * Initial evaluation of the policy, request.
     *
     * @param message
     *          message received
     * @return a message stating the response of the PDP to the request.
     */
    public TryAccessResponse tryAccess( TryAccessMessage message );

    /**
     * Starts the session spefified in the message if the previous tryAccess
     * has been already performed with a PERMIT status.
     *
     * @param message
     *          message received
     * @return a message stating the response of the PDP to the request
     * @throws Exception
     */
    public StartAccessResponse startAccess( StartAccessMessage message ) throws Exception;

    /**
     * Ends the session spefified in the message.
     *
     * @param message
     *          the message received by the RequestManager
     * @return a message stating the response of the PDP to the request
     * @throws StatusException
     */
    public EndAccessResponse endAccess( EndAccessMessage message ) throws StatusException;

}

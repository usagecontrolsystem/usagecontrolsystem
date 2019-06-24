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

import it.cnr.iit.ucs.exceptions.PolicyException;
import it.cnr.iit.ucs.exceptions.RequestException;
import it.cnr.iit.ucs.exceptions.SessionManagerException;
import it.cnr.iit.ucs.exceptions.StatusException;
import it.cnr.iit.ucs.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucs.message.endaccess.EndAccessResponseMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessResponseMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessResponseMessage;

/**
 * This is the interface provided by the ContextHandler.
 *
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
 * ContextHandler is in fact the only entity in charge of allowing the various
 * PIPs to communicate with the external world</li>
 * </ol>
 *
 * @author Antonio La Marra
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
     * @throws PolicyException
     * @throws RequestException
     */
    public TryAccessResponseMessage tryAccess( TryAccessMessage message ) throws PolicyException, RequestException;

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
     * @throws SessionManagerException
     * @throws StatusException
     * @throws PolicyException
     * @throws RequestException
     * @throws Exception
     */
    public StartAccessResponseMessage startAccess( StartAccessMessage message )
            throws StatusException, PolicyException, RequestException;

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
     * @throws StatusException
     * @throws RequestException
     * @throws PolicyException
     */
    public EndAccessResponseMessage endAccess( EndAccessMessage message ) throws StatusException, RequestException, PolicyException;

}

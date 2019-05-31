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
package it.cnr.iit.ucs.ucs;

import it.cnr.iit.ucs.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessMessage;

/**
 * This interface is meant to be used by external nodes or PEPs wanting to communicate with the UCS.
 * tryAccess: in order to allow an external component to ask for a tryAccess evaluation
 * startAccess: in order to allow an external component to ask for a startAccess evaluation
 * endAccess: in order to allow an external component to ask for a endAccess evaluation
 *
 * @author Antonio La Marra
 */
public interface UCSInterface {

    /**
     * Request of a tryAccess evaluation.
     * The tryAccess is the first operation required by a PEP before starting the
     * effective action on a certain resource. It asks (in general) for evaluation
     * of immutable attributes, i.e., attributes that do not change their value very
     * often. In this implementation this function can be called either by a PEP
     * directly connected to this UCS, either by a Node that has scheduled the
     * execution of a tryAccess directed to it to this node.
     *
     * @param tryAccessMessage
     *          the tryAccessMessage object, it contains all the informations
     *          required for the recipient to call back the caller and all the
     *          informations to correctly evaluate the request.
     */
    public void tryAccess( TryAccessMessage tryAccessMessage );

    /**
     * Request of a startAccess evaluation.
     * The startAccess is the second operation the PEP performs. It does it before
     * performing the action in order to notify the UCS that the action is started
     * and to query the UCS if it still has the permission to perform the action. In
     * general in this case also mutable attributes are taken into account. This
     * operation triggers also the monitoring of the attributes used to evaluate the
     * policy in order to react if something changes. This operation might also be
     * asked from the scheduler of another UCS, exactly as it happens in tryAccess.
     *
     * @param startAccessMessage
     *          the startAccess message object. It contains all the informations
     *          required for the recipient to call back the caller, in general in
     *          this message the caller puts only the sessionID that has to be
     *          evaluated
     */
    public void startAccess( StartAccessMessage startAccessMessage );

    /**
     * Request of an endAccess evaluation.
     * The endAccess is the last operation the PEP performs. It does it in order to
     * notify the UCS that its session has finished. There are two reasons for
     * ending a session:
     * <ol>
     * <li>the PEP has performed the operation and does not need the resource
     * anymore</li>
     * <li>one of the mutable attributes present in the policy to be evaluated for
     * the startAccess has changed its value and the PEP does not have the rights to
     * use the resource anymore</li>
     * </ol>
     * In any case when the PEP sends an endAccess is to notify the end of a session
     * and this allows the UCS to free some resources. This operation might also be
     * asked from the scheduler of another UCS, exactly as it happens in tryAccess
     * and startAccess.
     *
     * @param endAccessMessage
     *          the message containing the informations required to perform the
     *          endAccess
     */
    public void endAccess( EndAccessMessage endAccessMessage );

}

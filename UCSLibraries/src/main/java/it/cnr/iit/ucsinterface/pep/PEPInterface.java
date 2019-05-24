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
package it.cnr.iit.ucsinterface.pep;

import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationResponse;

/**
 * Interface offered to the UCS by the PEP.
 *
 * <p>
 * The PEP offers a single function to the UCS, the one that allows the UCS to
 * ask for a revoke of a certain session
 * </p>
 *
 * @author antonio
 *
 */
public interface PEPInterface {
    /**
     * In the version in which the PEP has also the capability of performing
     * onResume, this function will be used by the UCS any time the
     * onGoingEvaluation changes
     *
     * @param message
     *          the message containing the informations required by the PEP
     * @return a message
     */
    public Message onGoingEvaluation( ReevaluationResponse message );

    /**
     * In the asynchronous case the PEP has also to offer a way for the ucs to
     * communicate with it. Hence the PEP will also have a function to be used by
     * the UCS for sending responses. In the case in which the PEP is local to the
     * context handler, this will be considered a callback function (to be put
     * inside the message sent to the context handler), or it can be a REST API.
     */
    public String receiveResponse( Message message );
}

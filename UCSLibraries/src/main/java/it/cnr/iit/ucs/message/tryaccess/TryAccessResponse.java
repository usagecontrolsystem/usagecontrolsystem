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
package it.cnr.iit.ucs.message.tryaccess;

import it.cnr.iit.ucs.constants.ENTITIES;
import it.cnr.iit.ucs.constants.PURPOSE;
import it.cnr.iit.ucs.message.EvaluatedResponse;
import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.ucs.pdp.PDPEvaluation;

/**
 * This is the tryAccess response message
 *
 * @author Antonio La Marra, Alessandro Rosetti
 */
public final class TryAccessResponse extends Message implements EvaluatedResponse {

    private static final long serialVersionUID = 1L;

    private PDPEvaluation pdpEvaluation;

    private String sessionId;

    public TryAccessResponse() {
        super( ENTITIES.CH.toString(), ENTITIES.PEP.toString() );
        purpose = PURPOSE.TRY_RESPONSE;
    }

    public TryAccessResponse( String messageId ) {
        super( ENTITIES.CH.toString(), ENTITIES.PEP.toString(), messageId );
        purpose = PURPOSE.TRY_RESPONSE;
    }

    public TryAccessResponse( String source, String dest, String messageId ) {
        super( source, dest, messageId );
    }

    @Override
    public void setPDPEvaluation( PDPEvaluation pdpEvaluation ) {
        this.pdpEvaluation = pdpEvaluation;
    }

    @Override
    public PDPEvaluation getPDPEvaluation() {
        return pdpEvaluation;
    }

    public void setSessionId( String sessionId ) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

}

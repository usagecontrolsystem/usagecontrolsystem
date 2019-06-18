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
package it.cnr.iit.ucs.message.reevaluation;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.cnr.iit.ucs.constants.ENTITIES;
import it.cnr.iit.ucs.constants.PURPOSE;
import it.cnr.iit.ucs.message.EvaluatedMessage;
import it.cnr.iit.ucs.message.IdentifiedMessage;
import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.ucs.pdp.PDPEvaluation;
import it.cnr.iit.utility.errorhandling.Reject;

/**
 * Structure of the message that the CH sends to the PEP in case of reevaluation
 *
 * @author Antonio La Marra, Alessandro Rosetti
 */
public class ReevaluationResponseMessage extends Message implements EvaluatedMessage, IdentifiedMessage {

    @JsonProperty
    private PDPEvaluation evaluation;

    private String pepId;
    String sessionId;

    public ReevaluationResponseMessage( String source, String destination ) {
        super( source, destination );
        purpose = PURPOSE.REEVALUATION_RESPONSE;
    }

    public ReevaluationResponseMessage( String source, String destination, String messageId ) {
        super( source, destination, messageId );
        purpose = PURPOSE.REEVALUATION_RESPONSE;
    }

    public ReevaluationResponseMessage() {
        super( ENTITIES.CH.toString(), ENTITIES.PEP.toString() );
        purpose = PURPOSE.REEVALUATION_RESPONSE;
    }

    public ReevaluationResponseMessage( String id ) {
        super( ENTITIES.CH.toString(), ENTITIES.PEP.toString(), id );
        purpose = PURPOSE.REEVALUATION_RESPONSE;
    }

    @Override
    public void setEvaluation( PDPEvaluation evaluation ) {
        Reject.ifNull( evaluation );
        this.evaluation = evaluation;
    }

    @Override
    public PDPEvaluation getEvaluation() {
        return evaluation;
    }

    public void setPepId( String pepId ) {
        this.pepId = pepId;
    }

    public String getPepId() {
        return pepId;
    }

    @Override
    public void setSessionId( String sessionId ) {
        this.sessionId = sessionId;

    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

}

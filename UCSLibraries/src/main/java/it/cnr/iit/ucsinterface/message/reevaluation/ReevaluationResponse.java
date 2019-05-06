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
package it.cnr.iit.ucsinterface.message.reevaluation;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.PART;
import it.cnr.iit.ucsinterface.message.PURPOSE;
import it.cnr.iit.ucsinterface.pdp.PDPEvaluation;
import it.cnr.iit.utility.errorhandling.Reject;

/**
 * Structure of the message that the CH sends to the PEP in case of reevaluation
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public class ReevaluationResponse extends Message {

    private static final long serialVersionUID = 1L;

    @JsonProperty
    private PDPEvaluation pdpEvaluation;

    private String pepId;

    /**
     * Constructor for the message sent from the context handler to the pep
     *
     * @param source
     *          source of the message
     * @param destination
     *          destination of the message
     */
    public ReevaluationResponse( String source, String destination ) {
        super( source, destination );
        purpose = PURPOSE.REEVALUATION_RESPONSE;
    }

    /**
     * Constructor for the message sent from the context handler to the pep
     *
     * @param source
     *          source of the message
     * @param destination
     *          destination of the message
     * @param messageId
     *          the id to identify this message
     *
     */
    public ReevaluationResponse( String source, String destination, String messageId ) {
        super( source, destination, messageId );
        purpose = PURPOSE.REEVALUATION_RESPONSE;
    }

    public ReevaluationResponse() {
        super( PART.CH.toString(), PART.PEP.toString() );
        purpose = PURPOSE.REEVALUATION_RESPONSE;
    }

    public ReevaluationResponse( String id ) {
        super( PART.CH.toString(), PART.PEP.toString(), id );
        purpose = PURPOSE.REEVALUATION_RESPONSE;
    }

    public void setPDPEvaluation( PDPEvaluation pdpEvaluation ) {
        Reject.ifNull( pdpEvaluation );
        this.pdpEvaluation = pdpEvaluation;
    }

    public PDPEvaluation getPDPEvaluation() {
        return pdpEvaluation;
    }

    public void setPepId( String pepId ) {
        this.pepId = pepId;
    }

    public String getPepId() {
        return pepId;
    }
}

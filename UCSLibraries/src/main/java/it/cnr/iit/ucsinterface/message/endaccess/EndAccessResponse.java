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
package it.cnr.iit.ucsinterface.message.endaccess;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.PART;
import it.cnr.iit.ucsinterface.message.PURPOSE;
import it.cnr.iit.ucsinterface.pdp.PDPEvaluation;

/**
 * This is the response provided by the UCS once it receives an endAccess
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public class EndAccessResponse extends Message {

    private static final long serialVersionUID = 1L;

    @JsonProperty
    private PDPEvaluation pdpEvaluation;

    public EndAccessResponse() {
        super( PART.CH.toString(), PART.PEP.toString() );
        purpose = PURPOSE.ENDACCESS_RESPONSE;
    }

    /**
     * Constructor for an EndAccessResponse
     *
     * @param source      source of the message
     * @param destination destination of the message
     * @param messageId          id of the message
     */
    public EndAccessResponse( String source, String destination, String messageId ) {
        super( source, destination, messageId );
        purpose = PURPOSE.ENDACCESS_RESPONSE;
    }

    /**
     * Constructor for an EndAccessResponse
     *
     * @param messageId the id of the message
     */
    public EndAccessResponse( String messageId ) {
        super( PART.CH.toString(), PART.PEP.toString(), messageId );
        purpose = PURPOSE.ENDACCESS_RESPONSE;
    }

    public PDPEvaluation getPDPEvaluation() {
        return pdpEvaluation;
    }

    public void setPDPEvaluation( PDPEvaluation pdpEvaluation ) {
        this.pdpEvaluation = pdpEvaluation;
    }

}

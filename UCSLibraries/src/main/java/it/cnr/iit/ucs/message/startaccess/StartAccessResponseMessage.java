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
package it.cnr.iit.ucs.message.startaccess;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.cnr.iit.ucs.constants.ENTITIES;
import it.cnr.iit.ucs.constants.PURPOSE;
import it.cnr.iit.ucs.message.EvaluatedMessage;
import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.ucs.pdp.PDPEvaluation;

/**
 * This is the structure of a message used to response to a start access
 *
 * @author Antonio La Marra, Alessandro Rosetti
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public final class StartAccessResponseMessage extends Message implements EvaluatedMessage {

    private static final long serialVersionUID = 1L;

    @JsonProperty
    private PDPEvaluation evaluation;

    public StartAccessResponseMessage() {
        super();
        purpose = PURPOSE.START_RESPONSE;
    }

    public StartAccessResponseMessage( String source, String destination, String messageId ) {
        super( source, destination, messageId );
        purpose = PURPOSE.START_RESPONSE;
    }

    public StartAccessResponseMessage( String messageId ) {
        super( ENTITIES.CH.toString(), ENTITIES.PEP.toString(), messageId );
        purpose = PURPOSE.START_RESPONSE;
    }

    @Override
    public PDPEvaluation getEvaluation() {
        return evaluation;
    }

    @Override
    public void setEvaluation( PDPEvaluation pdpEvaluation ) {
        this.evaluation = pdpEvaluation;
    }

}

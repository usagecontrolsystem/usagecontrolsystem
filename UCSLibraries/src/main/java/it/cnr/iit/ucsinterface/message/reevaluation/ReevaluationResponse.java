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

import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.PART;
import it.cnr.iit.ucsinterface.message.PURPOSE;
import it.cnr.iit.ucsinterface.pdp.PDPEvaluation;

/**
 * Structure of the message that the CH sends to the PEP in case of reevaluation
 *
 * @author antonio
 *
 */
public class ReevaluationResponse extends Message {

    private static final Logger log = Logger.getLogger( ReevaluationResponse.class.getName() );

    private static final long serialVersionUID = 1L;
    // flag that states the status of this class
    private volatile boolean chPepInitialized = false;

    // the PDP evaluation
    @JsonProperty
    private PDPEvaluation pdpEvaluation;

    private String pepID;

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
        if( isInitialized() ) {
            chPepInitialized = true;
            purpose = PURPOSE.REEVALUATION_RESPONSE;
        }
    }

    /**
     * Constructor for the message sent from the context handler to the pep
     *
     * @param source
     *          source of the message
     * @param destination
     *          destination of the message
     * @param id
     *          the id to identify this message
     *
     */
    public ReevaluationResponse( String source, String destination, String id ) {
        super( source, destination, id );
        if( isInitialized() ) {
            purpose = PURPOSE.REEVALUATION_RESPONSE;
            chPepInitialized = true;
        }
    }

    /**
     * Constructor for a ChPepMessage
     */
    public ReevaluationResponse() {
        super( PART.CH.toString(), PART.PEP.toString() );
        if( isInitialized() ) {
            purpose = PURPOSE.REEVALUATION_RESPONSE;
            chPepInitialized = true;
        }
    }

    public ReevaluationResponse( String id ) {
        super( PART.CH.toString(), PART.PEP.toString(), id );
        if( isInitialized() ) {
            purpose = PURPOSE.REEVALUATION_RESPONSE;
            chPepInitialized = true;
        }
    }

    @Override
    public int compareTo( Message o ) {
        return 0;
    }

    public void setPDPEvaluation( PDPEvaluation pdpEvaluation ) {
        if( !chPepInitialized || pdpEvaluation == null ) {
            log.severe( "Impossible to set the evaluation " + chPepInitialized + "\t" + ( pdpEvaluation == null ) );
            return;
        }
        this.pdpEvaluation = pdpEvaluation;
    }

    public PDPEvaluation getPDPEvaluation() {
        if( !chPepInitialized ) {
            return null;
        }
        return pdpEvaluation;
    }

    public void setPepID( String pepId ) {
        pepID = pepId;
    }

    public String getPepID() {
        return pepID;
    }
}

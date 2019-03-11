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
package iit.cnr.it.ucsinterface.message.startaccess;

import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.PART;
import iit.cnr.it.ucsinterface.message.PURPOSE;
import iit.cnr.it.ucsinterface.pdp.PDPEvaluation;

/**
 * This is the structure of a message used to response to a start access
 *
 * @author antonio
 *
 */
public final class StartAccessResponse extends Message {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    // status of the startaccess
    private String status;
    // the evaluation provided by the PDP
    private PDPEvaluation pdpEvaluation;

    public StartAccessResponse() {
        super();
        purpose = PURPOSE.STARTACCESS_RESPONSE;
    }

    /**
     * Constructor of the StartAccessResponse
     *
     * @param source      the source
     * @param destination the destination
     * @param id          the id of the message
     */
    public StartAccessResponse( String source, String destination, String id ) {
        super( source, destination, id );
        purpose = PURPOSE.STARTACCESS_RESPONSE;
    }

    /**
     * Constructor of the StartAccessResponse
     *
     * @param id the id of the response
     */
    public StartAccessResponse( String id ) {
        super( PART.CH.toString(), PART.PEP.toString(), id );
        purpose = PURPOSE.STARTACCESS_RESPONSE;
    }

    // ---------------------------------------------------------------------------
    // GETTERS and SETTERS
    // ---------------------------------------------------------------------------
    public boolean setStatus( String status ) {
        // BEGIN parameter checking
        if( status == null || status.isEmpty() ) {
            return false;
        }
        // END parameter checking
        this.status = status;
        return true;
    }

    public String getStatus() {
        return status;
    }

    public PDPEvaluation getPDPEvaluation() {
        return pdpEvaluation;
    }

    @Override
    public int compareTo( Message o ) {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean setResponse( PDPEvaluation pdpEvaluation ) {
        // BEGIN parameter checking
        if( pdpEvaluation == null ) {
            return false;
        }
        // END parameter checking
        this.pdpEvaluation = pdpEvaluation;
        return false;
    }

}

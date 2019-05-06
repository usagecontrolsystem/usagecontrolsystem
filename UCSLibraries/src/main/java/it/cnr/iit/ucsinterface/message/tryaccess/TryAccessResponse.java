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
package it.cnr.iit.ucsinterface.message.tryaccess;

import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.PART;
import it.cnr.iit.ucsinterface.message.PURPOSE;
import it.cnr.iit.ucsinterface.pdp.PDPEvaluation;

/**
 * This is the tryAccess response message
 * <p>
 * This message will be sent from the ContextHandler to the PEP to tell to it
 * which is the exit of the tryaccess it has performed
 * </p>
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public final class TryAccessResponse extends Message {

    private static final long serialVersionUID = 1L;

    private PDPEvaluation pdpEvaluation;

    private String sessionId;

    public TryAccessResponse() {
        super( PART.CH.toString(), PART.PEP.toString() );
        purpose = PURPOSE.TRYACCESS_RESPONSE;
    }

    public TryAccessResponse( String messageId ) {
        super( PART.CH.toString(), PART.PEP.toString(), messageId );
        purpose = PURPOSE.TRYACCESS_RESPONSE;
    }

    public TryAccessResponse( String source, String dest, String messageId ) {
        super( source, dest, messageId );
    }

    public void setPDPEvaluation( PDPEvaluation pdpEvaluation ) {
        this.pdpEvaluation = pdpEvaluation;
    }

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

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
import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.utility.errorhandling.Reject;

/**
 * This is a tryAccess message.
 *
 * @author Antonio La Marra, Alessandro Rosetti
 */
public final class TryAccessMessage extends Message {

    private String pepUri;
    private String policyId;
    private String policy;
    private String request;

    public TryAccessMessage( String source, String destination ) {
        super( source, destination );
        purpose = PURPOSE.TRY;
    }

    public TryAccessMessage() {
        super( ENTITIES.PEP.toString(), ENTITIES.CH.toString() );
        purpose = PURPOSE.TRY;
    }

    public void setPepUri( String pepUri ) {
        Reject.ifBlank( pepUri );
        this.pepUri = pepUri;
    }

    public String getPepUri() {
        return pepUri;
    }

    public void setRequest( String request ) {
        Reject.ifBlank( request );
        this.request = request;
    }

    public String getRequest() {
        return request;
    }

    public void setPolicy( String policy ) {
        Reject.ifBlank( policy );
        this.policy = policy;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicyId( String policyId ) {
        this.policyId = policyId;
    }

    public String getPolicyId() {
        return policyId;
    }
}

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
import it.cnr.iit.utility.JAXBUtility;
import it.cnr.iit.utility.errorhandling.Reject;

import oasis.names.tc.xacml.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

/**
 * This is the whole object that arrives as a message to the tryaccess.
 *
 * @author Antonio La Marra, Alessandro Rosetti
 */
public final class TryAccessMessage extends Message {

    private static final long serialVersionUID = 1L;

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
        try {
            JAXBUtility.unmarshalToObject( RequestType.class, request );
        } catch( Exception exception ) {
            throw new IllegalStateException( exception.getLocalizedMessage() );
        }
        this.request = request;
    }

    public String getRequest() {
        return request;
    }

    public void setPolicy( String policy ) {
        try {
            JAXBUtility.unmarshalToObject( PolicyType.class, policy );
        } catch( Exception exception ) {
            throw new IllegalStateException( exception.getLocalizedMessage() );
        }
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

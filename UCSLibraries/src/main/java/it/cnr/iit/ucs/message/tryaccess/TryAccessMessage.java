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

import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.ucs.message.PART;
import it.cnr.iit.ucs.message.PURPOSE;
import it.cnr.iit.utility.JAXBUtility;
import it.cnr.iit.utility.errorhandling.Reject;

import oasis.names.tc.xacml.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

/**
 * This is the whole object that arrives as a message to the tryaccess.
 *
 * <p>
 * We assume that the tryaccess message wraps in a single point many different
 * aspects:
 * <ol>
 * <li>The uri of the pep in string format</li>
 * <li>The uxacml policy in the xml format converted as a string</li>
 * <li>The uxacml request in the xml format converted as a string</li>
 * </ol>
 * All this parts are wrapped inside the motivation that is the access point for
 * all theses parts.
 * </p>
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public final class TryAccessMessage extends Message {

    private static final long serialVersionUID = 1L;

    private String pepUri;
    private String policyId;
    private String policy;
    private String request;

    /**
     * Constructor for a TryAccessMessage
     *
     * @param source
     *          the source of the message
     * @param destination
     *          the destination of the message
     */
    public TryAccessMessage( String source, String destination ) {
        super( source, destination );
        purpose = PURPOSE.TRYACCESS;
    }

    public TryAccessMessage() {
        super( PART.PEP.toString(), PART.CH.toString() );
        purpose = PURPOSE.TRYACCESS;
    }

    public boolean setPepUri( String pepUri ) {
        Reject.ifBlank( pepUri );
        this.pepUri = pepUri;
        return true;
    }

    public String getPepUri() {
        return pepUri;
    }

    /**
     * Set the request to be sent to the UCS
     *
     * @param request
     *          a string representing the request
     * @return true if everything goes fine, false otherwise
     */
    public boolean setRequest( String request ) {
        Reject.ifBlank( request );
        try {
            JAXBUtility.unmarshalToObject( RequestType.class, request );
            this.request = request;
            return true;
        } catch( Exception exception ) {
            return false;
        }
    }

    public String getRequest() {
        return request;
    }

    /**
     * Set the policy to be used, in this case instead of having the id of the
     * policy we have the real policy.
     *
     * @param policy
     *          the policy to be used
     * @return true if everything goes fine, false otherwise
     */
    public void setPolicy( String policy ) {
        try {
            JAXBUtility.unmarshalToObject( PolicyType.class, policy );
            this.policy = policy;
        } catch( Exception exception ) {}
    }

    public String getPolicy() {
        return policy;
    }

    public boolean setPolicyId( String policyId ) {
        this.policyId = policyId;
        return true;
    }

    public String getPolicyId() {
        return policyId;
    }
}

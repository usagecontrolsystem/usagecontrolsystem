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

import java.util.logging.Logger;

import it.cnr.iit.utility.JAXBUtility;

import oasis.names.tc.xacml.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

/**
 *
 * This class represents the content of a tryAccess message.
 * <p>
 * The content part has been splitted into 4 strings:
 * <ol>
 * <li>for the uri of the pep</li>
 * <li>for the uxacml Policy or alternatively the id of the policy</li>
 * <li>for the uxacml request</li>
 * </ol>
 * </p>
 * {"sessionId":"2995bc40-4601-11e7-9378-bd4308375c7a","isInitialized":true,"source":"PEP","destination":"CH","purpose":"STARTACCESS","initialized":true}
 *
 * @author antonio
 *
 */
public final class TryAccessMessageContent {
    private static final Logger LOGGER = Logger.getLogger( TryAccessMessageContent.class.getName() );

    // the uri of the pep
    private String pepUri;

    // the id of the policy
    private String policyId;

    // the policy the pep wants to use
    private String policy;

    // the request of the policy
    private String request;

    public TryAccessMessageContent() {

    }

    /**
     * Set the uri of the pep
     *
     * @param pepUri
     *          the uri of the pep
     * @return true if everything goes fine, false otherwise
     */
    public boolean setPepUri( String pepUri ) {
        // BEGIN parameter checking
        if( pepUri == null || pepUri.isEmpty() ) {
            LOGGER.warning( "pep uri is not valid " + pepUri );
            return false;
        }
        // END parameter checking
        this.pepUri = pepUri;
        return true;
    }

    /**
     * Sets the id of the policy to be used
     *
     * @param policyId
     *          the id of the policy to be used
     * @return true if everything goes fine, false otherwise
     */
    public boolean setPolicyId( String policyId ) {
        // BEGIN parameter checking
        if( pepUri == null || pepUri.isEmpty() ) {
            LOGGER.warning( "policyId is not valid " + policyId );
            return false;
        }
        // END parameter checking
        this.policyId = policyId;
        return true;
    }

    /**
     * Set the policy to be used, in this case instead of having the id of the
     * policy we have the real policy.
     *
     * @param policy
     *          the policy to be used
     * @return true if everything goes fine, false otherwise
     */
    public boolean setPolicy( String policy ) {
        // BEGIN parameter checking
        if( policy == null || policy.equals( "" ) ) {
            return false;
        }
        // END parameter checking
        try {
            JAXBUtility.unmarshalToObject( PolicyType.class, policy );
            this.policy = policy;
            return true;
        } catch( Exception exception ) {
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Set the request to be sent to the UCS
     *
     * @param request
     *          a string representing the request
     * @return true if everything goes fine, false otherwise
     */
    public boolean setRequest( String request ) {
        // BEGIN parameter checking
        if( request == null || request.isEmpty() ) {
            LOGGER.warning( "Ivalid request " + request );
            return false;
        }
        // END parameter checking
        try {
            JAXBUtility.unmarshalToObject( RequestType.class, request );
            this.request = request;
            return true;
        } catch( Exception exception ) {
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Converts the policy string into the correspondent type which is more
     * manageable
     *
     * @return the PolicyType object corresponding to the Policy passed as string
     */
    public String getPolicy() {
        return policy;
    }

    /**
     * Retrieves the policy id
     *
     * @return the id of the policy
     */
    public String getPolicyId() {
        return policyId;
    }

    /**
     * Converts the request string into the correspondent type which is more
     * manageable
     *
     * @return the RequestType object corresponding to the Policy passed as string
     */
    public String getRequest() {
        return request;
    }

    /**
     * Retrieves the URI of the PEP
     *
     * @return the uri of the pep
     */
    public String getPepUri() {
        return pepUri;
    }

    /**
     * Checks if the created message is correct
     *
     * @return true if everything goes ok, false otherwise
     */
    public boolean isCorrect() {
        try {
            if( policy != null && !policy.equals( "" ) ) {
                JAXBUtility.unmarshalToObject( PolicyType.class, policy );
            }
            JAXBUtility.unmarshalToObject( RequestType.class, request );
            return true;
        } catch( Exception e ) {
            return false;
        }
    }

}

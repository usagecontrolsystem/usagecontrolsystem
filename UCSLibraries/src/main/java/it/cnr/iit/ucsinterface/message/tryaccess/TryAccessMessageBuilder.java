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

/**
 * Builder for a tryAccessMessage
 * <p>
 * This class has been used in order to build up in an easier way the message to
 * be used in the tryAccess. The builder pattern has been taken from
 * <a href="https://en.wikipedia.org/wiki/Builder_pattern">here</a>
 * </p>
 *
 * @author antonio
 *
 */
public final class TryAccessMessageBuilder {

    private static final Logger log = Logger.getLogger( TryAccessMessageBuilder.class.getName() );

    // instance of the tryaccessmessage that will be built
    private TryAccessMessage tryAccessMessage;

    /**
     * Constructor for the instance of the TryAccessMessage
     *
     * @param source
     *          the source that sent the message
     * @param dest
     *          the destination of the message
     */
    public TryAccessMessageBuilder( String source, String dest ) {
        tryAccessMessage = new TryAccessMessage( source, dest );
    }

    /**
     * Empty constructor
     */
    public TryAccessMessageBuilder() {
        tryAccessMessage = new TryAccessMessage();
    }

    /**
     * Sets the pep uri
     *
     * @param pepUri
     *          the pepuri
     * @return the builder itself or null if something goes wrong
     */
    public TryAccessMessageBuilder setPepUri( String pepUri ) {
        if( tryAccessMessage.getContent().setPepUri( pepUri ) ) {
            return this;
        }
        log.info( "SET URI FAILED" );
        return null;
    }

    /**
     * Sets the policy to be used
     *
     * @param policy
     *          the policy to be used
     * @return the builder itself or null
     */
    public TryAccessMessageBuilder setPolicy( String policy ) {
        if( tryAccessMessage.getContent().setPolicy( policy ) ) {
            return this;
        }
        return null;
    }

    /**
     * Sets the policy id of the policy to be used
     *
     * @param policyId
     *          the policy id
     * @return the builder itself or null if something goes wrong
     */
    public TryAccessMessageBuilder setPolicyId( String policyId ) {
        if( tryAccessMessage.getContent().setPolicyId( policyId ) ) {
            return this;
        }
        return null;
    }

    /**
     * Sets the request to be used in the message
     *
     * @param request
     *          the request to be used
     * @return the builder itself or null
     */
    public TryAccessMessageBuilder setRequest( String request ) {
        if( tryAccessMessage.getContent().setRequest( request ) ) {
            return this;
        }
        return null;
    }

    /**
     * Builds the TryAccessMessage
     *
     * @return the tryAccessMessage instance
     */
    public TryAccessMessage build() {
        return tryAccessMessage;
    }

}

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
package it.cnr.iit.ucsrest.proxies;

import java.net.URI;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.cnr.iit.ucs.constants.CONNECTION;
import it.cnr.iit.ucs.constants.OperationName;
import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.ucs.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucs.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucs.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucs.message.tryaccess.TryAccessResponse;
import it.cnr.iit.ucs.pep.PEPInterface;
import it.cnr.iit.ucs.properties.components.PepProperties;
import it.cnr.iit.ucs.requestmanager.UCSCHInterface;
import it.cnr.iit.ucsrest.rest.UCSRest;
import it.cnr.iit.utility.RESTUtils;
import it.cnr.iit.utility.Utility;
import it.cnr.iit.utility.errorhandling.Reject;

/**
 * This is the proxy towards the PEP.
 * <p>
 * This proxy is useful because in this way the UCS can assume that everything
 * is local and so call the interfaces provided by the various classes. The only
 * class to modify in the case in which we have a brand new communication link
 * is this one
 * </p>
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public class ProxyPEP implements PEPInterface {

    private static final Logger log = Logger.getLogger( ProxyPEP.class.getName() );

    private PepProperties properties;
    private URI uri;

    private boolean initialized = false;

    public ProxyPEP( PepProperties properties ) {
        Reject.ifNull( properties );
        this.properties = properties;

        Optional<URI> optUri = Utility.parseUri( properties.getBaseUri() );
        Reject.ifAbsent( optUri );
        this.uri = optUri.get(); // NOSONAR

        String connectionType = properties.getCommunicationType();
        Reject.ifBlank( connectionType );
        switch( getConnection() ) {
            case API:
                if( buildLocalPep( properties ) ) {
                    initialized = true;
                }
                break;
            case REST_API:
                initialized = true;
                break;
            case SOCKET:
                log.log( Level.SEVERE, CONNECTION.MSG_ERR_UNIMPLEMENTED, connectionType );
                break;
            default:
                log.log( Level.SEVERE, CONNECTION.MSG_ERR_INCORRECT, connectionType );
        }
    }

    private boolean buildLocalPep( PepProperties properties ) {
        Optional<PEPInterface> optPEP = UCSRest.buildComponent( properties );

        if( optPEP.isPresent() ) {
            return true;
        }
        log.severe( "Error building PEP" );
        return false;
    }

    /**
     * In the case of a local PEP sets its the request manager interface it has to
     * use
     *
     * @param requestManager
     *          the request manager the PEP has to use to communicate with the
     *          Request Manager
     */
    public void setRequestManagerInterface(
            UCSCHInterface requestManager ) {
        switch( getConnection() ) {
            case API:
            case REST_API:
            case SOCKET:
                break;
            default:
                log.log( Level.SEVERE, CONNECTION.MSG_ERR_INCORRECT, properties.getCommunicationType() );
                break;
        }

    }

    @Override
    public Message onGoingEvaluation( ReevaluationResponse message ) {
        switch( getConnection() ) {

            case REST_API:
                RESTUtils.asyncPost(
                    uri.toString(),
                    OperationName.ONGOINGRESPONSE_REST,
                    message );
                break;
            case API:
            default:
                break;
        }
        return null;
    }

    @Override
    public String receiveResponse( Message message ) {
        switch( getConnection() ) {
            case REST_API:
                Optional<String> api = getApi( message );
                try {
                    RESTUtils.asyncPost(
                        uri.toString(),
                        api.get(), // NOSONAR : we believe in exceptions
                        message );
                } catch( Exception e ) {
                    log.severe( "Error posting message : " + api );
                    return "KO";
                }
                break;
            case API:
            default:
                log.severe( "Error in the receive response" );
                break;
        }
        return "OK";
    }

    private Optional<String> getApi( Message message ) {
        if( message instanceof TryAccessResponse ) {
            return Optional.of( properties.getApiTryAccessResponse() );
        } else if( message instanceof StartAccessResponse ) {
            return Optional.of( properties.getApiStartAccessResponse() );
        } else if( message instanceof EndAccessResponse ) {
            return Optional.of( properties.getApiEndAccessResponse() );
        }

        return Optional.empty();
    }

    public void start() {
        switch( getConnection() ) {
            case REST_API:
            case SOCKET:
            default:
                break;
        }
    }

    protected CONNECTION getConnection() {
        return CONNECTION.valueOf( properties.getCommunicationType() );
    }

    public boolean isInitialized() {
        return initialized;
    }
}

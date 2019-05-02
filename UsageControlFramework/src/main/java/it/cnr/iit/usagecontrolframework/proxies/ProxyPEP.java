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
package it.cnr.iit.usagecontrolframework.proxies;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import com.google.common.base.Throwables;

import it.cnr.iit.ucs.constants.CONNECTION;
import it.cnr.iit.ucs.properties.components.PepProperties;
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessResponse;
import it.cnr.iit.ucsinterface.node.NodeInterface;
import it.cnr.iit.ucsinterface.pep.ExamplePEP;
import it.cnr.iit.ucsinterface.pep.PEPInterface;
import it.cnr.iit.ucsinterface.requestmanager.UCSCHInterface;
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
 * @author antonio
 *
 */
public class ProxyPEP extends Proxy implements PEPInterface {

    private static final Logger log = Logger.getLogger( ProxyPEP.class.getName() );

    private PepProperties properties;
    private URI uri;
    private ExamplePEP abstractPEP;

    private boolean initialized = false;

    public ProxyPEP( PepProperties properties ) {
        Reject.ifNull( properties );
        this.properties = properties;

        Optional<URI> uri = Utility.parseUri( properties.getBaseUri() );
        Reject.ifAbsent( uri );
        this.uri = uri.get();

        Reject.ifNull( properties.getCommunicationType() );
        switch( getConnection() ) {
            case API:
                if( localPep( properties ) ) {
                    initialized = true;
                }
                break;
            case SOCKET:
                if( connectSocket( properties ) ) { // NOSONAR
                    initialized = true;
                }
                break;
            case REST_API:
                // if( connectRest( properties ) ) {
                initialized = true;
                // }
                break;
            default:
                log.severe( "Incorrect communication medium : " + properties.getCommunicationType() );
                return;
        }
    }

    private boolean localPep( PepProperties properties ) {
        // BEGIN parameter checking
        String className = properties.getClassName();
        if( className == null ) {
            return false;
        }
        // END parameter checking

        try {
            // TODO UCS-32 NOSONAR
            Constructor<?> constructor = Class.forName( className )
                .getConstructor( PepProperties.class );
            abstractPEP = (ExamplePEP) constructor.newInstance( properties );
            return true;
        } catch( InstantiationException | IllegalAccessException
                | ClassNotFoundException | NoSuchMethodException | SecurityException
                | IllegalArgumentException | InvocationTargetException e ) {
            log.severe( e.getMessage() );
        }
        return false;
    }

    private boolean connectSocket( PepProperties properties ) {
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
                abstractPEP.setRequestManagerInterface( requestManager );
                break;
            case REST_API:
            case SOCKET:
            default:
                break;
        }

    }

    @Override
    public Message onGoingEvaluation( Message message ) {
        switch( getConnection() ) {
            case API:
                return abstractPEP.onGoingEvaluation( message );
            case REST_API:
                RESTUtils.asyncPost(
                    properties.getBaseUri(),
                    NodeInterface.ONGOINGRESPONSE_REST,
                    message );
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    // TODO decide better return type, bool?
    public String receiveResponse( Message message ) {
        switch( getConnection() ) {
            case API:
                abstractPEP.receiveResponse( message );
                break;
            case REST_API:
                Optional<String> api = getApi( message );
                try {
                    RESTUtils.asyncPost(
                        uri.toString(),
                        api.get(),
                        message );
                } catch( Exception e ) {
                    log.severe( "Error posting message : " + api );
                    return "KO";
                }
                break;
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

    /**
     * Function to start the local PEP
     */
    public void start() {
        switch( getConnection() ) {
            case API:
                try {
                    abstractPEP.start();
                } catch( InterruptedException | ExecutionException e ) {
                    log.severe( e.getMessage() );
                    Thread.currentThread().interrupt();
                    Throwables.propagate( e );
                }
                break;
            case REST_API:
            case SOCKET:
            default:
                break;
        }
    }

    @Override
    protected CONNECTION getConnection() {
        return CONNECTION.valueOf( properties.getCommunicationType() );
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }
}

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
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import it.cnr.iit.ucs.configuration.PepProperties;
import it.cnr.iit.ucs.constants.CONNECTION;
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessResponse;
import it.cnr.iit.ucsinterface.node.NodeInterface;
import it.cnr.iit.ucsinterface.pep.ExamplePEP;
import it.cnr.iit.ucsinterface.pep.PEPInterface;
import it.cnr.iit.ucsinterface.requestmanager.RequestManagerToExternalInterface;
import it.cnr.iit.utility.RESTUtils;
import it.cnr.iit.utility.Utility;

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

    private boolean initialized = false;

    PepProperties properties;

    // local PEP
    private ExamplePEP abstractPEP;

    // interfaces provided by the PEP to allow the proxy to call it when a
    // response is available
    private String tryAccessResponse = "";
    private String startAccessResponse = "";
    private String endAccessResponse = "";
    private String onGoingEvaluation = "";

    /**
     * Constructor for the proxy PEP
     *
     * @param properties
     *          the configuration of the PEP in xml format
     */
    public ProxyPEP( PepProperties properties ) {
        // BEGIN parameter checking
        if( properties == null ) {
            return;
        }
        String configuration = properties.getCommunication();
        if( configuration == null ) {
            return;
        }
        // END parameter checking
        this.properties = properties;

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
                if( connectRest( properties ) ) {
                    initialized = true;
                }
                break;
            default:
                log.severe( "Incorrect communication medium : " + properties.getCommunication() );
                return;
        }
    }

    /**
     * Function that performs the instantiation of a local PEP
     *
     * @param properties
     *          the configuration of the PEP
     * @return true if everything goes right, false otherwise
     */
    private boolean localPep( PepProperties properties ) {
        // BEGIN parameter checking
        String className = properties.getClassName();
        if( className == null ) {
            return false;
        }
        // END parameter checking

        try {
            Constructor<?> constructor = Class.forName( className )
                .getConstructor( PepProperties.class );
            abstractPEP = (ExamplePEP) constructor.newInstance( properties );
            return true;
        } catch( InstantiationException | IllegalAccessException
                | ClassNotFoundException | NoSuchMethodException | SecurityException
                | IllegalArgumentException | InvocationTargetException e ) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Function that connects to a remote PEP via socket
     *
     * @param xmlPep
     *          the configuration of the remote PEP
     * @return true if everything goes right, false otherwise
     */
    private boolean connectSocket( PepProperties properties ) {
        return false;
    }

    /**
     * Configures all the strings required to connect to a remote PEP via rest
     * interface
     *
     * @param properties
     *          the configuration file for the pep
     * @return true if everything goes ok, false otherwise
     */
    private boolean connectRest( PepProperties properties ) {
        if( properties.getIp() == null ) {
            log.warning( "Missing url parameter in configuration file" );
            return false;
        }

        if( properties.getPort() == null ) {
            log.warning( "Missing port parameter in configuration file" );
            return false;
        }

        if( ( onGoingEvaluation = properties.getOnGoingEvaluation() ) == null ) {
            log.warning( "Missing onGoingEvaluation parameter in configuration file" );
            return false;
        }

        if( ( tryAccessResponse = properties.getTryAccessResponse() ) == null ) {
            log.warning( "Missing tryAccessResponse parameter in configuration file" );
            return false;
        }

        if( ( startAccessResponse = properties.getStartAccessResponse() ) == null ) {
            log.warning( "Missing startAccessResponse parameter in configuration file" );
            return false;
        }

        if( ( endAccessResponse = properties.getEndAccessResponse() ) == null ) {
            log.warning( "Missing endAccessResponse parameter in configuration file" );
            return false;
        }
        return true;
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
            RequestManagerToExternalInterface requestManager ) {
        switch( getConnection() ) {
            case API:
                abstractPEP.setRequestManagerInterface( requestManager );
                break;
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
                    Utility.buildBaseUri( properties.getIp(), properties.getPort() ),
                    NodeInterface.ONGOINGRESPONSE_REST,
                    message );
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    public String receiveResponse( Message message ) {
        switch( getConnection() ) {
            case API:
                abstractPEP.receiveResponse( message );
                break;
            /**
             * LONG STORY:
             * <p>
             * Here we have two possibilities: one is to set the interfaces to be used
             * in the configuration file, the other is to let the PEP write inside the
             * message which is the name of the interface it wants the request manager
             * to call. Both possibilities have pros and cons
             * </p>
             */
            case REST_API:
                String api = "";
                if( message instanceof TryAccessResponse ) {
                    api = tryAccessResponse;
                } else if( message instanceof StartAccessResponse ) {
                    api = startAccessResponse;
                } else if( message instanceof EndAccessResponse ) {
                    api = endAccessResponse;
                }
                try {
                    RESTUtils.asyncPost(
                        Utility.buildBaseUri( properties.getIp(), properties.getPort() ),
                        api,
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

    /**
     * Function to start the local PEP
     */
    public void start() {
        switch( getConnection() ) {
            case API:
                try {
                    abstractPEP.start();
                } catch( InterruptedException | ExecutionException e ) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected CONNECTION getConnection() {
        return CONNECTION.valueOf( properties.getCommunication() );
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }
}

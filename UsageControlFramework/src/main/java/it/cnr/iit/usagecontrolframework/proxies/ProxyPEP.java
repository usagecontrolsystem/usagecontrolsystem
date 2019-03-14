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
import java.util.logging.Level;
import java.util.logging.Logger;

import it.cnr.iit.ucsinterface.constants.CONNECTION;
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessResponse;
import it.cnr.iit.ucsinterface.node.NodeInterface;
import it.cnr.iit.ucsinterface.pep.ExamplePEP;
import it.cnr.iit.ucsinterface.pep.PEPInterface;
import it.cnr.iit.ucsinterface.requestmanager.RequestManagerToExternalInterface;
import it.cnr.iit.utility.RESTUtils;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLPep;

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
    private final Logger LOGGER = Logger
        .getLogger( ProxyPEP.class.getName() );

    private boolean initialized = false;

    // the type of connection between the proxy and the real PEP
    CONNECTION connection;

    // --------------------
    // case of a local PEP
    private ExamplePEP abstractPEP;
    // case of remote PEP
    // --------------------

    // url of the PEP
    private String url = "";
    // Port on which the PEP is attached to
    private String port = "";
    // interfaces provided by the PEP to allow the proxy to call it when a
    // response is available
    private String tryAccessResponse = "";
    private String startAccessResponse = "";
    private String endAccessResponse = "";
    private String onGoingEvaluation = "";
    private String id = "";

    /**
     * Constructor for the proxy PEP
     *
     * @param xmlPep
     *          the configuration of the PEP in xml format
     */
    public ProxyPEP( XMLPep xmlPep ) {
        // BEGIN parameter checking
        if( xmlPep == null ) {
            return;
        }
        String configuration = xmlPep.getCommunication();
        if( configuration == null ) {
            return;
        }
        // END parameter checking
        id = xmlPep.getId();

        connection = CONNECTION.getCONNECTION( xmlPep.getCommunication() );
        switch( connection ) {
            case API:
                if( localPep( xmlPep ) ) {
                    initialized = true;
                }
                break;
            case SOCKET:
                if( connectSocket( xmlPep ) ) {
                    initialized = true;
                }
                break;
            case REST_API:
                if( connectRest( xmlPep ) ) {
                    initialized = true;
                }
                break;
            default:
                LOGGER.log( Level.SEVERE,
                    "WRONG communication " + xmlPep.getCommunication() );
                return;
        }
    }

    /**
     * Function that performs the instantiation of a local PEP
     *
     * @param xmlPep
     *          the configuration of the PEP
     * @return true if everything goes right, false otherwise
     */
    private boolean localPep( XMLPep xmlPep ) {
        // BEGIN parameter checking
        String className = xmlPep.getClassName();
        if( className == null ) {
            return false;
        }
        // END parameter checking

        try {
            Constructor<?> constructor = Class.forName( className )
                .getConstructor( XMLPep.class );
            abstractPEP = (ExamplePEP) constructor.newInstance( xmlPep );
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
    private boolean connectSocket( XMLPep xmlPep ) {
        return false;
    }

    /**
     * Configures all the strings required to connect to a remote PEP via rest
     * interface
     *
     * @param xmlPe
     *          the configuration file for the pep
     * @return true if everything goes ok, false otherwise
     */
    private boolean connectRest( XMLPep xmlPep ) {
        if( ( url = xmlPep.getIp() ) == null ) {
            LOGGER.log( Level.WARNING,
                "Missing parameter in configuration file: " + url );
            return false;
        }

        if( ( port = xmlPep.getPort() ) == null ) {
            LOGGER.log( Level.WARNING,
                "Missing parameter in configuration file: " + url );
            return false;
        }

        if( ( onGoingEvaluation = xmlPep.getOnGoingEvaluation() ) == null ) {
            LOGGER.log( Level.WARNING,
                "Missing parameter in configuration file: " + url );
            return false;
        }

        if( ( tryAccessResponse = xmlPep.getTryAccessResponse() ) == null ) {
            LOGGER.log( Level.WARNING,
                "Missing parameter in configuration file: " + url );
            return false;
        }

        if( ( startAccessResponse = xmlPep.getStartAccessResponse() ) == null ) {
            LOGGER.log( Level.WARNING,
                "Missing parameter in configuration file: " + url );
            return false;
        }

        if( ( endAccessResponse = xmlPep.getEndAccessResponse() ) == null ) {
            LOGGER.log( Level.WARNING,
                "Missing parameter in configuration file: " + url );
            return false;
        }
        return true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean ping() {
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
        switch( connection ) {
            case API:
                abstractPEP.setRequestManagerInterface( requestManager );
                break;
            default:
                break;
        }

    }

    @Override
    public Message onGoingEvaluation( Message message ) {
        switch( connection ) {
            case API:
                return abstractPEP.onGoingEvaluation( message );
            case REST_API:
                RESTUtils.asyncPostAsString(
                    buildUrl( NodeInterface.ONGOINGRESPONSE_REST ),
                    (ReevaluationResponse) message );
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    public String receiveResponse( Message message ) {
        switch( connection ) {
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
                if( message instanceof TryAccessResponse ) {
                    RESTUtils.asyncPostAsString( buildUrl( tryAccessResponse ),
                        (TryAccessResponse) message );
                }
                if( message instanceof StartAccessResponse ) {
                    RESTUtils.asyncPostAsString( buildUrl( startAccessResponse ),
                        (StartAccessResponse) message );
                }
                if( message instanceof EndAccessResponse ) {
                    RESTUtils.asyncPostAsString( buildUrl( endAccessResponse ),
                        (EndAccessResponse) message );
                }
                break;
            default:
                LOGGER.log( Level.SEVERE, "Error in the receive response" );
                break;
        }
        return "";

    }

    /**
     * Function to start the local PEP
     */
    public void start() {
        switch( connection ) {
            case API:
                try {
                    abstractPEP.start();
                } catch( InterruptedException | ExecutionException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Provided the name of the rest API to call, it builds up the complete url to
     * be used to call that interface
     *
     * @param function
     *          the name of the function
     * @return the complete url to be used in the rest call
     */
    private String buildUrl( String function ) {
        StringBuilder url = new StringBuilder();
        url.append( "http://" + this.url + ":" );
        url.append( port + "/" );
        url.append( function );
        return url.toString();
    }

    public String getURL() {
        return url;
    }

    public String getPort() {
        return port;
    }
}

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
import java.util.logging.Level;
import java.util.logging.Logger;

import it.cnr.iit.ucs.configuration.xmlclasses.XMLPdp;
import it.cnr.iit.ucsinterface.constants.CONNECTION;
import it.cnr.iit.ucsinterface.contexthandler.STATUS;
import it.cnr.iit.ucsinterface.pdp.AbstractPDP;
import it.cnr.iit.ucsinterface.pdp.PDPEvaluation;
import it.cnr.iit.ucsinterface.pdp.PDPInterface;

/**
 * This is the class implementing the proxy towards the PDP.
 * <p>
 *
 * </p>
 *
 * @author antonio
 *
 */
final public class ProxyPDP extends Proxy implements PDPInterface {
    private static final Logger LOGGER = Logger.getLogger( ProxyPDP.class.getName() );

    private volatile boolean initialized = false;
    private String configuration;

    // effective implementation of the PDP
    AbstractPDP abstractPDP;

    /**
     * This is the constructor of the proxy to the PDP.
     * <p>
     * Basically here we have to distinguish between the various possibilities we
     * have to implement the pdp and act accordingly. There are 3 different
     * options:
     * <ol>
     * <li>through API: in this case this means that the PDP is in the same
     * virtual machine of the UCS</li>
     * <li>through REST API: this means that the PDP can be queried using API
     * offered via REST</li>
     * <li>through SOCKET: this means that the PDP can be queried by passing to it
     * messages via sockets</li>
     * </ol>
     * </p>
     *
     * @param xmlPdp
     *          the configuration of the PDP in xml form
     */
    public ProxyPDP( XMLPdp xmlPdp ) {
        // BEGIN parameter checking
        if( xmlPdp == null ) {
            return;
        }
        configuration = xmlPdp.getCommunication();
        if( configuration == null ) {
            return;
        }
        // END parameter checking

        CONNECTION connection = CONNECTION.getCONNECTION( xmlPdp.getCommunication() );
        switch( connection ) {
            case API:
                if( localPdp( xmlPdp ) ) {
                    initialized = true;
                }
                break;
            case SOCKET:
                if( connectSocket( xmlPdp ) ) {
                    initialized = true;
                }
                break;
            case REST_API:
                if( connectRest( xmlPdp ) ) {
                    initialized = true;
                }
                break;
            default:
                LOGGER.log( Level.SEVERE,
                    "WRONG communication " + xmlPdp.getCommunication() );
                return;
        }
    }

    private boolean connectSocket( XMLPdp xmlPdp ) {
        return false;
    }

    private boolean connectRest( XMLPdp xmlPdp ) {
        return false;
    }

    /**
     * Initialization of the localPDP
     *
     * @param xmlPdp
     *          the xml configuration of the local pdp
     * @return true if everything goes ok, false otherwise
     */
    private boolean localPdp( XMLPdp xmlPdp ) {
        // BEGIN parameter checking
        String className = xmlPdp.getClassName();
        if( className == null ) {
            return false;
        }
        // END parameter checking

        try {
            Constructor<?> constructor = Class.forName( className )
                .getConstructor( XMLPdp.class );
            abstractPDP = (AbstractPDP) constructor.newInstance( xmlPdp );
            return true;
        } catch( InstantiationException | IllegalAccessException
                | ClassNotFoundException | NoSuchMethodException | SecurityException
                | IllegalArgumentException | InvocationTargetException e ) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public PDPEvaluation evaluate( String request, String policy ) {
        if( initialized == true ) {
            return abstractPDP.evaluate( request, policy );
        }
        return null;
    }

    @Override
    public PDPEvaluation evaluate( String request ) {
        return null;
    }

    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Sets the interfaces the PDP needs to deal with the ObligationManager and
     * the PAP
     *
     * @param proxyPAP
     *          the PAP
     */
    public void setInterfaces( ProxyPAP proxyPAP ) {
        // BEGIN parameter checking
        if( proxyPAP == null ) {
            return;
        }
        // END parameter checking
        if( ( CONNECTION.getCONNECTION( configuration ) == CONNECTION.API )
                && abstractPDP.isInitialized() ) {
            abstractPDP.setPAPInterface( proxyPAP );
            initialized = true;
        }
    }

    @Override
    public boolean isValid() {
        if( initialized ) {
            LOGGER.info( "PAPProxy correctly configured" );
        } else {
            LOGGER.severe( "PAPProxy wrongly configured" );
        }
        return initialized;
    }

    @Override
    public PDPEvaluation evaluate( String request, StringBuilder policy,
            STATUS status ) {
        if( initialized == true ) {
            return abstractPDP.evaluate( request, policy, status );
        }
        return null;
    }

}

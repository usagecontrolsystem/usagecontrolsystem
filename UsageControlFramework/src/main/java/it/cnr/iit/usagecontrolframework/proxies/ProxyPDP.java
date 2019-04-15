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
import java.util.logging.Logger;

import it.cnr.iit.ucs.configuration.PdpProperties;
import it.cnr.iit.ucs.constants.CONNECTION;
import it.cnr.iit.ucs.constants.STATUS;
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
public final class ProxyPDP extends Proxy implements PDPInterface {

    private static final Logger log = Logger.getLogger( ProxyPDP.class.getName() );

    private PdpProperties properties;
    private AbstractPDP abstractPDP;

    private volatile boolean initialized = false;

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
     * @param properties
     *          the configuration of the PDP
     */
    public ProxyPDP( PdpProperties properties ) {
        // BEGIN parameter checking
        if( properties == null ) {
            return;
        }
        this.properties = properties;
        // END parameter checking

        CONNECTION connection = CONNECTION.getCONNECTION( properties.getCommunication() );
        switch( connection ) {
            case API:
                if( localPdp( properties ) ) {
                    initialized = true;
                }
                break;
            case SOCKET:
                if( connectSocket( properties ) ) { // NOSONAR
                    initialized = true;
                }
                break;
            case REST_API:
                if( connectRest( properties ) ) { // NOSONAR
                    initialized = true;
                }
                break;
            default:
                log.severe( "Incorrect communication medium " + properties.getCommunication() );
                return;
        }
    }

    private boolean connectSocket( PdpProperties properties ) {
        return false;
    }

    private boolean connectRest( PdpProperties properties ) {
        return false;
    }

    /**
     * Initialization of the localPDP
     *
     * @param properties
     *          the xml configuration of the local pdp
     * @return true if everything goes ok, false otherwise
     */
    private boolean localPdp( PdpProperties properties ) {
        // BEGIN parameter checking
        String className = properties.getClassName();
        if( className == null ) {
            return false;
        }
        // END parameter checking

        try {
            // TODO UCS-32 NOSONAR
            Constructor<?> constructor = Class.forName( className )
                .getConstructor( PdpProperties.class );
            abstractPDP = (AbstractPDP) constructor.newInstance( properties );
            return true;
        } catch( InstantiationException | IllegalAccessException
                | ClassNotFoundException | NoSuchMethodException | SecurityException
                | IllegalArgumentException | InvocationTargetException e ) {
            log.severe( e.getMessage() );
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
        if( ( CONNECTION.getCONNECTION( properties.getCommunication() ) == CONNECTION.API )
                && abstractPDP.isInitialized() ) {
            abstractPDP.setPAPInterface( proxyPAP );
            initialized = true;
        }
    }

    @Override
    public PDPEvaluation evaluate( String request, StringBuilder policy,
            STATUS status ) {
        if( abstractPDP != null ) {
            return abstractPDP.evaluate( request, policy, status );
        }
        return null;
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

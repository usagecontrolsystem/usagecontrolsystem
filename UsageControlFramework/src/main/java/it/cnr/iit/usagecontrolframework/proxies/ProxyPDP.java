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

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.cnr.iit.ucs.constants.CONNECTION;
import it.cnr.iit.ucs.constants.STATUS;
import it.cnr.iit.ucs.properties.components.PdpProperties;
import it.cnr.iit.ucsinterface.pdp.AbstractPDP;
import it.cnr.iit.ucsinterface.pdp.PDPEvaluation;
import it.cnr.iit.ucsinterface.pdp.PDPInterface;
import it.cnr.iit.usagecontrolframework.rest.UsageControlFramework;
import it.cnr.iit.utility.errorhandling.Reject;

/**
 * This is the class implementing the proxy towards the PDP.
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public final class ProxyPDP extends Proxy implements PDPInterface {

    private static final Logger log = Logger.getLogger( ProxyPDP.class.getName() );

    private PdpProperties properties;
    private AbstractPDP pdp;

    private volatile boolean initialized = false;

    public ProxyPDP( PdpProperties properties ) {
        Reject.ifNull( properties );
        this.properties = properties;

        switch( getConnection() ) {
            case API:
                if( buildLocalPdp( properties ) ) {
                    initialized = true;
                }
                break;
            case SOCKET:
            case REST_API:
                log.log( Level.SEVERE, CONNECTION.MSG_ERR_UNIMPLEMENTED, properties.getCommunicationType() );
                break;
            default:
                log.log( Level.SEVERE, CONNECTION.MSG_ERR_INCORRECT, properties.getCommunicationType() );
        }
    }

    private boolean buildLocalPdp( PdpProperties properties ) {
        Optional<AbstractPDP> optPDP = UsageControlFramework.buildComponent( properties );
        if( optPDP.isPresent() ) {
            pdp = optPDP.get();
            return true;
        }
        log.severe( "Error building PDP" );
        return false;
    }

    @Override
    public PDPEvaluation evaluate( String request, String policy ) {
        if( initialized ) {
            return pdp.evaluate( request, policy );
        }
        return null;
    }

    @Override
    public PDPEvaluation evaluate( String request ) {
        return null;
    }

    public void setInterfaces( ProxyPAP proxyPAP ) {
        Reject.ifNull( proxyPAP );

        if( getConnection() == CONNECTION.API
                && pdp.isInitialized() ) {
            pdp.setPAPInterface( proxyPAP );
            initialized = true;
        }
    }

    @Override
    public PDPEvaluation evaluate( String request, StringBuilder policy, STATUS status ) {
        if( pdp != null ) {
            return pdp.evaluate( request, policy, status );
        }
        return null;
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

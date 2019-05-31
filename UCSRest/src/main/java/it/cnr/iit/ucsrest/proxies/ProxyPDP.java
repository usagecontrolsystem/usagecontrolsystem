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

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.cnr.iit.ucs.constants.CONNECTION;
import it.cnr.iit.ucs.constants.STATUS;
import it.cnr.iit.ucs.pap.PAPInterface;
import it.cnr.iit.ucs.pdp.AbstractPDP;
import it.cnr.iit.ucs.pdp.PDPEvaluation;
import it.cnr.iit.ucs.pdp.PDPInterface;
import it.cnr.iit.ucs.properties.components.PdpProperties;
import it.cnr.iit.ucsrest.rest.UCSRest;
import it.cnr.iit.utility.errorhandling.Reject;
import it.cnr.iit.xacml.wrappers.PolicyWrapper;
import it.cnr.iit.xacml.wrappers.RequestWrapper;

/**
 * This is the class implementing the proxy towards the PDP.
 *
 * @author Antonio La Marra, Alessandro Rosetti
 */
public final class ProxyPDP implements PDPInterface {

    private static final Logger log = Logger.getLogger( ProxyPDP.class.getName() );

    private PdpProperties properties;
    private AbstractPDP pdp;

    public ProxyPDP( PdpProperties properties ) {
        Reject.ifNull( properties );
        this.properties = properties;

        switch( getConnection() ) {
            case LOCAL:
                Reject.ifFalse( buildLocalPdp( properties ), "Error building PDP" );
                break;
            case SOCKET:
            case REST:
                log.log( Level.SEVERE, CONNECTION.MSG_ERR_UNIMPLEMENTED, properties.getCommunicationType() );
                break;
            default:
                log.log( Level.SEVERE, CONNECTION.MSG_ERR_INCORRECT, properties.getCommunicationType() );
        }
    }

    private boolean buildLocalPdp( PdpProperties properties ) {
        Optional<AbstractPDP> component = UCSRest.buildComponent( properties );
        if( component.isPresent() ) {
            pdp = component.get();
            return true;
        }
        return false;
    }

    @Override
    public PDPEvaluation evaluate( RequestWrapper request, PolicyWrapper policy ) {
        switch( getConnection() ) {
            case LOCAL:
                Reject.ifNull( pdp );
                return pdp.evaluate( request, policy );
            default:
                return null;
        }
    }

    @Override
    public PDPEvaluation evaluate( RequestWrapper request, PolicyWrapper policy, STATUS status ) {
        switch( getConnection() ) {
            case LOCAL:
                Reject.ifNull( pdp );
                return pdp.evaluate( request, policy, status );
            default:
                return null;
        }
    }

    @Override
    public PDPEvaluation evaluate( RequestWrapper request ) {
        return null;
    }

    public void setInterfaces( PAPInterface pap ) {
        Reject.ifNull( pap );
        pdp.setPAP( pap );
    }

    public boolean isInitialized() {
        return pdp != null ? pdp.isInitialized() : false;
    }

    protected CONNECTION getConnection() {
        return CONNECTION.valueOf( properties.getCommunicationType() );
    }
}

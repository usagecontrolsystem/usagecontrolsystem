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
package it.cnr.iit.ucs.contexthandler;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import it.cnr.iit.ucs.contexthandler.pipregistry.PIPRegistry;
import it.cnr.iit.ucs.contexthandler.pipregistry.PIPRegistryInterface;
import it.cnr.iit.ucs.obligationmanager.ObligationManagerInterface;
import it.cnr.iit.ucs.pap.PAPInterface;
import it.cnr.iit.ucs.pdp.PDPInterface;
import it.cnr.iit.ucs.pip.PIPCHInterface;
import it.cnr.iit.ucs.properties.components.ContextHandlerProperties;
import it.cnr.iit.ucs.requestmanager.RequestManagerToCHInterface;
import it.cnr.iit.ucs.sessionmanager.SessionManagerInterface;
import it.cnr.iit.utility.RESTUtils;
import it.cnr.iit.utility.errorhandling.Reject;
import it.cnr.iit.utility.errorhandling.exception.PreconditionException;

/**
 * This is the abstract representation of the context handler object.
 * In order to work properly, a context handler requires the interfaces offered
 * by other components.
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public abstract class AbstractContextHandler implements ContextHandlerInterface {

    private static final Logger log = Logger.getLogger( AbstractContextHandler.class.getName() );

    private SessionManagerInterface sessionManager;
    private RequestManagerToCHInterface requestManager;
    private ObligationManagerInterface obligationManager;
    private PDPInterface pdp;
    private PAPInterface pap;

    private PIPRegistryInterface pipRegistry;

    protected ContextHandlerProperties properties;
    protected URI uri;

    protected AbstractContextHandler( ContextHandlerProperties properties ) {
        Reject.ifNull( properties );
        this.properties = properties;

        Optional<URI> uri = RESTUtils.parseUri( properties.getBaseUri() );
        Reject.ifAbsent( uri );
        this.uri = uri.get(); // NOSONAR

        pipRegistry = new PIPRegistry();
    }

    public abstract void startMonitoringThread();

    public abstract void stopMonitoringThread();

    protected final SessionManagerInterface getSessionManager() {
        Reject.ifNull( sessionManager );
        return sessionManager;
    }

    public void setSessionManager( SessionManagerInterface sessionManager ) {
        Reject.ifNull( sessionManager );
        this.sessionManager = sessionManager;
    }

    protected final PDPInterface getPdp() {
        Reject.ifNull( pdp );
        return pdp;
    }

    public void setPdp( PDPInterface pdp ) {
        Reject.ifNull( pdp );
        this.pdp = pdp;
    }

    protected final PAPInterface getPap() {
        Reject.ifNull( pap );
        return pap;
    }

    public void setPap( PAPInterface pap ) {
        Reject.ifNull( pap );
        this.pap = pap;
    }

    protected final RequestManagerToCHInterface getRequestManager() {
        Reject.ifNull( requestManager );
        return requestManager;
    }

    public void setRequestManager(
            RequestManagerToCHInterface requestManager ) {
        Reject.ifNull( requestManager );
        this.requestManager = requestManager;
    }

    public void setPIPs( List<PIPCHInterface> pipList ) {
        Reject.ifNull( pipList );
        for( PIPCHInterface pip : pipList ) {
            pip.setContextHandler( this );
            pipRegistry.add( pip );
        }
    }

    public PIPRegistryInterface getPipRegistry() {
        Reject.ifNull( pipRegistry );
        return pipRegistry;
    }

    protected void setPipRegistry( PIPRegistryInterface pipRegistry ) {
        Reject.ifNull( pipRegistry );
        this.pipRegistry = pipRegistry;
    }

    public void setObligationManager( ObligationManagerInterface obligationManager ) {
        Reject.ifNull( obligationManager );
        this.obligationManager = obligationManager;
    }

    protected final ObligationManagerInterface getObligationManager() {
        Reject.ifNull( obligationManager );
        return obligationManager;
    }

}

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

    /**
     * The constructor requires the various interfaces the
     * ContextHandler will have to deal with to work properly.
     *
     * @param the only parameter is the configuration of the actual context handler.
     *
     */
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
        return sessionManager;
    }

    public void setSessionManager( SessionManagerInterface sessionManager ) {
        this.sessionManager = sessionManager;
    }

    protected final PDPInterface getPdp() {
        return pdp;
    }

    public void setPdp( PDPInterface pdpInterface ) {
        this.pdp = pdpInterface;
    }

    protected final PAPInterface getPap() {
        return pap;
    }

    public void setPap( PAPInterface papInterface ) {
        this.pap = papInterface;
    }

    protected final RequestManagerToCHInterface getRequestManager() {
        return requestManager;
    }

    public void setRequestManager(
            RequestManagerToCHInterface requestManagerToChInterface ) {
        this.requestManager = requestManagerToChInterface;
    }

    public void setPIPs( List<PIPCHInterface> pipList ) {
        for( PIPCHInterface pip : pipList ) {
            pip.setContextHandler( this );
            pipRegistry.add( pip );
        }
    }

    public PIPRegistryInterface getPipRegistry() {
        return pipRegistry;
    }

    protected void setPipRegistry( PIPRegistryInterface pipRegistry ) {
        this.pipRegistry = pipRegistry;
    }

    public void setObligationManager( ObligationManagerInterface obligationManager ) {
        // Reject.ifNull( obligationManager );
        this.obligationManager = obligationManager;
    }

    protected final ObligationManagerInterface getObligationManager() {
        return obligationManager;
    }

    /**
     * Verifies that the status of the context handler is consistent.
     * @throws Exception
     */
    @Deprecated
    public void verify() throws PreconditionException {
        final Map<String, Boolean> checkMap = new HashMap<String, Boolean>() {
            private static final long serialVersionUID = 1L;
            {
                put( "sessionManager", sessionManager == null );
                put( "requestManager", requestManager == null );
                put( "obligationManager", obligationManager == null );
                put( "pap", pap == null );
                put( "pdp", pdp == null );
            }
        };

        StringBuilder sb = new StringBuilder();
        for( Map.Entry<String, Boolean> entry : checkMap.entrySet() ) {
            if( entry.getValue() ) {
                sb.append( entry.getKey() ).append( " " );
            }
        }

        Reject.ifFalse( sb.length() == 0, "Context handler initialization error : " + sb );
        log.info( "ContextHandler correctly initialized" );
    }

}

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
package it.cnr.iit.ucsinterface.contexthandler;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.cnr.iit.ucs.properties.components.ContextHandlerProperties;
import it.cnr.iit.ucsinterface.contexthandler.pipregistry.PIPRegistry;
import it.cnr.iit.ucsinterface.forwardingqueue.ForwardingQueueToCHInterface;
import it.cnr.iit.ucsinterface.obligationmanager.ObligationManagerInterface;
import it.cnr.iit.ucsinterface.pap.PAPInterface;
import it.cnr.iit.ucsinterface.pdp.PDPInterface;
import it.cnr.iit.ucsinterface.pip.PIPCHInterface;
import it.cnr.iit.ucsinterface.requestmanager.RequestManagerToCHInterface;
import it.cnr.iit.ucsinterface.sessionmanager.SessionManagerInterface;
import it.cnr.iit.utility.Utility;
import it.cnr.iit.utility.errorhandling.Reject;

/**
 * This is the abstract representation of the context handler object.
 *
 * <p>
 * In order to work properly, a context handler requires the interfaces offered
 * by other components:
 * <ol>
 * <li>SessionManager</li>
 * <li>PolicyInformationPoint</li>
 * <li>PolicyDecisionPoint</li>
 * <li>PolicyAdministrationPoint</li>
 * <li>RequestManager</li>
 * </ol>
 * </p>
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

    private ForwardingQueueToCHInterface forwardingQueue;
    private PIPRegistry pipRegistry;

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

        Optional<URI> uri = Utility.parseUri( properties.getBaseUri() );
        Reject.ifAbsent( uri );
        this.uri = uri.get(); // NOSONAR

        pipRegistry = new PIPRegistry();
    }

    public abstract void startMonitoringThread();

    public abstract void stopMonitoringThread();

    protected final SessionManagerInterface getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(
            SessionManagerInterface sessionManager ) {
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

    public PIPRegistry getPipRegistry() {
        return pipRegistry;
    }

    protected void setPipRegistry( PIPRegistry pipRegistry ) {
        this.pipRegistry = pipRegistry;
    }

    public void setObligationManager( ObligationManagerInterface obligationManager ) {
        // Reject.ifNull( obligationManager );
        this.obligationManager = obligationManager;
    }

    protected final ObligationManagerInterface getObligationManager() {
        return obligationManager;
    }

    public void setForwardingQueue( ForwardingQueueToCHInterface forwardingQueue ) {
        if( forwardingQueue != null ) {
            this.forwardingQueue = forwardingQueue;
        }
    }

    protected final ForwardingQueueToCHInterface getForwardingQueue() {
        return forwardingQueue;
    }

    /**
     * Verifies that the status of the context handler is consistent.
     * @throws Exception
     */
    @Deprecated
    public void verify() throws Exception {
        final String[] checkObjectsNames = {
            "sessionManager", "pipRegistry", "pap", "pdp",
            "requestManagerToCh", "forwardingQueue", "obligationManager" };
        final boolean[] checkObjects = {
            sessionManager == null, pipRegistry == null,
            pap == null, pdp == null, requestManager == null,
            forwardingQueue == null, obligationManager == null };

        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < checkObjects.length; i++ ) {
            if( checkObjects[i] ) {
                sb.append( checkObjectsNames[i] ).append( " " );
            }
        }

        if( sb.length() == 0 ) {
            log.info( "ContextHandler correctly initialized" );
        } else {
            log.log( Level.SEVERE, "Context handler initialization error : {0}", sb );
            throw new Exception( "Context handler initialization error : {0}" + sb.toString() );
        }
    }

}

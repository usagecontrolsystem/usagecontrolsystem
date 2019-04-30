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
package it.cnr.iit.usagecontrolframework.contexthandler;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.cnr.iit.ucs.configuration.ContextHandlerProperties;
import it.cnr.iit.ucsinterface.contexthandler.ContextHandlerInterface;
import it.cnr.iit.ucsinterface.forwardingqueue.ForwardingQueueToCHInterface;
import it.cnr.iit.ucsinterface.obligationmanager.ObligationManagerInterface;
import it.cnr.iit.ucsinterface.pap.PAPInterface;
import it.cnr.iit.ucsinterface.pdp.PDPInterface;
import it.cnr.iit.ucsinterface.pip.PIPCHInterface;
import it.cnr.iit.ucsinterface.pip.PIPRMInterface;
import it.cnr.iit.ucsinterface.pip.PIPRetrieval;
import it.cnr.iit.ucsinterface.requestmanager.RequestManagerToCHInterface;
import it.cnr.iit.ucsinterface.sessionmanager.SessionManagerInterface;
import it.cnr.iit.usagecontrolframework.configuration.UCFProperties;
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
 *
 * </p>
 *
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public abstract class AbstractContextHandler implements ContextHandlerInterface {

    private static final Logger log = Logger.getLogger( AbstractContextHandler.class.getName() );

    // interface to the session manager
    private SessionManagerInterface sessionManagerInterface;
    // list of pips attached to this context handler
    private List<PIPCHInterface> pipList = new ArrayList<>();
    // pip retrieval used to query remote pips
    private PIPRMInterface pipRetrieval;
    // interface to the pdp
    private PDPInterface pdpInterface;
    // interface to the pap
    private PAPInterface papInterface;
    // interface to the request manager
    private RequestManagerToCHInterface requestManagerToChInterface;
    // obligation manager
    private ObligationManagerInterface obligationManager;
    // forwarding queue interface
    private ForwardingQueueToCHInterface forwardingQueue;

    protected UCFProperties ucfProperties;
    protected ContextHandlerProperties properties;
    protected URI uri;

    private volatile boolean initialized = false;

    /**
     * Superclass constructor. The constructor requires the various interfaces the
     * ContextHandler will have to deal with to work properly.
     *
     * @param the only parameter is the configuration of the actual context handler
     *          passed as a JAVA Object.
     *
     */
    protected AbstractContextHandler( UCFProperties ucfProperties, ContextHandlerProperties properties ) {
        Reject.ifNull( properties );
        this.properties = properties;
        Reject.ifNull( ucfProperties );
        this.ucfProperties = ucfProperties;
        Optional<URI> uri = Utility.parseUri( properties.getBaseUri() );
        Reject.ifAbsent( uri );
        this.uri = uri.get();
    }

    protected final boolean isInitialized() {
        return initialized;
    }

    /**
     * Verifies that the status of the context handler is consistent. In case it is consistent it
     * sets the initialised flag to true, otherwise to false.
     * Having the initialised flag to false makes it impossible to deal with this object
     */
    @Deprecated
    public void verify() {
        final String[] checkObjectsNames = { "configuration", "sessionManager", "pipList/pipRetrieval",
            "pap", "pdp", "requestManagerToCh", "ip", "port", "forwardingQueue", "obligationManager" };
        final boolean[] checkObjects = { properties == null, sessionManagerInterface == null,
            pipList == null && pipRetrieval == null, papInterface == null,
            pdpInterface == null, requestManagerToChInterface == null,
            forwardingQueue == null, obligationManager == null };

        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < checkObjects.length; i++ ) {
            if( checkObjects[i] ) {
                sb.append( checkObjectsNames[i] ).append( " " );
            }
        }

        initialized = sb.length() == 0;

        if( initialized ) {
            log.info( "ContextHandler correctly initialized" );
        } else {
            log.severe( "ContextHandler incorrectly initialized" );
            log.log( Level.SEVERE, "The offending components are : {0}", sb );
        }
    }

    public abstract boolean startMonitoringThread() throws Exception;

    protected final SessionManagerInterface getSessionManagerInterface() {
        if( !initialized ) {
            return null;
        }
        return sessionManagerInterface;
    }

    public void setSessionManagerInterface(
            SessionManagerInterface sessionManagerInterface ) {
        if( sessionManagerInterface == null ) {
            return;
        }
        this.sessionManagerInterface = sessionManagerInterface;
    }

    protected final List<PIPCHInterface> getPipList() {
        if( !initialized ) {
            return new ArrayList<>();
        }
        return pipList;
    }

    public void setPIPRetrieval( PIPRetrieval pipRetrieval ) {
        if( pipRetrieval == null ) {
            return;
        }
        this.pipRetrieval = pipRetrieval;
    }

    protected final PIPRMInterface getPipRetrieval() {
        if( !initialized ) {
            return null;
        }
        return pipRetrieval;
    }

    public void addPip( PIPCHInterface pipInterface ) {
        if( pipInterface == null ) {
            return;
        }
        pipList.add( pipInterface );
    }

    protected final PDPInterface getPdpInterface() {
        if( !initialized ) {
            return null;
        }
        return pdpInterface;
    }

    public void setPdpInterface( PDPInterface pdpInterface ) {
        if( pdpInterface == null ) {
            return;
        }
        this.pdpInterface = pdpInterface;
    }

    protected final PAPInterface getPapInterface() {
        if( !initialized ) {
            return null;
        }
        return papInterface;
    }

    public void setPapInterface( PAPInterface papInterface ) {
        if( papInterface == null ) {
            return;
        }
        this.papInterface = papInterface;
    }

    protected final RequestManagerToCHInterface getRequestManagerToChInterface() {
        if( !initialized ) {
            return null;
        }
        return requestManagerToChInterface;
    }

    public void setRequestManagerToChInterface(
            RequestManagerToCHInterface requestManagerToChInterface ) {
        if( requestManagerToChInterface == null ) {
            return;
        }
        this.requestManagerToChInterface = requestManagerToChInterface;
    }

    /**
     * Sets the various interfaces with which the ContextHandler has to
     * communicate.
     *
     * @param proxySessionManager
     *          the proxy to deal with the session manager
     * @param proxyRequestManager
     *          the proxy to deal with the request manager
     * @param proxyPDP
     *          the proxy to deal with the pdp
     * @param proxyPAP
     *          the proxy to deal with the pap
     * @param pipList
     *          the list of PIPs available to the context handler
     * @param pipRetrieval
     *          the pip-retrieval to be used by the context handler
     * @param obligationManager
     *          the interface to the obligation manager
     */
    public final void setInterfaces( SessionManagerInterface proxySessionManager,
            RequestManagerToCHInterface proxyRequestManager, PDPInterface proxyPDP,
            PAPInterface proxyPAP, List<PIPCHInterface> pipList,
            PIPRMInterface pipRetrieval, ObligationManagerInterface obligationManager,
            ForwardingQueueToCHInterface forwardingQueue ) {
        this.setSessionManagerInterface( proxySessionManager );
        this.setPapInterface( proxyPAP );
        this.setPdpInterface( proxyPDP );
        this.setRequestManagerToChInterface( proxyRequestManager );
        this.setObligationManager( obligationManager );
        this.setForwardingQueue( forwardingQueue );
        // add the pips to the context handler
        for( PIPCHInterface pipInterface : pipList ) {
            pipInterface.setContextHandlerInterface( this );
            this.addPip( pipInterface );
        }

        // add the pipRetrieval
        if( pipRetrieval != null ) {
            pipRetrieval.setContextHandlerInterface( this );
        }
        this.pipRetrieval = pipRetrieval;
        verify();
    }

    public void setObligationManager( ObligationManagerInterface obligationManager ) {
        if( obligationManager != null ) {
            this.obligationManager = obligationManager;
        }
    }

    protected final ObligationManagerInterface getObligationManager() {
        if( !initialized ) {
            return null;
        }
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

}

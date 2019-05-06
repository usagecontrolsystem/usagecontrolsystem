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
package it.cnr.iit.usagecontrolframework.requestmanager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import it.cnr.iit.ucs.properties.components.GeneralProperties;
import it.cnr.iit.ucs.properties.components.RequestManagerProperties;
import it.cnr.iit.ucsinterface.contexthandler.ContextHandlerInterface;
import it.cnr.iit.ucsinterface.forwardingqueue.ForwardingQueueToRMInterface;
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.pipch.PipChMessage;
import it.cnr.iit.ucsinterface.node.NodeInterface;
import it.cnr.iit.ucsinterface.pep.PEPInterface;
import it.cnr.iit.ucsinterface.requestmanager.InterfaceToPerformanceMonitor;
import it.cnr.iit.ucsinterface.requestmanager.RequestManagerToCHInterface;
import it.cnr.iit.ucsinterface.requestmanager.UCSCHInterface;
import it.cnr.iit.usagecontrolframework.proxies.NodeProxy;
import it.cnr.iit.utility.errorhandling.Reject;

/**
 * This is the abstract class representing the request manager.
 * <p>
 * Since we may have different flavours of the request manager, each with its own
 * characteristics (single thread or multiple threads, algorithms used to
 * prioritise the queue and so on), this is a way to provide all the
 * RequestManagers the same basics characteristics
 * </p>
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public abstract class AsynchronousRequestManager
        implements RequestManagerToCHInterface, UCSCHInterface, InterfaceToPerformanceMonitor {

    protected static final Logger log = Logger.getLogger( AsynchronousRequestManager.class.getName() );

    // queue of messages received from the context handler
    private final BlockingQueue<Message> queueFromCH = new LinkedBlockingQueue<>();
    // queue of messages to be passed to the context handler
    private final BlockingQueue<Message> queueToCH = new LinkedBlockingQueue<>();
    // interface provided by the context handler
    private final BlockingQueue<PipChMessage> retrieveRequests = new LinkedBlockingQueue<>();

    private ContextHandlerInterface contextHandler;
    // interface provided by the PEP
    private HashMap<String, PEPInterface> pep;
    // flag that states if the request manager has been correctly initialised
    private volatile boolean initialised = false;

    private ForwardingQueueToRMInterface forwardingQueue;

    protected GeneralProperties properties;
    protected RequestManagerProperties rmProperties;

    // interface used to communicate with other nodes
    private NodeInterface nodeInterface;

    protected AsynchronousRequestManager( GeneralProperties properties, RequestManagerProperties rmProperties ) {
        Reject.ifNull( rmProperties );
        this.rmProperties = rmProperties;

        Reject.ifNull( properties );
        nodeInterface = new NodeProxy( properties );
        this.properties = properties;

        pep = new HashMap<>();

        initialised = true;
    }

    /**
     * Set the interfaces the RequestManager has to communicate with
     *
     * @param contextHandler
     *          the interface provided by the context handler
     * @param proxyPEPMap
     *          the interface to the PEP (behind this interface there is proxy)
     * @param nodeInterface
     *          the interface provided by the nodes for a distributes system
     */
    public final void setInterfaces( ContextHandlerInterface contextHandler,
            Map<String, PEPInterface> proxyPEPMap, NodeInterface nodeInterface,
            ForwardingQueueToRMInterface forwardingQueue ) {
        Reject.ifInvalidObjectState( initialised, AsynchronousRequestManager.class.getName(), log );
        Reject.ifNull( contextHandler, proxyPEPMap, forwardingQueue, nodeInterface );
        this.contextHandler = contextHandler;
        pep.putAll( proxyPEPMap );
        this.forwardingQueue = forwardingQueue;
        this.nodeInterface = nodeInterface;
    }

    protected boolean isInitialized() {
        return initialised;
    }

    protected ContextHandlerInterface getContextHandler() {
        Reject.ifInvalidObjectState( initialised, AsynchronousRequestManager.class.getName(), log );
        return contextHandler;
    }

    protected HashMap<String, PEPInterface> getPEPInterface() {
        Reject.ifInvalidObjectState( initialised, AsynchronousRequestManager.class.getName(), log );
        return pep;
    }

    protected BlockingQueue<Message> getQueueFromCH() {
        Reject.ifInvalidObjectState( initialised, AsynchronousRequestManager.class.getName(), log );
        return queueFromCH;
    }

    protected BlockingQueue<Message> getQueueToCH() {
        Reject.ifInvalidObjectState( initialised, AsynchronousRequestManager.class.getName(), log );
        return queueToCH;
    }

    protected final BlockingQueue<PipChMessage> getRetrieveRequestsQueue() {
        Reject.ifInvalidObjectState( initialised, AsynchronousRequestManager.class.getName(), log );
        return retrieveRequests;
    }

    protected final NodeInterface getNodeInterface() {
        Reject.ifInvalidObjectState( initialised, AsynchronousRequestManager.class.getName(), log );
        return nodeInterface;
    }

    protected final ForwardingQueueToRMInterface getForwardingQueue() {
        Reject.ifInvalidObjectState( initialised, AsynchronousRequestManager.class.getName(), log );
        return forwardingQueue;
    }

    @Override
    public final int getBackLogQueueLength() {
        return queueToCH.size();
    }

}

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
package it.cnr.iit.ucsinterface.requestmanager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import it.cnr.iit.ucs.configuration.GeneralProperties;
import it.cnr.iit.ucs.configuration.RequestManagerProperties;
import it.cnr.iit.ucsinterface.contexthandler.ContextHandlerInterface;
import it.cnr.iit.ucsinterface.forwardingqueue.ForwardingQueueToRMInterface;
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.remoteretrieval.MessagePipCh;
import it.cnr.iit.ucsinterface.node.NodeInterface;
import it.cnr.iit.ucsinterface.node.NodeProxy;
import it.cnr.iit.ucsinterface.pep.PEPInterface;
import it.cnr.iit.utility.errorhandling.Reject;

/**
 * This is the abstract class representing the request manager.
 * <p>
 * Since we may have different flavors of the request manager, each with its own
 * characteristics (single thread or multiple threads, algorithms used to
 * prioritize the queue and so on), this is a way to provide all the
 * RequestManagers the same basics characteristics
 * </p>
 *
 * @author antonio
 *
 */
public abstract class AsynchronousRequestManager
        implements RequestManagerToCHInterface, RequestManagerToExternalInterface,
        InterfaceToPerformanceMonitor {
    protected static final Logger log = Logger.getLogger( AsynchronousRequestManager.class.getName() );

    // queue of messages received from the context handler
    private final BlockingQueue<Message> queueFromCH = new LinkedBlockingQueue<>();
    // queue of messages to be passed to the context handler
    private final BlockingQueue<Message> queueToCH = new LinkedBlockingQueue<>();
    // interface provided by the context handler
    private final BlockingQueue<MessagePipCh> retrieveRequests = new LinkedBlockingQueue<>();
    private ContextHandlerInterface contextHandler;
    // interface provided by the PEP
    private HashMap<String, PEPInterface> pep;
    // flag that states if the request manager has been correctly initialized
    private volatile boolean initialized = false;

    private ForwardingQueueToRMInterface forwardingQueue;

    // configuration of the request manager
    protected RequestManagerProperties properties;
    protected GeneralProperties generalProperties;
    // interface used to communicate with other nodes
    private NodeInterface nodeInterface;

    /**
     * Constructor for the request manager
     *
     * @param properties
     *          the object representing the configuration of the request manager
     */
    protected AsynchronousRequestManager( GeneralProperties generalProperties, RequestManagerProperties properties ) {
        Reject.ifNull( generalProperties );
        Reject.ifNull( properties );
        initialized = true;
        this.properties = properties;
        this.generalProperties = generalProperties;
        pep = new HashMap<>();
        nodeInterface = new NodeProxy( generalProperties );
    }

    /**
     * Set the interfaces the RequestManager has to communicate with
     *
     * @param contextHandler
     *          the interface privided by the context handler
     * @param proxyPEPMap
     *          the interface to the PEP (behind this interface there is proxy)
     * @param nodeInterface
     *          the interface privided by the nodes for a distributes system
     */
    public final void setInterfaces( ContextHandlerInterface contextHandler,
            Map<String, PEPInterface> proxyPEPMap, NodeInterface nodeInterface,
            ForwardingQueueToRMInterface forwardingQueue ) {
        Reject.ifInvalidObjectState( initialized, AsynchronousRequestManager.class.getName(), log );
        Reject.ifNull( contextHandler, proxyPEPMap, forwardingQueue, nodeInterface );
        this.contextHandler = contextHandler;
        pep.putAll( proxyPEPMap );
        this.forwardingQueue = forwardingQueue;
        this.nodeInterface = nodeInterface;
    }

    /**
     * Checks if the abstract class has been correctly initialized
     *
     * @return the initialized flag
     */
    protected boolean isInitialized() {
        return initialized;
    }

    protected ContextHandlerInterface getContextHandler() {
        Reject.ifInvalidObjectState( initialized, AsynchronousRequestManager.class.getName(), log );
        return contextHandler;
    }

    protected HashMap<String, PEPInterface> getPEPInterface() {
        Reject.ifInvalidObjectState( initialized, AsynchronousRequestManager.class.getName(), log );
        return pep;
    }

    protected BlockingQueue<Message> getQueueFromCH() {
        Reject.ifInvalidObjectState( initialized, AsynchronousRequestManager.class.getName(), log );
        return queueFromCH;
    }

    protected BlockingQueue<Message> getQueueToCH() {
        Reject.ifInvalidObjectState( initialized, AsynchronousRequestManager.class.getName(), log );
        return queueToCH;
    }

    protected final RequestManagerProperties getConfiguration() {
        return properties;
    }

    protected final BlockingQueue<MessagePipCh> getRetrieveRequestsQueue() {
        Reject.ifInvalidObjectState( initialized, AsynchronousRequestManager.class.getName(), log );
        return retrieveRequests;
    }

    protected final NodeInterface getNodeInterface() {
        Reject.ifInvalidObjectState( initialized, AsynchronousRequestManager.class.getName(), log );
        return nodeInterface;
    }

    protected final ForwardingQueueToRMInterface getForwardingQueue() {
        Reject.ifInvalidObjectState( initialized, AsynchronousRequestManager.class.getName(), log );
        return forwardingQueue;
    }

    @Override
    public final int getBackLogQueueLength() {
        return queueToCH.size();
    }

}

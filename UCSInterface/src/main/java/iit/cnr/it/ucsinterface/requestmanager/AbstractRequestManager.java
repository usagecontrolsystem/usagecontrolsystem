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
package iit.cnr.it.ucsinterface.requestmanager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLRequestManager;

import iit.cnr.it.ucsinterface.contexthandler.ContextHandlerInterface;
import iit.cnr.it.ucsinterface.forwardingqueue.ForwardingQueueToRMInterface;
import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.remoteretrieval.MessagePipCh;
import iit.cnr.it.ucsinterface.pep.PEPInterface;
import iit.cnr.it.ucsinterface.routing.NodeInterface;

/**
 * This is the abstract class representing the request manager.
 * <p>
 * Since we may have different flavors of the request manager, each with its own
 * characteristics (single thread or multiple threads, algorithms used to
 * prioritize the queue and so on), this is a way to provide all the
 * RequestManagers the same basics characteristics
 * </p>
 * 
 * @deprecated
 * @author antonio
 *
 */
@Deprecated
public abstract class AbstractRequestManager
        implements RequestManagerToCHInterface, RequestManagerToExternalInterface {

    // queue of messages received from the context handler
    private final BlockingQueue<Message> queueFromCH = new LinkedBlockingQueue<>();
    // queue of messages to be passed to the context handler
    private final BlockingQueue<Message> queueToCH = new LinkedBlockingQueue<>();
    // interface provided by the context handler
    private final BlockingQueue<MessagePipCh> retrieveRequests = new LinkedBlockingQueue<>();
    private ContextHandlerInterface contextHandler;
    // interface provided by the PEP
    private PEPInterface pep;
    // interface provided by the other nodes
    private NodeInterface routingInterface;

    private ForwardingQueueToRMInterface forwardingQueue;

    // flag that states if the request manager has been correctly initialized
    private volatile boolean initialized = false;
    // object to be used for logging purposes
    protected static final Logger LOGGER = Logger
        .getLogger( AbstractRequestManager.class.getName() );
    // configuration of the request manager
    private XMLRequestManager xml;

    /**
     * Constructor for the request manager
     * 
     * @param xml
     *          the xml representing the configuration of the request manager
     */
    protected AbstractRequestManager( XMLRequestManager xml ) {
        // BEGIN parameter checking
        if( xml == null ) {
            return;
        }
        // END parameter checking
        initialized = true;
        this.xml = xml;
    }

    /**
     * Set the interfaces the RequestManager has to communicate with
     * 
     * @param contextHandler
     *          the interface privided by the context handler
     * @param proxyPEP
     *          the interface to the PEP (behind this interface there is proxy)
     * @param nodeInterface
     *          the interface privided by the nodes for a distributes system
     */
    public final void setInterfaces( ContextHandlerInterface contextHandler,
            PEPInterface proxyPEP, NodeInterface nodeInterface,
            ForwardingQueueToRMInterface forwardingQueue ) {
        // BEGIN parameter checking
        if( !initialized ) {
            LOGGER.log( Level.WARNING, "RequestManager not initialized correctly" );
            return;
        }
        if( contextHandler == null || proxyPEP == null || forwardingQueue == null ) {
            LOGGER.log( Level.WARNING, "Passed interfaces are not valid" );
            return;
        }
        // END parameter checking
        this.contextHandler = contextHandler;
        this.pep = proxyPEP;
        this.routingInterface = nodeInterface;
        this.forwardingQueue = forwardingQueue;
    }

    /**
     * Checks if the abstract class has been correctly initialized
     * 
     * @return the initialized flag
     */
    protected boolean isInitialized() {
        return initialized;
    }

    // ---------------------------------------------------------------------------
    // Protected getter
    // ---------------------------------------------------------------------------
    protected ContextHandlerInterface getContextHandler() {
        // BEGIN parameter checking
        if( initialized == false ) {
            return null;
        }
        // END parameter checking
        return contextHandler;
    }

    protected PEPInterface getPEPInterface() {
        // BEGIN parameter checking
        if( initialized == false ) {
            return null;
        }
        // END parameter checking
        return pep;
    }

    protected NodeInterface getRoutingInterface() {
        // BEGIN parameter checking
        if( initialized == false ) {
            return null;
        }
        // END parameter checking
        return routingInterface;
    }

    protected BlockingQueue<Message> getQueueFromCH() {
        // BEGIN parameter checking
        if( initialized == false ) {
            return null;
        }
        // END parameter checking
        return queueFromCH;
    }

    protected BlockingQueue<Message> getQueueToCH() {
        // BEGIN parameter checking
        if( initialized == false ) {
            return null;
        }
        // END parameter checking
        return queueToCH;
    }

    final protected XMLRequestManager getXML() {
        return xml;
    }

    final protected BlockingQueue<MessagePipCh> getRetrieveRequestsQueue() {
        // BEGIN parameter checking
        if( initialized == false ) {
            return null;
        }
        // END parameter checking
        return retrieveRequests;
    }

}

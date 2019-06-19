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
package it.cnr.iit.ucs.requestmanager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import it.cnr.iit.ucs.contexthandler.ContextHandlerInterface;
import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.ucs.message.attributechange.AttributeChangeMessage;
import it.cnr.iit.ucs.pep.PEPInterface;
import it.cnr.iit.ucs.properties.components.RequestManagerProperties;
import it.cnr.iit.utility.errorhandling.Reject;

/**
 * This is the abstract class representing the request manager.
 * Since we may have different flavours of the request manager, each with its own
 * characteristics (single thread or multiple threads, algorithms used to
 * prioritise the queue and so on), this is a way to provide all the
 * RequestManagers the same basics characteristics
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public abstract class AbstractRequestManager implements RequestManagerToCHInterface, UCSCHInterface {

    protected static final Logger log = Logger.getLogger( AbstractRequestManager.class.getName() );

    private final BlockingQueue<Message> queueFromCH = new LinkedBlockingQueue<>();
    private final BlockingQueue<Message> queueToCH = new LinkedBlockingQueue<>();
    private final BlockingQueue<AttributeChangeMessage> retrieveRequests = new LinkedBlockingQueue<>();

    private ContextHandlerInterface contextHandler;
    private HashMap<String, PEPInterface> pepMap;

    protected RequestManagerProperties properties;

    protected AbstractRequestManager( RequestManagerProperties properties ) {
        Reject.ifNull( properties );
        this.properties = properties;
        pepMap = new HashMap<>();
    }

    public final void setContextHandler( ContextHandlerInterface contextHandler ) {
        Reject.ifNull( contextHandler );
        this.contextHandler = contextHandler;
    }

    protected ContextHandlerInterface getContextHandler() {
        return contextHandler;
    }

    public final void setPEPMap( Map<String, PEPInterface> pepMap ) {
        Reject.ifNull( pepMap );
        this.pepMap.putAll( pepMap );
    }

    protected HashMap<String, PEPInterface> getPEPMap() {
        return pepMap;
    }

    protected BlockingQueue<Message> getQueueFromCH() {
        return queueFromCH;
    }

    protected BlockingQueue<Message> getQueueToCH() {
        return queueToCH;
    }

    protected final BlockingQueue<AttributeChangeMessage> getRetrieveRequestsQueue() {
        return retrieveRequests;
    }

    public abstract void startMonitoring();

}

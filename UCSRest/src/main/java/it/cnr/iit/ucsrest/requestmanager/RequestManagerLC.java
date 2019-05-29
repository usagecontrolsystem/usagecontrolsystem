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
package it.cnr.iit.ucsrest.requestmanager;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.ucs.message.PURPOSE;
import it.cnr.iit.ucs.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucs.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucs.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucs.properties.components.RequestManagerProperties;
import it.cnr.iit.ucs.requestmanager.AbstractRequestManager;
import it.cnr.iit.utility.errorhandling.Reject;

/**
 * The request manager is an asynchronous component.
 * <p>
 * All the requests coming to the context handler have to reach the request
 * manager first. It will parse and prioritise them. <br>
 * It is an ASYNCHRONOUS component (otherwise it would be impossible to
 * prioritise requests). Once it is queried, it simply provides a dummy response
 * to the caller. Then it will call the interface of the PEP. As you know behind
 * this interface there is a Proxy that abstracts the real communication link
 * between the UCS and the PEP.
 * </p>
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public class RequestManagerLC extends AbstractRequestManager {

    private static final Logger log = Logger.getLogger( RequestManagerLC.class.getName() );

    private ExecutorService inquirers;

    public RequestManagerLC( RequestManagerProperties properties ) {
        super( properties );
        initializeInquirers();
    }

    /**
     * Initialises the request manager with a pool of threads
     *
     * @return true if everything goes fine, false in case of exceptions
    */
    private boolean initializeInquirers() {
        try {
            inquirers = Executors.newFixedThreadPool( 1 );
        } catch( Exception e ) {
            log.severe( "Error initialising the RequestManager inquirers : " + e.getMessage() );
            return false;
        }
        return true;
    }

    @Override
    public synchronized void sendReevaluation( ReevaluationResponse reevaluation ) {
        Reject.ifNull( reevaluation, "Invalid message" );

        log.info( "Sending on going reevaluation." );
        getPEPInterface().get( ( reevaluation ).getPepId() )
            .onGoingEvaluation( reevaluation );
    }

    /**
     * Handles the case of a message received from outside
     * Once a message coming from outside is received from the request manager, it
     * puts it in the priority queue of messages
     */
    @Override
    public synchronized Message sendMessageToCH( Message message ) {
        Reject.ifNull( message, "Null message" );
        try {
            getQueueToCH().put( message );
        } catch( NullPointerException e ) {
            log.severe( e.getMessage() );
        } catch( InterruptedException e ) {
            log.severe( e.getMessage() );
            Thread.currentThread().interrupt();
        }
        return null;
    }

    /**
     * The context handler inquirers basically perform an infinite loop in order
     * to retrieve the messages coming to the request manager and sends those
     * requests to the context handler which will be in charge of answer to the
     * requests
     *
     * @author antonio
     *
    */
    private class ContextHandlerInquirer implements Callable<Message> {

        @Override
        public Message call() {
            Message message;
            try {
                while( ( message = getQueueToCH().take() ) != null ) {
                    Message responseMessage = null;
                    if( message.getPurpose() == PURPOSE.TRYACCESS ) {
                        responseMessage = getContextHandler().tryAccess( (TryAccessMessage) message );
                    } else if( message.getPurpose() == PURPOSE.STARTACCESS ) {
                        responseMessage = getContextHandler().startAccess( (StartAccessMessage) message );
                    } else if( message.getPurpose() == PURPOSE.ENDACCESS ) {
                        responseMessage = getContextHandler().endAccess( (EndAccessMessage) message );
                    } else {
                        throw new IllegalArgumentException( "Invalid message arrived" );
                    }
                    getPEPInterface().get( responseMessage.getDestination() ).receiveResponse( responseMessage );
                }
            } catch( Exception e ) {
                log.severe( e.getMessage() );
                Thread.currentThread().interrupt();
            }
            return null;
        }
    }

    @Override
    public void startMonitoring() {
        inquirers.submit( new ContextHandlerInquirer() );
    }

}

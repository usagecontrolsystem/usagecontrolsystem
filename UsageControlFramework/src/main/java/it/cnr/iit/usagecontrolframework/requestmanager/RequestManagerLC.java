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

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.cnr.iit.ucs.configuration.RequestManagerProperties;
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationMessage;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucsinterface.message.remoteretrieval.MessagePipCh;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessResponse;
import it.cnr.iit.usagecontrolframework.configuration.UCFProperties;
import it.cnr.iit.utility.Utility;
import it.cnr.iit.utility.errorhandling.Reject;

/**
 * The request manager is an asynchronous component.
 * <p>
 * All the requests coming to the context handler have to reach the request
 * manager first. It will parse and prioritize them. <br>
 * It is an ASYNCHRONOUS component (otherwise it would be impossible to
 * prioritize requests). Once it is queried, it simply provides a dummy response
 * to the caller. Then it will call the interface of the PEP. As you know behind
 * this interface there is a Proxy that abstracts the real communication link
 * between the UCS and the PEP.
 * </p>
 *
 * @author antonio
 *
 */
public class RequestManagerLC extends AsynchronousRequestManager {

    private static final Logger log = Logger.getLogger( RequestManagerLC.class.getName() );

    /*
     * This is the pool of thread in charge of polling the queue to retrieve
     * messages coming to the CH
     */
    private ExecutorService inquirers;

    /*
     * This is the thread in charge of handling the operations requested from a
     * remote PIP except from reevaluation.
    private ExecutorService attributeSupplier;
    */

    // states if the request manager has been correctly initialized
    private volatile boolean initialized = false;

    /**
     * Constructor for the RequestManager starting from an XML which describes the
     * basic properties. In this way the Request Manager becomes more easy to be
     * configured
     *
     * @param properties
     */
    public RequestManagerLC( UCFProperties ucfProperties, RequestManagerProperties properties ) {
        super( ucfProperties, properties );
        initialize();
        initialized = isInitialized();
    }

    /**
     * Initializes the request manager with a------ pool of threads
     *
     * @return true if everything goes fine, false in case of exceptions
    */
    private boolean initialize() {
        try {
            inquirers = Executors
                .newFixedThreadPool( 2 );
            inquirers.submit( new ContextHandlerInquirer() );
        } catch( Exception e ) {
            log.severe( e.getMessage() );
            return false;
        }
        return true;

    }

    @Override
    public synchronized void sendMessageToOutside( Message message ) {
        // BEGIN parameter checking
        if( !isInitialized() ) {
            log.warning( "Invalid state of the request manager" );
            throw new IllegalStateException( "RequestManager not initialized correctly" );
        }
        if( message == null ) {
            log.warning( "Invalid message" );
            throw new IllegalArgumentException( "Invalid message" );
        }
        // END parameter checking

        // Case in which we have to forward a message to a remote node
        if( message instanceof TryAccessMessage
                || message instanceof StartAccessMessage
                || message instanceof EndAccessMessage
                || message instanceof ReevaluationMessage ) {
            getNodeInterface().sendMessage( message );
            return;
        }
        if( message instanceof TryAccessResponse
                || message instanceof StartAccessResponse
                || message instanceof EndAccessResponse ) {
            sendResponse( message );

        } else if( message instanceof ReevaluationResponse ) {
            sendReevaluation( (ReevaluationResponse) message );
        }
    }

    private void sendResponse( Message message ) {
        Message original;

        // Case in which we have to forward a response to a remote node
        if( ( original = getForwardingQueue()
            .getOriginalSource( message.getID() ) ) != null ) {
            reswap( message, original );
            getPEPInterface().get( message.getDestination() )
                .receiveResponse( message );
        } else {
            if( message.getDestinationType() ) {
                getNodeInterface().sendMessage( message );
            } else {
                getPEPInterface().get( message.getDestination() )
                    .receiveResponse( message );
            }
        }
    }

    private void reswap( Message message, Message original ) {
        message.setDestination( original.getSource() );
        message.setSourcePort( original.getSourcePort() );
        message.setSource( original.getDestination() );
    }

    private void sendReevaluation( ReevaluationResponse reevaluation ) {
        Optional<URI> uri = Utility.parseUri( ucfProperties.getBaseUri() );
        Reject.ifAbsent( uri );

        log.log( Level.INFO, "[TIME] Effectively Sending on going evaluation {0}",
            System.currentTimeMillis() );

        if( reevaluation.getDestination()
            .equals( uri.get().getHost() ) ) {
            getPEPInterface().get( ( reevaluation ).getPepID() )
                .onGoingEvaluation( reevaluation );
        } else {
            getNodeInterface().sendMessage( reevaluation );
        }
    }

    /**
     * Handles the case of a message received from outside
     * <p>
     * Once a message coming from outside is received from the request manager, it
     * puts it in the priority queue of messages
     * </p>
     */
    @Override
    public synchronized Message sendMessageToCH( Message message ) {
        try {
            if( message instanceof MessagePipCh ) {
                getRetrieveRequestsQueue().put( (MessagePipCh) message );
            } else {
                if( message instanceof TryAccessResponse
                        || message instanceof StartAccessResponse
                        || message instanceof EndAccessResponse
                        || message instanceof ReevaluationResponse ) {
                    sendMessageToOutside( message );
                } else {
                    getQueueToCH().put( message );
                }
            }
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

            while( true ) {
                // BEGIN parameter checking
                if( !initialized ) {
                    log.warning( "Request Manager not initialized correctly" );
                    return null;
                }
                // END parameter checking
                try {
                    Message message = getQueueToCH().take();
                    if( message instanceof TryAccessMessage ) {
                        getContextHandler().tryAccess( message );
                    }
                    if( message instanceof StartAccessMessage ) {
                        getContextHandler().startAccess( message );
                    }
                    if( message instanceof EndAccessMessage ) {
                        getContextHandler().endAccess( message );
                    }
                    if( message instanceof ReevaluationMessage ) {
                        getContextHandler().reevaluate( message );
                    }
                    return message;
                } catch( Exception e ) {
                    log.severe( e.getMessage() );
                    return null;
                }
            }
        }
    }

    /**
     * This is the thread in charge of handling the messages coming from remote
     * PIPS to this contextHnadler.
     *
     * <p>
     * All the messages coming from a remote PIP must be forwarded by the
     * ContextHandler to its local PIP, except the attribute changed message. In
     * this case, in fact, the message wants to trigger a reevaluation.
     * </p>
     *
     * @author antonio
     *
    
    private class AttributeSupplier implements Callable<Void> {
    
    	@Override
    	public Void call() throws Exception {
    		while (true) {
    			// BEGIN parameter checking
    			if (!initialize) {
    				log.log(Level.SEVERE, "Request Manager not initialized correctly");
    				return null;
    			}
    			// END parameter checking
    			MessagePipCh originalMessage = getRetrieveRequestsQueue().take();
    			// System.out.println("Original Message: " + new
    			// Gson().toJson(originalMessage));
    			if (originalMessage.getAction() == ACTION.ATTRIBUTE_CHANGED) {
    				getContextHandler().attributeChanged(originalMessage);
    			} else {
    				MessagePipCh answer = (MessagePipCh) getContextHandler()
    				    .messageForPIP(originalMessage);
    				if (answer != null) {
    					MessagePipCh message = createResponse(answer);
    					String response = buildRestResponseFunction(
    					    originalMessage.getSource());
    					// System.out
    					// .println("Response Message: " + new Gson().toJson(message));
    					RESTUtils.asyncPostAsString(response, message);
    				}
    			}
    		}
    	}
     */

    /**
    	private String buildRestResponseFunction(String source) {
    		StringBuilder string = new StringBuilder();
    		string.append("http://" + source + ":8080");
    		string.append(getXML().getRemoteResponse());
    		return string.toString();
    	}
     */

    /**
     * Starting from the message returned by the context handler builds up the
     * proper message to send as response to the pip retrieval
     *
     * @param message
     *          the message returned by the context handler
     * @return the message to be used as response
    
    private MessagePipCh createResponse(Message message) {
    	MessagePipCh chResponse = (MessagePipCh) message;
    	switch (chResponse.getAction()) {
    		case RETRIEVE:
    			chResponse.setAction(ACTION.RETRIEVE_RESPONSE);
    			break;
    		case SUBSCRIBE:
    			chResponse.setAction(ACTION.SUBSCRIBE_RESPONSE);
    			break;
    		case UPDATE:
    			chResponse.setAction(ACTION.UPDATE_RESPONSE);
    			break;
    		case UNSUBSCRIBE:
    			chResponse.setAction(ACTION.UNSUBSCRIBE_RESPONSE);
    			break;
    		default:
    			break;
    	}
    	return chResponse;
    }
     */
}

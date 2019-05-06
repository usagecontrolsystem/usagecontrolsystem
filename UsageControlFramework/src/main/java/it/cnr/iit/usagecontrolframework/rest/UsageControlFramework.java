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
package it.cnr.iit.usagecontrolframework.rest;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import it.cnr.iit.ucs.properties.UCSProperties;
import it.cnr.iit.ucs.properties.components.ContextHandlerProperties;
import it.cnr.iit.ucs.properties.components.GeneralProperties;
import it.cnr.iit.ucs.properties.components.ObligationManagerProperties;
import it.cnr.iit.ucs.properties.components.PepProperties;
import it.cnr.iit.ucs.properties.components.PipProperties;
import it.cnr.iit.ucs.properties.components.RequestManagerProperties;
import it.cnr.iit.ucsinterface.forwardingqueue.ForwardingQueue;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationMessage;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucsinterface.message.remoteretrieval.MessagePipCh;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessResponse;
import it.cnr.iit.ucsinterface.node.NodeInterface;
import it.cnr.iit.ucsinterface.obligationmanager.ObligationManagerInterface;
import it.cnr.iit.ucsinterface.pep.PEPInterface;
import it.cnr.iit.ucsinterface.pip.PIPBase;
import it.cnr.iit.ucsinterface.pip.PIPCHInterface;
import it.cnr.iit.ucsinterface.pip.PIPOMInterface;
import it.cnr.iit.ucsinterface.pip.PIPRetrieval;
import it.cnr.iit.ucsinterface.ucs.UCSInterface;
import it.cnr.iit.usagecontrolframework.contexthandler.AbstractContextHandler;
import it.cnr.iit.usagecontrolframework.proxies.NodeProxy;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPAP;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPDP;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPEP;
import it.cnr.iit.usagecontrolframework.proxies.ProxySessionManager;
import it.cnr.iit.usagecontrolframework.requestmanager.AsynchronousRequestManager;

/**
 * This is the usage control framework class.
 * <p>
 * This class is in charge of instantiating all the classes and of providing
 * communication means to each component. In order to have a framework that is
 * highly configurable, instead of leaving this class in charge of instantiating
 * the various components, we will have some classes (following the so-called
 * <i>proxy pattern</i>) in charge of providing the effective implementation.
 *
 * Hence the task this class has to accomplish is to instantiate the proxy class
 * and linking together the various classes. In this way this object will be
 * agnostic about the implementation. Moreover the various classes will be built
 * using the informations provided in the configuration file, hence, if the
 * implementation of the configuration is available, it is very easy to
 * configure the framework.
 * </p>
 * <p>
 * Only some objects do not require to have a proxy, since they're the basic
 * components of an UCS:
 * <ol>
 * <li>the Request Manager</li>
 * <li>the Context Handler</li>
 * <li>the various PIPs</li>
 * </ol>
 * Without these components, in fact, it doesn't exist an UCS system.
 * </p>
 * <p>
 * In the implementation we've tried to follow the CERT guidelines to secure
 * coding as much as possible <a href=
 * "https://www.securecoding.cert.org/confluence/display/java/1+Front+Matter">link</a>.
 * </p>
 *
 * @author antonio
 *
 */

@Component
public class UsageControlFramework implements UCSInterface {

    private static final Logger log = Logger.getLogger( UsageControlFramework.class.getName() );

    private static final String MSG_ERR_BUILD_PROP = "Error building PIPBase from properties : {0}";

    // local components
    private AbstractContextHandler contextHandler;
    private AsynchronousRequestManager requestManager;
    private List<PIPBase> pipList = new ArrayList<>();
    private PIPRetrieval pipRetrieval;
    private ObligationManagerInterface obligationManager;

    // proxy components
    private HashMap<String, PEPInterface> proxyPEPMap = new HashMap<>();
    private ProxySessionManager proxySessionManager;
    private ProxyPDP proxyPDP;
    private ProxyPAP proxyPAP;

    private ForwardingQueue forwardingQueue;

    // the only component not initialised here
    private NodeInterface nodeInterface;

    private volatile boolean initialised = false;

    @Autowired
    private UCSProperties properties;

    @PostConstruct
    private void init() {
        if( buildComponents() ) {
            initialised = true;
        }
    }

    private boolean buildComponents() {
        if( !buildContextHandler() ) {
            log.info( "Error in building the context handler" );
            return false;
        }
        if( !buildRequestManager() ) {
            log.info( "Error in building the request manager" );
            return false;
        }
        if( !buildProxySM() ) {
            log.info( "Error in building the session manager" );
            return false;
        }
        if( !buildProxyPDP() ) {
            log.info( "Error in building the pdp" );
            return false;
        }
        if( !buildProxyPolicyAdministrationPoint() ) {
            log.info( "Error in building the pap" );
            return false;
        }
        if( !buildProxyPEPList() ) {
            log.info( "Error in building the pep" );
            return false;
        }
        if( !buildPIPList() ) {
            log.info( "Error in building the pips" );
            return false;
        }
        if( !buildObligationManager() ) {
            log.info( "Error in building the obligation manager" );
            return false;
        }

        forwardingQueue = new ForwardingQueue();
        nodeInterface = new NodeProxy( properties.getGeneral() );

        log.info( "UCF components building done." );

        return checkConnection();
    }

    private boolean checkConnection() {
        List<PIPCHInterface> pipchList = new ArrayList<>( pipList );

        contextHandler.setInterfaces( proxySessionManager, requestManager, proxyPDP,
            proxyPAP, pipchList, pipRetrieval, obligationManager, forwardingQueue );

        try {
            contextHandler.startMonitoringThread();
        } catch( Exception e ) {
            log.severe( "Error starting context handler : " + e.getMessage() );
            return false;
        }

        requestManager.setInterfaces( contextHandler, proxyPEPMap, nodeInterface, forwardingQueue );
        proxyPDP.setInterfaces( proxyPAP );

        return true;
    }

    private boolean buildContextHandler() {
        try {
            ContextHandlerProperties chProperties = properties.getContextHandler();
            // TODO UCS-32 NOSONAR
            Constructor<?> constructor = Class.forName( chProperties.getClassName() )
                .getConstructor( ContextHandlerProperties.class );
            contextHandler = (AbstractContextHandler) constructor
                .newInstance( chProperties );
            return true;
        } catch( Exception e ) {
            log.severe( "build ContextHandler failed : " + e.getMessage() );
            return false;
        }
    }

    private boolean buildRequestManager() {
        try {
            RequestManagerProperties rmProperties = properties.getRequestManager();
            // TODO UCS-32 NOSONAR
            Constructor<?> constructor = Class.forName( rmProperties.getClassName() )
                .getConstructor( GeneralProperties.class, RequestManagerProperties.class );
            requestManager = (AsynchronousRequestManager) constructor
                .newInstance( properties.getGeneral(), rmProperties );
            return true;
        } catch( Exception exception ) {
            log.severe( "build RequestManager failed : " + exception.getMessage() );
            return false;
        }
    }

    public static Optional<PIPBase> buildPIP( PipProperties properties ) {
        try {
            // TODO UCS-32 NOSONAR
            Class<?> clazz = Class.forName( properties.getClassName() );
            Constructor<?> constructor = clazz.getConstructor( PipProperties.class );
            PIPBase pip = (PIPBase) constructor.newInstance( properties );
            return Optional.of( pip );
        } catch( Exception e ) {
            log.log( Level.SEVERE, MSG_ERR_BUILD_PROP, e.getMessage() );
        }
        return Optional.empty();
    }

    private boolean buildPIPList() {
        int failures = 0;

        for( PipProperties pip : properties.getPipList() ) {
            Optional<PIPBase> optPip = buildPIP( pip );

            if( !optPip.isPresent() ) {
                log.severe( "Error building pip" );
                failures++;
                continue;
            }

            PIPBase pipBase = optPip.get();
            pipBase.setContextHandlerInterface( contextHandler );
            pipList.add( pipBase );
        }
        return failures == 0;
    }

    private boolean buildObligationManager() {
        try {
            ObligationManagerProperties omProperties = properties.getObligationManager();
            // TODO UCS-32 NOSONAR
            Constructor<?> constructor = Class.forName( omProperties.getClassName() )
                .getConstructor( ObligationManagerProperties.class );
            obligationManager = (ObligationManagerInterface) constructor
                .newInstance( omProperties );
            obligationManager.setPIPs( new ArrayList<PIPOMInterface>( pipList ), pipRetrieval );
            return true;
        } catch( Exception exception ) {
            log.severe( "Error building obligation manager : " + exception.getMessage() );
            return false;
        }
    }

    private boolean buildProxySM() {
        proxySessionManager = new ProxySessionManager( properties.getSessionManager() );
        proxySessionManager.start();
        return proxySessionManager.isInitialized();
    }

    private boolean buildProxyPDP() {
        proxyPDP = new ProxyPDP( properties.getPolicyDecisionPoint() );
        return proxyPDP.isInitialized();
    }

    private boolean buildProxyPolicyAdministrationPoint() {
        proxyPAP = new ProxyPAP( properties.getPolicyAdministrationPoint() );
        return proxyPAP.isInitialized();
    }

    private boolean buildProxyPEPList() {
        for( PepProperties pep : properties.getPepList() ) {
            ProxyPEP proxyPEP = new ProxyPEP( pep );
            proxyPEP.setRequestManagerInterface( requestManager );
            if( !proxyPEP.isInitialized() ) {
                return false;
            }
            proxyPEPMap.put( pep.getId(), proxyPEP );
        }
        return true;
    }

    @Override
    @Async
    public void tryAccess( TryAccessMessage tryAccessMessage ) {
        // TODO check if sent
        requestManager.sendMessageToCH( tryAccessMessage );
    }

    @Override
    @Async
    public void tryAccessResponse( TryAccessResponse tryAccessResponse ) {
        // TODO check if sent
        requestManager.sendMessageToCH( tryAccessResponse );
    }

    @Override
    @Async
    public void startAccess( StartAccessMessage startAccessMessage ) {
        // TODO check if sent
        requestManager.sendMessageToCH( startAccessMessage );
    }

    @Override
    @Async
    public void startAccessResponse( StartAccessResponse startAccessResponse ) {
        // TODO check if sent
        requestManager.sendMessageToCH( startAccessResponse );
    }

    @Override
    @Async
    public void endAccess( EndAccessMessage endAccessMessage ) {
        // TODO check if sent
        requestManager.sendMessageToCH( endAccessMessage );
    }

    @Override
    @Async
    public void endAccessResponse( EndAccessResponse endAccessResponse ) {
        // TODO check if sent
        requestManager.sendMessageToCH( endAccessResponse );
    }

    @Override
    @Async
    public void onGoingEvaluation( ReevaluationMessage onGoingEvaluation ) {
        // TODO check if sent
        requestManager.sendMessageToCH( onGoingEvaluation );
    }

    @Override
    @Async
    public void onGoingEvaluationResponse(
            ReevaluationResponse onGoingEvaluationResponse ) {
        // TODO check if sent
        requestManager.sendMessageToCH( onGoingEvaluationResponse );
    }

    @Override
    @Async
    public void retrieveRemote( MessagePipCh messagePipCh ) {
        // TODO check if sent
        requestManager.sendMessageToCH( messagePipCh );
    }

    @Override
    @Async
    public void retrieveRemoteResponse( MessagePipCh messagePipCh ) {} // NOSONAR

    public boolean isInitialised() {
        return initialised;
    }

}

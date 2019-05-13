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
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import it.cnr.iit.ucs.properties.UCSProperties;
import it.cnr.iit.ucs.properties.base.PluginProperties;
import it.cnr.iit.ucs.properties.components.PepProperties;
import it.cnr.iit.ucs.properties.components.PipProperties;
import it.cnr.iit.ucsinterface.contexthandler.AbstractContextHandler;
import it.cnr.iit.ucsinterface.forwardingqueue.ForwardingQueue;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucsinterface.message.pipch.PipChMessage;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationMessage;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessResponse;
import it.cnr.iit.ucsinterface.obligationmanager.ObligationManagerInterface;
import it.cnr.iit.ucsinterface.pep.PEPInterface;
import it.cnr.iit.ucsinterface.pip.PIPBase;
import it.cnr.iit.ucsinterface.pip.PIPOMInterface;
import it.cnr.iit.ucsinterface.requestmanager.AbstractRequestManager;
import it.cnr.iit.ucsinterface.ucs.UCSInterface;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPAP;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPDP;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPEP;
import it.cnr.iit.usagecontrolframework.proxies.ProxySessionManager;
import it.cnr.iit.utility.errorhandling.Reject;
import it.cnr.iit.utility.errorhandling.exception.PreconditionException;

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

    private AbstractContextHandler contextHandler;
    private AbstractRequestManager requestManager;
    private ObligationManagerInterface obligationManager;
    private List<PIPBase> pipList = new ArrayList<>();

    private HashMap<String, PEPInterface> proxyPEPMap = new HashMap<>();
    private ProxySessionManager proxySessionManager;
    private ProxyPDP proxyPDP;
    private ProxyPAP proxyPAP;

    private ForwardingQueue forwardingQueue = new ForwardingQueue();

    private volatile boolean initialised = false;

    @Autowired
    private UCSProperties properties;

    @PostConstruct
    private void init() {
        try {
            if( buildComponents() ) {
                initialised = true;
            }
        } catch( PreconditionException e ) {
            log.severe( e.getLocalizedMessage() );
            Thread.currentThread().interrupt();
        }
    }

    private boolean buildComponents() {
        Optional<AbstractContextHandler> optCH = buildComponent( properties.getContextHandler() );
        Reject.ifAbsent( optCH, "Error in building the context handler" );
        contextHandler = optCH.get(); // NOSONAR

        Optional<AbstractRequestManager> optRM = buildComponent( properties.getRequestManager() );
        Reject.ifAbsent( optRM, "Error in building the request manager" );
        requestManager = optRM.get(); // NOSONAR

        Reject.ifFalse( buildProxySM(), "Error in building the session manager" );
        Reject.ifFalse( buildProxyPDP(), "Error in building the pdp" );
        Reject.ifFalse( buildProxyPolicyAdministrationPoint(), "Error in building the pap" );
        Reject.ifFalse( buildProxyPEPList(), "Error in building the pep" );
        Reject.ifFalse( buildPIPList(), "Error in building the pips" );

        Optional<ObligationManagerInterface> optOM = buildComponent( properties.getObligationManager() );
        Reject.ifAbsent( optOM, "Error in building the request manager" );
        obligationManager = optOM.get(); // NOSONAR
        obligationManager.setPIPs( new ArrayList<PIPOMInterface>( pipList ) );

        log.info( "UCF components building done." );

        return checkConnection();
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

    private boolean buildPIPList() {
        int failures = 0;

        for( PipProperties pip : properties.getPipList() ) {
            Optional<PIPBase> optPip = buildComponent( pip );

            if( !optPip.isPresent() ) {
                log.severe( "Error building pip" );
                failures++;
                continue;
            }

            PIPBase pipBase = optPip.get();
            pipBase.setContextHandler( contextHandler );
            pipList.add( pipBase );
        }
        return failures == 0;
    }

    private boolean checkConnection() {

        contextHandler.setInterfaces( proxySessionManager, requestManager, proxyPDP,
            proxyPAP, new ArrayList<>( pipList ), obligationManager, forwardingQueue );

        try {
            contextHandler.startMonitoringThread();
        } catch( Exception e ) {
            log.severe( "Error starting context handler : " + e.getMessage() );
            return false;
        }

        requestManager.setInterfaces( contextHandler, proxyPEPMap, forwardingQueue );
        proxyPDP.setInterfaces( proxyPAP );

        return true;
    }

    public static <T> Optional<T> buildComponent( PluginProperties properties ) {
        try {
            // TODO UCS-32 NOSONAR
            Class<?> propClass = properties.getClass().getInterfaces()[0];
            Constructor<?> constructor = Class.forName( properties.getClassName() )
                .getConstructor( propClass );
            T obj = (T) constructor.newInstance( properties );
            return Optional.of( obj );
        } catch( Exception e ) {
            log.severe( "build " + properties.getClassName() + " failed : " + e.getMessage() );
            return Optional.empty();
        }
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
    public void onGoingEvaluationResponse( ReevaluationResponse onGoingEvaluationResponse ) {
        // TODO check if sent
        requestManager.sendMessageToCH( onGoingEvaluationResponse );
    }

    @Override
    @Async
    public void retrieveRemote( PipChMessage messagePipCh ) {
        // TODO check if sent
        requestManager.sendMessageToCH( messagePipCh );
    }

    public boolean isInitialised() {
        return initialised;
    }

}

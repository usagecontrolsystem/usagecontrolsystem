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
package it.cnr.iit.usagecontrolframework.entry;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.scheduling.annotation.Async;

import it.cnr.iit.ucs.builders.PIPBuilder;
import it.cnr.iit.ucs.configuration.ContextHandlerProperties;
import it.cnr.iit.ucs.configuration.GeneralProperties;
import it.cnr.iit.ucs.configuration.ObligationManagerProperties;
import it.cnr.iit.ucs.configuration.PapProperties;
import it.cnr.iit.ucs.configuration.PdpProperties;
import it.cnr.iit.ucs.configuration.PepProperties;
import it.cnr.iit.ucs.configuration.RequestManagerProperties;
import it.cnr.iit.ucs.configuration.UCSConfiguration;
import it.cnr.iit.ucs.configuration.pip.PipProperties;
import it.cnr.iit.ucs.configuration.session_manager.SessionManagerProperties;
import it.cnr.iit.ucsinterface.contexthandler.AbstractContextHandler;
import it.cnr.iit.ucsinterface.forwardingqueue.ForwardingQueue;
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
import it.cnr.iit.ucsinterface.node.NodeInterface;
import it.cnr.iit.ucsinterface.node.NodeProxy;
import it.cnr.iit.ucsinterface.obligationmanager.ObligationManagerInterface;
import it.cnr.iit.ucsinterface.pep.PEPInterface;
import it.cnr.iit.ucsinterface.pip.PIPBase;
import it.cnr.iit.ucsinterface.pip.PIPCHInterface;
import it.cnr.iit.ucsinterface.pip.PIPOMInterface;
import it.cnr.iit.ucsinterface.pip.PIPRetrieval;
import it.cnr.iit.ucsinterface.requestmanager.AsynchronousRequestManager;
import it.cnr.iit.ucsinterface.requestmanager.RequestManagerToExternalInterface;
import it.cnr.iit.ucsinterface.ucs.UCSInterface;
import it.cnr.iit.usagecontrolframework.builders.ObligationManagerBuilder;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPAP;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPDP;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPEP;
import it.cnr.iit.usagecontrolframework.proxies.ProxySessionManager;

/**
 * This is the usagecontrol framework class.
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
public final class UsageControlFramework implements UCSInterface {

    private static final Logger log = Logger.getLogger( UsageControlFramework.class.getName() );

    private UCSConfiguration configuration;

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

    // the only component not initialized here
    private NodeInterface nodeInterface;

    private volatile boolean initialized = false;

    /**
     * Constructor for the UsageControlFramework.
     * <p>
     * Here we build up all the proxies required to communicate with the various
     * components. We have chosen an approach based on proxies because in this way
     * it becomes a lot easier the part of swapping components, moreover the
     * framework in this way gains flexibility.
     * </p>
     */
    public UsageControlFramework() {
        /*
         * According to CERT we cannot leave an object in an inconsistent state,
         * that's why at first we build all the various components using this
         * utility function, if everything goes fine, then the object is
         * initialized.
         */
        if( !buildComponents() ) {
            return;
        }

        initialized = true;
    }

    /**
     * This utility function has the task of initializing the various proxies.
     * <p>
     * The only objects that do not have a proxy are the PIPs, the ContextHandler
     * and the Request Manager.
     * </p>
     *
     * @return
     */
    private boolean buildComponents() {
        Optional<UCSConfiguration> optConfiguration = UCSConfigurationLoader.getConfiguration();

        if( !optConfiguration.isPresent() ) {
            log.severe( UCSConfigurationLoader.CONFIG_ERR_MESSAGE );
            return false;
        }

        configuration = optConfiguration.get();

        // build the context handler
        if( !buildContextHandler() ) {
            log.info( "Error in building the context handler" );
            return false;
        }
        // builds the request manager
        if( !buildRequestManager() ) {
            log.info( "Error in building the request manager" );
            return false;
        }
        // builds the PIPS
        if( !buildPIPs() ) {
            log.info( "Error in building the pips" );
            return false;
        }
        // builds the proxies
        if( !buildProxySM() ) {
            log.info( "Error in building the session manager" );
            return false;
        }
        if( !buildObligationManager() ) {
            log.info( "Error in building the obligation manager" );
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
        if( !buildProxyPEP() ) {
            log.info( "Error in building the pep" );
            return false;
        }

        forwardingQueue = new ForwardingQueue();
        nodeInterface = new NodeProxy( configuration.getGeneral() );
        log.info( "*******************\nCC" );
        // checks if every component is ok
        return checkConnection();
    }

    /**
     * Builds the context handler. The building of the context handler employs a
     * class which simply performs the building and return the context handler
     * instance.
     *
     * @return true if everything goes ok, false otherwise
     */
    private boolean buildContextHandler() {
        ContextHandlerProperties properties = configuration.getContextHandler();
        try {
            String className = properties.getClassName();
            Constructor<?> constructor = Class.forName( className )
                .getConstructor( GeneralProperties.class, ContextHandlerProperties.class );
            contextHandler = (AbstractContextHandler) constructor
                .newInstance( configuration.getGeneral(), properties );
            return true;
        } catch( Exception exception ) {
            log.severe( "build ContextHandler failed" + exception.getMessage() );
            return false;
        }
    }

    /**
     * Function in charge of building the request manager.
     * <p>
     * By reading the xml provided in the configuration file, this function is
     * able to build up the request manager.
     * </p>
     *
     * @return true if everything goes ok, false otherwise
     */
    private boolean buildRequestManager() {
        RequestManagerProperties properties = configuration.getRequestManager();

        try {
            String className = properties.getClassName();
            Constructor<?> constructor = Class.forName( className )
                .getConstructor( GeneralProperties.class, RequestManagerProperties.class );
            requestManager = (AsynchronousRequestManager) constructor
                .newInstance( configuration.getGeneral(), properties );
            return true;
        } catch( Exception exception ) {
            log.severe( "build RequestManager failed" + exception.getMessage() );
            return false;
        }
    }

    /**
     * Function in charge of building the various PIPS.
     * <p>
     * Basically by reading the provided XML this function is able to build up the
     * required PIPs.
     * </p>
     *
     * @return true if all the PIPs are correctly created, false otherwise
     */
    private boolean buildPIPs() {
        int failures = 0;

        for( PipProperties pip : configuration.getPipList() ) {
            Optional<PIPBase> optPip = PIPBuilder.buildFromProperties( pip );

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

    /**
     * Builds the proxy to deal with the Session Manager
     *
     * @return true if everything goes fine, false otherwise
     */
    private boolean buildProxySM() {
        SessionManagerProperties properties = configuration.getSessionManager();
        proxySessionManager = new ProxySessionManager( properties );
        proxySessionManager.start();
        return proxySessionManager.isInitialized();
    }

    /**
     * Builds the proxy to the obligation manager
     *
     * @return true if everything goes fine, false otherwise
     */
    private boolean buildObligationManager() {
        ObligationManagerProperties properties = configuration.getObligationManager();
        obligationManager = ObligationManagerBuilder.build( properties,
            new ArrayList<PIPOMInterface>( pipList ), pipRetrieval );
        return true;
    }

    /**
     * Builds the proxy to deal with the PDP
     *
     * @return true if everything goes fine, false otherwise
     */
    private boolean buildProxyPDP() {
        PdpProperties properties = configuration.getPolicyDecisionPoint();
        proxyPDP = new ProxyPDP( properties );
        return proxyPDP.isInitialized();
    }

    /**
     * Builds the proxy to deal with the PAP
     *
     * @return true if everything goes fine, false otherwise
     */
    private boolean buildProxyPolicyAdministrationPoint() {
        PapProperties properties = configuration.getPolicyAdministrationPoint();
        proxyPAP = new ProxyPAP( properties );
        return proxyPAP.isInitialized();
    }

    /**
     * At first sets up all the interfaces to allow communication between the
     * various components, then performs a very simple ping function in order to
     * check if every component is right
     *
     * @return true if everything goes ok, false otherwise
     * @throws Exception
     */
    private boolean checkConnection() {
        List<PIPCHInterface> pipchList = new ArrayList<>( pipList );

        contextHandler.setInterfaces( proxySessionManager, requestManager, proxyPDP,
            proxyPAP, pipchList, pipRetrieval, obligationManager, forwardingQueue );

        try {
            contextHandler.startMonitoringThread();
        } catch( Exception e ) {
            log.severe( e.getMessage() );
            return false;
        }

        requestManager.setInterfaces( contextHandler, proxyPEPMap, nodeInterface,
            forwardingQueue );
        proxyPDP.setInterfaces( proxyPAP );

        return true;
    }

    /**
     * Builds the Proxy to communicate with the PEP
     *
     * @return true if the proxy was correclty set up, false otherwise
     */
    private boolean buildProxyPEP() {
        List<PepProperties> pepList = configuration.getPepList();
        for( PepProperties pep : pepList ) {
            ProxyPEP proxyPEP = new ProxyPEP( pep );
            proxyPEP.setRequestManagerInterface( requestManager );
            if( !proxyPEP.isInitialized() ) {
                return false;
            }
            proxyPEPMap.put( pep.getId(), proxyPEP );
        }
        return true;
    }

    /* Functions */

    @Override
    @Async
    public void tryAccess( TryAccessMessage tryAccessMessage ) {
        getRequestManager().sendMessageToCH( tryAccessMessage );
    }

    @Override
    @Async
    public void tryAccessResponse( TryAccessResponse tryAccessResponse ) {
        getRequestManager().sendMessageToCH( tryAccessResponse );
    }

    @Override
    @Async
    public void startAccess( StartAccessMessage startAccessMessage ) {
        getRequestManager().sendMessageToCH( startAccessMessage );
    }

    @Override
    @Async
    public void startAccessResponse( StartAccessResponse startAccessResponse ) {
        getRequestManager().sendMessageToCH( startAccessResponse );
    }

    @Override
    @Async
    public void endAccess( EndAccessMessage endAccessMessage ) {
        getRequestManager().sendMessageToCH( endAccessMessage );
    }

    @Override
    @Async
    public void endAccessResponse( EndAccessResponse endAccessResponse ) {
        getRequestManager().sendMessageToCH( endAccessResponse );
    }

    @Override
    @Async
    public void onGoingEvaluation( ReevaluationMessage onGoingEvaluation ) {
        getRequestManager().sendMessageToCH( onGoingEvaluation );
    }

    @Override
    @Async
    public void onGoingEvaluationResponse(
            ReevaluationResponse onGoingEvaluationResponse ) {
        getRequestManager().sendMessageToCH( onGoingEvaluationResponse );
    }

    @Override
    @Async
    public void retrieveRemote( MessagePipCh messagePipCh ) {
        getRequestManager().sendMessageToCH( messagePipCh );
    }

    @Override
    @Async
    public void retrieveRemoteResponse( MessagePipCh messagePipCh ) {} // NOSONAR

    /* Getters */

    public Map<String, PEPInterface> getPEPProxy() {
        return proxyPEPMap;
    }

    public RequestManagerToExternalInterface getRequestManager() {
        return requestManager;
    }

    public boolean getInitialized() {
        return initialized;
    }

    @Override
    public void register( Message message ) {} // NOSONAR

}

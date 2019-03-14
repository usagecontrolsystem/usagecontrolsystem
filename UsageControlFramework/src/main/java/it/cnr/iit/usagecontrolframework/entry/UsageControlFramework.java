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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.scheduling.annotation.Async;

import it.cnr.iit.ucs.configuration.BasicConfiguration;
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
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.Configuration;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLContextHandler;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLObligationManager;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLPap;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLPdp;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLPep;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLPip;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLRequestManager;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLSessionManager;
import it.cnr.iit.usagecontrolframework.obligationmanager.ObligationManagerBuilder;
import it.cnr.iit.usagecontrolframework.property.Properties;
import it.cnr.iit.usagecontrolframework.proxies.PIPBuilder;
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
    private static final Logger LOGGER = Logger
        .getLogger( UsageControlFramework.class.getName() );

    private Configuration configuration;

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
    private Properties properties;

    private ForwardingQueue forwardingQueue;

    // the only component not initialized here
    private NodeInterface nodeInterface;

    // performance monitor component, it can be considered as related to all the
    // components
    // private PerformanceMonitorInterface performanceMonitor;

    /**
     * This variable has to be checked every time we try to access an object.
     */
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
        if( ( configuration = retrieveConfiguration() ) == null ) {
            return false;
        }

        BasicConfiguration.getBasicConfiguration().configure( configuration );

        DISTRIBUTED_TYPE distributedType = DISTRIBUTED_TYPE.NONE;

        // build the context handler
        if( !buildCH( distributedType ) ) {
            LOGGER.log( Level.INFO, "Error in building the context handler" );
            return false;
        }
        // builds the request manager
        if( !buildRequestManager() ) {
            LOGGER.log( Level.INFO, "Error in building the request manager" );
            return false;
        }
        // builds the PIPS
        if( !buildPIPs() ) {
            LOGGER.log( Level.INFO, "Error in building the pips" );
            return false;
        }

        // Builds the proxies

        if( !buildProxySM() ) {
            LOGGER.log( Level.INFO, "Error in building the session manager" );
            return false;
        }

        if( !buildObligationManager() ) {
            LOGGER.log( Level.INFO, "Error in building the obligation manager" );
            return false;
        }
        if( !buildProxyPDP() ) {
            LOGGER.log( Level.INFO, "Error in building the pdp" );
            return false;
        }
        if( !buildProxyPAP() ) {
            LOGGER.log( Level.INFO, "Error in building the pap" );
            return false;
        }
        if( !buildProxyPEP() ) {
            LOGGER.log( Level.INFO, "Error in building the pep" );
            return false;
        }

        forwardingQueue = new ForwardingQueue();
        nodeInterface = new NodeProxy();
        System.out.println( "*******************\nCC" );
        // checks if every component is ok
        if( !checkConnection() ) {
            return false;
        }
        return true;

    }

    /**
     * Retrieves the object that represents the configuration file.
     *
     * @return the object that represents the configuration file.
     */
    private Configuration retrieveConfiguration() {
        properties = new Properties();
        Configuration configuration = properties.getConfiguration();
        if( configuration == null ) {
            LOGGER.log( Level.SEVERE,
                "Configuration is null, properties was not correctly initialized" );
            return null;
        }
        return configuration;
    }

    private boolean buildCH( DISTRIBUTED_TYPE distributedType ) {
        if( distributedType == null || distributedType == DISTRIBUTED_TYPE.NONE ) {
            return buildCH();
        } else {
            XMLContextHandler xmlContextHandler = configuration.getCh();
            try {
                String className = xmlContextHandler.getClassName();
                Constructor<?> constructor = Class.forName( className )
                    .getConstructor( XMLContextHandler.class );
                contextHandler = (AbstractContextHandler) constructor
                    .newInstance( xmlContextHandler );

                return true;
            } catch( Exception exception ) {
                exception.printStackTrace();
                LOGGER.log( Level.SEVERE, "buildCH failed" );
                return false;
            }
        }

    }

    /**
     * Builds the context handler. The building of the context handler employs a
     * class which simply performs the building and return the context handler
     * instance.
     *
     * @return true if everything goes ok, false otherwise
     */
    private boolean buildCH() {
        XMLContextHandler xmlContextHandler = configuration.getCh();
        try {
            String className = xmlContextHandler.getClassName();
            Constructor<?> constructor = Class.forName( className )
                .getConstructor( XMLContextHandler.class );
            contextHandler = (AbstractContextHandler) constructor
                .newInstance( xmlContextHandler );

            return true;
        } catch( Exception exception ) {
            exception.printStackTrace();
            LOGGER.log( Level.SEVERE, "buildCH failed" );
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
        XMLRequestManager xmlRequestManager = configuration.getRm();

        try {
            String className = xmlRequestManager.getClassName();
            Constructor<?> constructor = Class.forName( className )
                .getConstructor( XMLRequestManager.class );
            requestManager = (AsynchronousRequestManager) constructor
                .newInstance( xmlRequestManager );
            return true;
        } catch( Exception exception ) {
            exception.printStackTrace();
        }

        return false;
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
        List<XMLPip> list = configuration.getPipList();
        for( XMLPip xmlPip : list ) {
            PIPBase pipInterface = PIPBuilder.build( xmlPip );
            if( pipInterface == null ) {
                return false;
            }
            pipInterface.setContextHandlerInterface( contextHandler );
            pipList.add( pipInterface );
        }
        return true;
    }

    /**
     * Builds the proxy to deal with the Session Manager
     *
     * @return true if everything goes fine, false otherwise
     */
    private boolean buildProxySM() {
        XMLSessionManager xmlSM = configuration.getSessionManager();
        proxySessionManager = new ProxySessionManager( xmlSM );
        proxySessionManager.start();
        return proxySessionManager.isInitialized();
    }

    /**
     * Builds the proxy to the obligation manager
     *
     * @return true if everything goes fine, false otherwise
     */
    private boolean buildObligationManager() {
        XMLObligationManager xmlOm = configuration.getOm();
        obligationManager = ObligationManagerBuilder.build( xmlOm,
            new ArrayList<PIPOMInterface>( pipList ), pipRetrieval );
        return obligationManager.isInitialized();
    }

    /**
     * Builds the proxy to deal with the PDP
     *
     * @return true if everything goes fine, false otherwise
     */
    private boolean buildProxyPDP() {
        XMLPdp xmlPdp = configuration.getPdp();
        proxyPDP = new ProxyPDP( xmlPdp );
        return proxyPDP.isInitialized();
    }

    /**
     * Builds the proxy to deal with the PAP
     *
     * @return true if everything goes fine, false otherwise
     */
    private boolean buildProxyPAP() {
        XMLPap xmlPap = configuration.getPap();
        proxyPAP = new ProxyPAP( xmlPap );
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
        List<PIPCHInterface> list = new ArrayList<>( pipList );

        contextHandler.setInterfaces( proxySessionManager, requestManager, proxyPDP,
            proxyPAP, list, pipRetrieval, obligationManager, forwardingQueue );

        try {
            contextHandler.startMonitoringThread();
        } catch( Exception e ) {
            e.printStackTrace();
            return false;
        }

        requestManager.setInterfaces( contextHandler, proxyPEPMap, nodeInterface,
            forwardingQueue );
        proxyPDP.setInterfaces( proxyPAP );
        // proxySessionManager.start();

        return true;
    }

    /**
     * Builds the Proxy to communicate with the PEP
     *
     * @return true if the proxy was correclty set up, false otherwise
     */
    private boolean buildProxyPEP() {

        List<XMLPep> xmlPep = configuration.getPep();
        for( XMLPep xml : xmlPep ) {
            ProxyPEP proxyPEP = new ProxyPEP( xml );
            proxyPEP.setRequestManagerInterface( requestManager );
            if( !proxyPEP.isInitialized() ) {
                return false;
            }
            this.proxyPEPMap.put( xml.getId(), proxyPEP );
        }
        // proxyPEP.setCHInterface(contextHandler);
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
        return;
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
        return;
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
    public void retrieveRemoteResponse( MessagePipCh messagePipCh ) {
        getPIPRetrieval().messageArrived( messagePipCh );
    }

    /* Getters */

    public HashMap<String, PEPInterface> getPEPProxy() {
        return proxyPEPMap;
    }

    public RequestManagerToExternalInterface getRequestManager() {
        return requestManager;
    }

    public PIPRetrieval getPIPRetrieval() {
        return pipRetrieval;
    }

    @Override
    public void register( Message message ) {

    }

    public boolean getInitialized() {
        return initialized;
    }

}

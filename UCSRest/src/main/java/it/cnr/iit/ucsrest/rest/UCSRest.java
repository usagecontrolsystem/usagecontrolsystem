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
package it.cnr.iit.ucsrest.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import it.cnr.iit.ucs.contexthandler.AbstractContextHandler;
import it.cnr.iit.ucs.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucs.obligationmanager.ObligationManagerInterface;
import it.cnr.iit.ucs.pap.PAPInterface;
import it.cnr.iit.ucs.pdp.PDPInterface;
import it.cnr.iit.ucs.pep.PEPInterface;
import it.cnr.iit.ucs.pip.PIPBase;
import it.cnr.iit.ucs.pip.PIPCHInterface;
import it.cnr.iit.ucs.pip.PIPOMInterface;
import it.cnr.iit.ucs.properties.UCSProperties;
import it.cnr.iit.ucs.properties.base.PluginProperties;
import it.cnr.iit.ucs.properties.components.PepProperties;
import it.cnr.iit.ucs.properties.components.PipProperties;
import it.cnr.iit.ucs.requestmanager.AbstractRequestManager;
import it.cnr.iit.ucs.sessionmanager.SessionManagerInterface;
import it.cnr.iit.ucs.ucs.UCSInterface;
import it.cnr.iit.utility.ReflectionsUtility;
import it.cnr.iit.utility.errorhandling.Reject;
import it.cnr.iit.utility.errorhandling.exception.PreconditionException;

/**
 * This class is in charge of instantiating all the components.
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
@Component
public class UCSRest implements UCSInterface {

    private static final Logger log = Logger.getLogger( UCSRest.class.getName() );

    private AbstractContextHandler contextHandler;
    private AbstractRequestManager requestManager;
    private ObligationManagerInterface obligationManager;
    private SessionManagerInterface sessionManager;
    private PDPInterface pdp;
    private PAPInterface pap;
    private List<PIPBase> pipList = new ArrayList<>();
    private HashMap<String, PEPInterface> pepMap = new HashMap<>();

    private boolean initialised = false;

    @Autowired
    private UCSProperties properties;

    @PostConstruct
    private void init() {
        try {
            buildComponents();
            initialised = true;
        } catch( PreconditionException e ) {
            log.severe( e.getLocalizedMessage() );
            Thread.currentThread().interrupt();
        }
    }

    private boolean buildComponents() {
        log.info( "UsageControlFramework init" );

        buildContextHandler();
        buildRequestManager();
        buildSessionManager();
        buildPDP();
        buildPolicyAdministrationPoint();
        buildPEPList();
        Reject.ifFalse( buildPIPList(), "Error in building the pips" );
        buildObligationManager();

        log.info( "UsageControlFramework building components completed." );

        return setupComponentsConnections();
    }

    private <T> Optional<T> buildComponent( PluginProperties property, Class<T> clazz ) {
        Optional<T> component = ReflectionsUtility.buildComponent( property, clazz );
        Reject.ifAbsent( component, "Error in building " + clazz.getSimpleName() );
        return component;
    }

    private void buildContextHandler() {
        contextHandler = buildComponent( properties.getContextHandler(), AbstractContextHandler.class ).get(); // NOSONAR
    }

    private void buildRequestManager() {
        requestManager = buildComponent( properties.getRequestManager(), AbstractRequestManager.class ).get(); // NOSONAR
        requestManager.startMonitoring();
    }

    private void buildSessionManager() {
        sessionManager = buildComponent( properties.getSessionManager(), SessionManagerInterface.class ).get(); // NOSONAR
        sessionManager.start();
    }

    private void buildPDP() {
        this.pdp = buildComponent( properties.getPolicyDecisionPoint(), PDPInterface.class ).get(); // NOSONAR
    }

    private void buildPolicyAdministrationPoint() {
        this.pap = buildComponent( properties.getPolicyAdministrationPoint(), PAPInterface.class ).get(); // NOSONAR
    }

    private void buildPEPList() {
        for( PepProperties pepProp : properties.getPepList() ) {
            Optional<PEPInterface> pep = ReflectionsUtility.buildComponent( pepProp, PEPInterface.class );
            if( pep.isPresent() ) {
                pepMap.put( pepProp.getId(), pep.get() );
            } else {
                log.severe( "building PEP failed" );
            }
        }
    }

    private void buildObligationManager() {
        Optional<ObligationManagerInterface> optOM = ReflectionsUtility.buildComponent( properties.getObligationManager(),
            ObligationManagerInterface.class );
        Reject.ifAbsent( optOM, "Error in building the request manager" );
        obligationManager = optOM.get(); // NOSONAR
        obligationManager.setPIPs( new ArrayList<PIPOMInterface>( pipList ) );
    }

    private boolean buildPIPList() {
        int failures = 0;

        for( PipProperties pip : properties.getPipList() ) {
            Optional<PIPBase> optPip = ReflectionsUtility.buildComponent( pip, PIPBase.class );

            if( !optPip.isPresent() ) {
                log.severe( "Error building pip" );
                failures++;
                continue;
            }
            initialised = true;

            PIPBase pipBase = optPip.get();
            pipBase.setContextHandler( contextHandler );
            pipList.add( pipBase );
        }
        return failures == 0;
    }

    private boolean setupComponentsConnections() {
        try {
            contextHandler.setSessionManager( sessionManager );
            contextHandler.setRequestManager( requestManager );
            contextHandler.setPap( pap );
            contextHandler.setPdp( pdp );
            contextHandler.setObligationManager( obligationManager );
            contextHandler.setPIPs( new ArrayList<PIPCHInterface>( pipList ) );

            contextHandler.startMonitoringThread();
            requestManager.setInterfaces( contextHandler, pepMap );
            // pdp.setInterfaces( pap );
        } catch( Exception e ) {
            log.severe( "Error starting context handler : " + e.getMessage() );
            return false;
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
    public void startAccess( StartAccessMessage startAccessMessage ) {
        // TODO check if sent
        requestManager.sendMessageToCH( startAccessMessage );
    }

    @Override
    @Async
    public void endAccess( EndAccessMessage endAccessMessage ) {
        // TODO check if sent
        requestManager.sendMessageToCH( endAccessMessage );
    }

    public boolean isInitialised() {
        return initialised;
    }

}

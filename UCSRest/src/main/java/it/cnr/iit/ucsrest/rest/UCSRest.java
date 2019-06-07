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
            log.info( "[INIT] usage control initialisation ..." );
            buildComponents();
            setupConnections();
            log.info( "[DONE] building components completed" );
            initialised = true;
        } catch( PreconditionException e ) {
            log.severe( "[ERROR] " + e.getMessage() );
            Thread.currentThread().interrupt();
        }
    }

    private void buildComponents() {
        buildContextHandler();
        buildRequestManager();
        buildSessionManager();
        buildPolicyDecisionPoint();
        buildPolicyAdministrationPoint();
        buildPEPList();
        buildPIPList();
        buildObligationManager();
    }

    private void setupConnections() {
        contextHandler.setSessionManager( sessionManager );
        contextHandler.setRequestManager( requestManager );
        contextHandler.setPap( pap );
        contextHandler.setPdp( pdp );
        contextHandler.setObligationManager( obligationManager );
        contextHandler.setPIPs( new ArrayList<PIPCHInterface>( pipList ) );
        contextHandler.startMonitoringThread();
        requestManager.setContextHandler( contextHandler );
        requestManager.setPEPMap( pepMap );
        for( PIPBase pip : pipList ) {
            pip.setContextHandler( contextHandler );
        }
        pdp.setPap( pap );
        pdp.setObligationManager( obligationManager );
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

    private void buildPolicyDecisionPoint() {
        this.pdp = buildComponent( properties.getPolicyDecisionPoint(), PDPInterface.class ).get(); // NOSONAR
    }

    private void buildPolicyAdministrationPoint() {
        this.pap = buildComponent( properties.getPolicyAdministrationPoint(), PAPInterface.class ).get(); // NOSONAR
    }

    private void buildObligationManager() {
        this.obligationManager = buildComponent( properties.getObligationManager(), ObligationManagerInterface.class ).get(); // NOSONAR
        this.obligationManager.setPIPs( new ArrayList<PIPOMInterface>( pipList ) );
    }

    private void buildPEPList() {
        for( PepProperties pepProp : properties.getPepList() ) {
            Optional<PEPInterface> pep = buildComponent( pepProp, PEPInterface.class ); // NOSONAR
            pepMap.put( pepProp.getId(), pep.get() ); // NOSONAR
        }
    }

    private void buildPIPList() {
        for( PipProperties pipProp : properties.getPipList() ) {
            Optional<PIPBase> pip = buildComponent( pipProp, PIPBase.class );
            pipList.add( pip.get() ); // NOSONAR
        }
    }

    private <T> Optional<T> buildComponent( PluginProperties property, Class<T> clazz ) {
        log.info( "[BUILD] " + property.getName() );
        Optional<T> component = ReflectionsUtility.buildComponent( property, clazz );
        Reject.ifAbsent( component, "Error building " + property.getName() );
        return component;
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

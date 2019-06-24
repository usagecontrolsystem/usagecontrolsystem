package it.cnr.iit.ucsrest.properties;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import it.cnr.iit.ucs.properties.UCSProperties;
import it.cnr.iit.ucs.properties.components.PepProperties;
import it.cnr.iit.ucs.properties.components.PipProperties;

@Component
@ConfigurationProperties( prefix = "ucs", ignoreUnknownFields = true, ignoreInvalidFields = true )
public class UCSRestProperties implements UCSProperties {

    @NestedConfigurationProperty
    private UCSRestCoreProperties core;

    @NestedConfigurationProperty
    private UCSRestContextHandlerProperties contextHandler;

    @NestedConfigurationProperty
    private UCSRestRequestManagerProperties requestManager;

    @NestedConfigurationProperty
    private UCSRestSessionManagerProperties sessionManager;

    @NestedConfigurationProperty
    private UCSRestPapProperties policyAdministrationPoint;

    @NestedConfigurationProperty
    private UCSRestPdpProperties policyDecisionPoint;

    @NestedConfigurationProperty
    private UCSRestObligationManagerProperties obligationManager;

    @NestedConfigurationProperty
    private List<UCSRestPipProperties> pipList;

    @NestedConfigurationProperty
    private List<UCSRestPepProperties> pepList;

    @Override
    public UCSRestContextHandlerProperties getContextHandler() {
        return contextHandler;
    }

    public void setContextHandler( UCSRestContextHandlerProperties contextHandler ) {
        this.contextHandler = contextHandler;
    }

    @Override
    public UCSRestRequestManagerProperties getRequestManager() {
        return requestManager;
    }

    public void setRequestManager( UCSRestRequestManagerProperties requestManager ) {
        this.requestManager = requestManager;
    }

    @Override
    public UCSRestCoreProperties getCore() {
        return core;
    }

    public void setCore( UCSRestCoreProperties core ) {
        this.core = core;
    }

    @Override
    public UCSRestSessionManagerProperties getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager( UCSRestSessionManagerProperties sessionManager ) {
        this.sessionManager = sessionManager;
    }

    @Override
    public UCSRestPapProperties getPolicyAdministrationPoint() {
        return policyAdministrationPoint;
    }

    public void setPolicyAdministrationPoint( UCSRestPapProperties policyAdministrationPoint ) {
        this.policyAdministrationPoint = policyAdministrationPoint;
    }

    @Override
    public UCSRestPdpProperties getPolicyDecisionPoint() {
        return policyDecisionPoint;
    }

    public void setPolicyDecisionPoint( UCSRestPdpProperties policyDecisionPoint ) {
        this.policyDecisionPoint = policyDecisionPoint;
    }

    @Override
    public UCSRestObligationManagerProperties getObligationManager() {
        return obligationManager;
    }

    public void setObligationManager( UCSRestObligationManagerProperties obligationManager ) {
        this.obligationManager = obligationManager;
    }

    @Override
    public List<PipProperties> getPipList() {
        return new ArrayList<>( pipList );
    }

    public void setPipList( List<UCSRestPipProperties> pipList ) {
        this.pipList = pipList;
    }

    @Override
    public List<PepProperties> getPepList() {
        return new ArrayList<>( pepList );
    }

    public void setPepList( List<UCSRestPepProperties> pepList ) {
        this.pepList = pepList;
    }

}
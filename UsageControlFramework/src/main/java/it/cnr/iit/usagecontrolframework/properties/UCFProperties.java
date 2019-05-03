package it.cnr.iit.usagecontrolframework.properties;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import it.cnr.iit.ucs.properties.UCSProperties;
import it.cnr.iit.ucs.properties.components.PepProperties;
import it.cnr.iit.ucs.properties.components.PipProperties;

@Component
@ConfigurationProperties(
    prefix = "ucf",
    ignoreUnknownFields = true,
    ignoreInvalidFields = true )
public class UCFProperties implements UCSProperties {

    @NestedConfigurationProperty
    private UCFGeneralProperties general;

    @NestedConfigurationProperty
    private UCFContextHandlerProperties contextHandler;

    @NestedConfigurationProperty
    private UCFRequestManagerProperties requestManager;

    @NestedConfigurationProperty
    private UCFSessionManagerProperties sessionManager;

    @NestedConfigurationProperty
    private UCFPapProperties policyAdministrationPoint;

    @NestedConfigurationProperty
    private UCFPdpProperties policyDecisionPoint;

    @NestedConfigurationProperty
    private UCFObligationManagerProperties obligationManager;

    @NestedConfigurationProperty
    private List<UCFPipProperties> pipList;

    @NestedConfigurationProperty
    private List<UCFPepProperties> pepList;

    @Override
    public UCFContextHandlerProperties getContextHandler() {
        return contextHandler;
    }

    public void setContextHandler( UCFContextHandlerProperties contextHandler ) {
        this.contextHandler = contextHandler;
    }

    @Override
    public UCFRequestManagerProperties getRequestManager() {
        return requestManager;
    }

    public void setRequestManager( UCFRequestManagerProperties requestManager ) {
        this.requestManager = requestManager;
    }

    @Override
    public UCFGeneralProperties getGeneral() {
        return general;
    }

    public void setGeneral( UCFGeneralProperties general ) {
        this.general = general;
    }

    @Override
    public UCFSessionManagerProperties getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager( UCFSessionManagerProperties sessionManager ) {
        this.sessionManager = sessionManager;
    }

    @Override
    public UCFPapProperties getPolicyAdministrationPoint() {
        return policyAdministrationPoint;
    }

    public void setPolicyAdministrationPoint( UCFPapProperties policyAdministrationPoint ) {
        this.policyAdministrationPoint = policyAdministrationPoint;
    }

    @Override
    public UCFPdpProperties getPolicyDecisionPoint() {
        return policyDecisionPoint;
    }

    public void setPolicyDecisionPoint( UCFPdpProperties policyDecisionPoint ) {
        this.policyDecisionPoint = policyDecisionPoint;
    }

    @Override
    public UCFObligationManagerProperties getObligationManager() {
        return obligationManager;
    }

    public void setObligationManager( UCFObligationManagerProperties obligationManager ) {
        this.obligationManager = obligationManager;
    }

    @Override
    public List<PipProperties> getPipList() {
        return new ArrayList<>( pipList );
    }

    public void setPipList( List<UCFPipProperties> pipList ) {
        this.pipList = pipList;
    }

    @Override
    public List<PepProperties> getPepList() {
        return new ArrayList<>( pepList );
    }

    public void setPepList( List<UCFPepProperties> pepList ) {
        this.pepList = pepList;
    }

}
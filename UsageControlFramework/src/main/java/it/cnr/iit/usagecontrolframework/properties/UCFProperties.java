package it.cnr.iit.usagecontrolframework.properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(
    prefix = "ucf",
    ignoreUnknownFields = true,
    ignoreInvalidFields = true )
public class UCFProperties {

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

    public UCFContextHandlerProperties getContextHandler() {
        return contextHandler;
    }

    public void setContextHandler( UCFContextHandlerProperties contextHandler ) {
        this.contextHandler = contextHandler;
    }

    public UCFRequestManagerProperties getRequestManager() {
        return requestManager;
    }

    public void setRequestManager( UCFRequestManagerProperties requestManager ) {
        this.requestManager = requestManager;
    }

    public UCFGeneralProperties getGeneral() {
        return general;
    }

    public void setGeneral( UCFGeneralProperties general ) {
        this.general = general;
    }

    public UCFSessionManagerProperties getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager( UCFSessionManagerProperties sessionManager ) {
        this.sessionManager = sessionManager;
    }

    public UCFPapProperties getPolicyAdministrationPoint() {
        return policyAdministrationPoint;
    }

    public void setPolicyAdministrationPoint( UCFPapProperties policyAdministrationPoint ) {
        this.policyAdministrationPoint = policyAdministrationPoint;
    }

    public UCFPdpProperties getPolicyDecisionPoint() {
        return policyDecisionPoint;
    }

    public void setPolicyDecisionPoint( UCFPdpProperties policyDecisionPoint ) {
        this.policyDecisionPoint = policyDecisionPoint;
    }

    public UCFObligationManagerProperties getObligationManager() {
        return obligationManager;
    }

    public void setObligationManager( UCFObligationManagerProperties obligationManager ) {
        this.obligationManager = obligationManager;
    }

    public List<UCFPipProperties> getPipList() {
        return pipList;
    }

    public void setPipList( List<UCFPipProperties> pipList ) {
        this.pipList = pipList;
    }

    public List<UCFPepProperties> getPepList() {
        return pepList;
    }

    public void setPepList( List<UCFPepProperties> pepList ) {
        this.pepList = pepList;
    }

}
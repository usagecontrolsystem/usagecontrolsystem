package it.cnr.iit.usagecontrolframework.properties;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(
    prefix = "ucf",
    ignoreUnknownFields = true,
    ignoreInvalidFields = true )
public class UCFProperties {

    @Value( "${base-uri}" )
    private String baseUri;

    @Value( "#{new Boolean('${scheduler-enabled}')}" )
    private boolean schedulerEnabled;

    @Value( "${journal-dir}" )
    private String journalDir;

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri( String baseUri ) {
        this.baseUri = baseUri;
    }

    public boolean isSchedulerEnabled() {
        return schedulerEnabled;
    }

    public void setSchedulerEnabled( boolean schedulerEnabled ) {
        this.schedulerEnabled = schedulerEnabled;
    }

    public String getJournalDir() {
        return journalDir;
    }

    public void setJournalDir( String journalDir ) {
        this.journalDir = journalDir;
    }

    /**********************************************************/

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

    // @Override
    public UCFContextHandlerProperties getContextHandler() {
        return contextHandler;
    }

    public void setContextHandler( UCFContextHandlerProperties contextHandler ) {
        this.contextHandler = contextHandler;
    }

    // @Override
    public UCFRequestManagerProperties getRequestManager() {
        return requestManager;
    }

    public void setRequestManager( UCFRequestManagerProperties requestManager ) {
        this.requestManager = requestManager;
    }

    // @Override
    public UCFSessionManagerProperties getSessionManager() {
        return null;
    }

    // @Override
    public UCFPdpProperties getPolicyDecisionPoint() {
        return null;
    }

    // @Override
    public UCFPapProperties getPolicyAdministrationPoint() {
        return null;
    }

    // @Override
    public UCFObligationManagerProperties getObligationManager() {
        return null;
    }

    // @Override
    public List<UCFPipProperties> getPIPs() {
        return null;
    }

    // @Override
    public List<UCFPepProperties> getPEPs() {
        return null;
    }

}
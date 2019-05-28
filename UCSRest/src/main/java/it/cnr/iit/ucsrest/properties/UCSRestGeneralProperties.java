package it.cnr.iit.ucsrest.properties;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.GeneralProperties;

public class UCSRestGeneralProperties implements GeneralProperties {

    @Value( "${base-uri}" )
    private String baseUri;

    @Value( "#{new Boolean('${scheduler-enabled}')}" )
    private boolean schedulerEnabled;

    @Value( "${journal-dir}" )
    private String journalDir;

    @Override
    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri( String baseUri ) {
        this.baseUri = baseUri;
    }

    @Override
    public boolean isSchedulerEnabled() {
        return schedulerEnabled;
    }

    public void setSchedulerEnabled( boolean schedulerEnabled ) {
        this.schedulerEnabled = schedulerEnabled;
    }

    @Override
    public String getJournalDir() {
        return journalDir;
    }

    public void setJournalDir( String journalDir ) {
        this.journalDir = journalDir;
    }

}

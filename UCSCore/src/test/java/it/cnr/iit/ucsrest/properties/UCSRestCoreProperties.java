package it.cnr.iit.ucsrest.properties;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.CoreProperties;

public class UCSRestCoreProperties implements CoreProperties {

    @Value( "${uri}" )
    private String uri;

    @Value( "${journal-dir}" )
    private String journalDir;

    @Override
    public String getUri() {
        return uri;
    }

    public void setUri( String uri ) {
        this.uri = uri;
    }

    @Override
    public String getJournalDir() {
        return journalDir;
    }

    public void setJournalDir( String journalDir ) {
        this.journalDir = journalDir;
    }

}

package it.cnr.iit.ucsrest.properties;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.PdpProperties;

public class UCSRestPdpProperties extends BaseProperties implements PdpProperties {

    @Value( "${journal-dir}" )
    private String journalDir;

    @Override
    public String getJournalDir() {
        return journalDir;
    }

    public void setJournalDir( String journalDir ) {
        this.journalDir = journalDir;
    }

}

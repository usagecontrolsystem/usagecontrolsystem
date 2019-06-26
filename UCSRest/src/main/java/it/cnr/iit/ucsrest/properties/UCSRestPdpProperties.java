package it.cnr.iit.ucsrest.properties;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.PdpProperties;

public class UCSRestPdpProperties extends BaseProperties implements PdpProperties {

    @Value( "${journal-path}" )
    private String journalPath;

    @Value( "${journal-protocol}" )
    private String journalProtocol;

    public void setJournalPath( String journalPath ) {
        this.journalPath = journalPath;
    }

    @Override
    public String getJournalPath() {
        return journalPath;
    }

    @Override
    public String getJournalProtocol() {
        return journalProtocol;
    }

    public void setJournalProtocol( String journalProtocol ) {
        this.journalProtocol = journalProtocol;
    }

    @Override
    public Map<String, String> getJournalAdditionalProperties() {
        return new HashMap<>();
    }

}

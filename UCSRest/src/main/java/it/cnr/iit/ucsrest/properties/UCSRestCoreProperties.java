package it.cnr.iit.ucsrest.properties;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.CoreProperties;

public class UCSRestCoreProperties implements CoreProperties {

    @Value( "${uri}" )
    private String uri;

    @Value( "${journal-path}" )
    private String journalPath;

    @Value( "${journal-protocol}" )
    private String journalProtocol;

    @Override
    public String getUri() {
        return uri;
    }

    public void setUri( String uri ) {
        this.uri = uri;
    }

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

package it.cnr.iit.ucsrest.properties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.PipProperties;

public class UCSRestPipProperties extends BaseProperties implements PipProperties {

    @Value( "${journal-path}" )
    private String journalPath;

    @Value( "${journal-protocol}" )
    private String journalProtocol;

    @Value( "${attributes}" )
    private List<Map<String, String>> attributes;

    public void setJournalPath( String journalPath ) {
        this.journalPath = journalPath;
    }

    @Override
    public boolean isMultiAttribute() {
        return attributes != null && attributes.size() > 1;
    }

    @Override
    public List<Map<String, String>> getAttributes() {
        return attributes;
    }

    public void setAttributes( List<Map<String, String>> attributes ) {
        this.attributes = attributes;
    }

    @Override
    public String getJournalPath() {
        return journalPath;
    }

    @Override
    public String getJournalProtocol() {
        return journalProtocol;
    }

    @Override
    public Map<String, String> getJournalAdditionalProperties() {
        return new HashMap<>();
    }

}

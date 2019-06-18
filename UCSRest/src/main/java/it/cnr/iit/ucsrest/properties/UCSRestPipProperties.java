package it.cnr.iit.ucsrest.properties;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.PipProperties;

public class UCSRestPipProperties extends BaseProperties implements PipProperties {

    @Value( "${journalDir}" )
    private String journalDir;

    @Value( "${attributes}" )
    private List<Map<String, String>> attributes;

    @Override
    public String getJournalDir() {
        return journalDir;
    }

    public void setJournalDir( String journalDir ) {
        this.journalDir = journalDir;
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

}

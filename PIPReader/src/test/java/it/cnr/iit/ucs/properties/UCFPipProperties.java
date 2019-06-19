package it.cnr.iit.ucs.properties;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.cnr.iit.ucs.properties.components.PipProperties;

public class UCFPipProperties implements PipProperties {

    private String className;
    private String id;
    private String journalDir;
    private List<Map<String, String>> attributes;

    @Override
    public String getName() {
        return className;
    }

    public void setClassName( String className ) {
        this.className = className;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    @Override
    public String getJournalDir() {
        return journalDir;
    }

    public void setJournalDir( String journalDir ) {
        this.journalDir = journalDir;
    }

    @Override
    @JsonIgnore
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
    public Map<String, String> getAdditionalProperties() {
        // TODO Auto-generated method stub
        return null;
    }

}

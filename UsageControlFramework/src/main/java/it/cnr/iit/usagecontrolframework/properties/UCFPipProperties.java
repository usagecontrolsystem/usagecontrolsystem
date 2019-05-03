package it.cnr.iit.usagecontrolframework.properties;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.PipProperties;

public class UCFPipProperties implements PipProperties {

    @Value( "${class-name}" )
    private String className;

    @Value( "${communication-type}" )
    private String communicationType;

    @Value( "${id}" )
    private String id;

    @Value( "${journalDir}" )
    private String journalDir;

    @Value( "${attributes}" )
    private List<Map<String, String>> attributes;

    @Override
    public String getClassName() {
        return className;
    }

    public void setClassName( String className ) {
        this.className = className;
    }

    @Override
    public String getCommunicationType() {
        return communicationType;
    }

    public void setCommunicationType( String communicationType ) {
        this.communicationType = communicationType;
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

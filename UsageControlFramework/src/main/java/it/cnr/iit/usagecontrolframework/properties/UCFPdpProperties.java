package it.cnr.iit.usagecontrolframework.properties;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.PdpProperties;

public class UCFPdpProperties implements PdpProperties {

    @Value( "${class-name}" )
    private String className;

    @Value( "${communication-type}" )
    private String communicationType;

    @Value( "${journal-dir}" )
    private String journalDir;

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getCommunicationType() {
        return communicationType;
    }

    @Override
    public String getJournalDir() {
        return journalDir;
    }

    public void setClassName( String className ) {
        this.className = className;
    }

    public void setCommunicationType( String communicationType ) {
        this.communicationType = communicationType;
    }

    public void setJournalDir( String journalDir ) {
        this.journalDir = journalDir;
    }

}

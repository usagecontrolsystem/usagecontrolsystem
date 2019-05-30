package it.cnr.iit.ucsrest.properties;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.PapProperties;

public class UCSRestPapProperties implements PapProperties {

    @Value( "${class-name}" )
    private String className;

    @Value( "${communication-type}" )
    private String communicationType;

    @Value( "${path}" )
    private String path;

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getCommunicationType() {
        return communicationType;
    }

    @Override
    public String getPath() {
        return path;
    }

    public void setClassName( String className ) {
        this.className = className;
    }

    public void setCommunicationType( String communicationType ) {
        this.communicationType = communicationType;
    }

    public void setPath( String path ) {
        this.path = path;
    }
}

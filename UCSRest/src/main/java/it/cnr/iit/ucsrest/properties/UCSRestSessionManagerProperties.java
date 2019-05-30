package it.cnr.iit.ucsrest.properties;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.SessionManagerProperties;

public class UCSRestSessionManagerProperties implements SessionManagerProperties {

    @Value( "${class-name}" )
    private String className;

    @Value( "${communication-type}" )
    private String communicationType;

    @Value( "${type}" )
    private String type;

    @Value( "${db-uri}" )
    private String dbUri;

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getCommunicationType() {
        return communicationType;
    }

    public void setClassName( String className ) {
        this.className = className;
    }

    public void setCommunicationType( String communicationType ) {
        this.communicationType = communicationType;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    @Override
    public String getDbUri() {
        return dbUri;
    }

    public void setDbUri( String dbUri ) {
        this.dbUri = dbUri;
    }

}

package it.cnr.iit.ucsrest.properties;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.ObligationManagerProperties;

public class UCSRestObligationManagerProperties implements ObligationManagerProperties {

    @Value( "${class-name}" )
    private String className;

    @Value( "${communication-type}" )
    private String communicationType;

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

}

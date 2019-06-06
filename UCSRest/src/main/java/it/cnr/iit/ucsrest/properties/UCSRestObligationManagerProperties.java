package it.cnr.iit.ucsrest.properties;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.ObligationManagerProperties;

public class UCSRestObligationManagerProperties implements ObligationManagerProperties {

    @Value( "${name}" )
    private String name;

    @Value( "${communication}" )
    private String communication;

    @Override
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

}

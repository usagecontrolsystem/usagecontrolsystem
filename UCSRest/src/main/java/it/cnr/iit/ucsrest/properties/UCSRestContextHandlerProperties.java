package it.cnr.iit.ucsrest.properties;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.ContextHandlerProperties;

public class UCSRestContextHandlerProperties implements ContextHandlerProperties {

    @Value( "${name}" )
    private String name;

    @Value( "${uri}" )
    private String uri;

    @Override
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public String getUri() {
        return uri;
    }

    public void setUri( String uri ) {
        this.uri = uri;
    }

}

package it.cnr.iit.ucsrest.properties;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.ContextHandlerProperties;

public class UCSRestContextHandlerProperties extends BaseProperties implements ContextHandlerProperties {

    @Value( "${uri}" )
    private String uri;

    @Override
    public String getUri() {
        return uri;
    }

    public void setUri( String uri ) {
        this.uri = uri;
    }

    @Override
    public Map<String, String> getAdditionalProperties() {
        return null;
    }

}

package it.cnr.iit.ucsrest.properties;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.base.CommonProperties;

public abstract class BaseProperties implements CommonProperties {

    @Value( "${name}" )
    private String name;

    @Value( "${additional-properties}" )
    private Map<String, String> additionalProperties;

    @Value( "${id}" )
    private String id;

    @Override
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public Map<String, String> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties( Map<String, String> additionalProperties ) {
        this.additionalProperties = additionalProperties;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

}

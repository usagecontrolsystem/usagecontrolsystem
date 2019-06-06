package it.cnr.iit.ucsrest.properties;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.PapProperties;

public class UCSRestPapProperties implements PapProperties {

    @Value( "${name}" )
    private String name;

    @Value( "${communication}" )
    private String communication;

    @Value( "${path}" )
    private String path;

    @Override
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public String getPath() {
        return path;
    }

    public void setPath( String path ) {
        this.path = path;
    }
}

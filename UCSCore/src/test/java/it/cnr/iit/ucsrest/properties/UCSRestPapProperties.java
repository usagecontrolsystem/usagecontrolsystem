package it.cnr.iit.ucsrest.properties;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.PapProperties;

public class UCSRestPapProperties extends BaseProperties implements PapProperties {

    @Value( "${path}" )
    private String path;

    @Override
    public String getPath() {
        return path;
    }

    public void setPath( String path ) {
        this.path = path;
    }
}

package it.cnr.iit.ucsrest.properties;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.SessionManagerProperties;

public class UCSRestSessionManagerProperties implements SessionManagerProperties {

    @Value( "${name}" )
    private String name;

    @Value( "${db-uri}" )
    private String dbUri;

    @Override
    public String getName() {
        return name;
    }

    public void setName( String className ) {
        this.name = className;
    }

    @Override
    public String getDbUri() {
        return dbUri;
    }

    public void setDbUri( String dbUri ) {
        this.dbUri = dbUri;
    }

}

package it.cnr.iit.ucsrest.properties;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.SessionManagerProperties;

public class UCSRestSessionManagerProperties extends BaseProperties implements SessionManagerProperties {

    @Value( "${db-uri}" )
    private String dbUri;

    @Override
    public String getDbUri() {
        return dbUri;
    }

    public void setDbUri( String dbUri ) {
        this.dbUri = dbUri;
    }

}

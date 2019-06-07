package it.cnr.iit.ucsrest.properties;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.RequestManagerProperties;

public class UCSRestRequestManagerProperties implements RequestManagerProperties {

    @Value( "${name}" )
    private String name;

    @Value( "${api-remote-response}" )
    private String apiRemoteResponse;

    @Value( "${active}" )
    private boolean active;

    @Override
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public String getApiRemoteResponse() {
        return apiRemoteResponse;
    }

    public void setApiRemoteResponse( String apiRemoteResponse ) {
        this.apiRemoteResponse = apiRemoteResponse;
    }

    public void setActive( boolean active ) {
        this.active = active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

}

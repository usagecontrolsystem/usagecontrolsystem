package it.cnr.iit.ucsrest.properties;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.RequestManagerProperties;

public class UCSRestRequestManagerProperties extends BaseProperties implements RequestManagerProperties {

    @Value( "${api-remote-response}" )
    private String apiRemoteResponse;

    @Value( "${active}" )
    private boolean active;

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

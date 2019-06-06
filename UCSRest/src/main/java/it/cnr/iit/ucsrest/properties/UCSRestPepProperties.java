package it.cnr.iit.ucsrest.properties;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.PepProperties;

public class UCSRestPepProperties implements PepProperties {

    @Value( "${name}" )
    private String name;

    @Value( "${communication:LOCAL}" )
    private String communication;

    @Value( "${base-uri}" )
    private String uri;

    @Value( "${id:1}" )
    private String id;

    @Value( "${revoke-type:SOFT}" )
    private String revokeType;

    @Value( "${api-ongoing-evaluation}" )
    private String apiOngoingEvaluation;

    @Value( "${api-try-access-response}" )
    private String apiTryAccessResponse;

    @Value( "${api-start-access-response}" )
    private String apiStartAccessResponse;

    @Value( "${api-end-access-response}" )
    private String apiEndAccessResponse;

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

    @Override
    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    @Override
    public String getRevokeType() {
        return revokeType;
    }

    public void setRevokeType( String revokeType ) {
        this.revokeType = revokeType;
    }

    @Override
    public String getApiOngoingEvaluation() {
        return apiOngoingEvaluation;
    }

    public void setApiOngoingEvaluation( String apiOngoingEvaluation ) {
        this.apiOngoingEvaluation = apiOngoingEvaluation;
    }

    @Override
    public String getApiTryAccessResponse() {
        return apiTryAccessResponse;
    }

    public void setApiTryAccessResponse( String apiTryAccessResponse ) {
        this.apiTryAccessResponse = apiTryAccessResponse;
    }

    @Override
    public String getApiStartAccessResponse() {
        return apiStartAccessResponse;
    }

    public void setApiStartAccessResponse( String apiStartAccessResponse ) {
        this.apiStartAccessResponse = apiStartAccessResponse;
    }

    @Override
    public String getApiEndAccessResponse() {
        return apiEndAccessResponse;
    }

    public void setApiEndAccessResponse( String apiEndAccessResponse ) {
        this.apiEndAccessResponse = apiEndAccessResponse;
    }

}

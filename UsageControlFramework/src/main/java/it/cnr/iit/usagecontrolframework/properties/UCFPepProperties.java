package it.cnr.iit.usagecontrolframework.properties;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.PepProperties;

public class UCFPepProperties implements PepProperties {

    @Value( "${class-name}" )
    private String className;

    @Value( "${communication-type:API}" )
    private String communicationType;

    @Value( "${base-uri}" )
    private String baseUri;

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
    public String getClassName() {
        return className;
    }

    @Override
    public String getCommunicationType() {
        return communicationType;
    }

    @Override
    public String getBaseUri() {
        return baseUri;
    }

    public void setClassName( String className ) {
        this.className = className;
    }

    public void setCommunicationType( String communicationType ) {
        this.communicationType = communicationType;
    }

    public void setBaseUri( String baseUri ) {
        this.baseUri = baseUri;
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

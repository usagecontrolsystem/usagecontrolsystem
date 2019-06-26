package it.cnr.iit.ucsrest.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.tngtech.jgiven.integration.spring.EnableJGiven;

@Configuration
@EnableJGiven
public class UCSRestTestContext {

    @Value( "${ucstest.policy-file}" )
    private String policyFile;

    @Value( "${ucstest.policy-file-deny}" )
    private String policyFileDeny;

    @Value( "${ucstest.request-file}" )
    private String requestFile;

    @Value( "${ucstest.pep-id}" )
    private String pepId;

    @Value( "${ucstest.session-id}" )
    private String sessionId;

    public String getPolicyFile() {
        return policyFile;
    }

    public void setPolicyFile( String policyFile ) {
        this.policyFile = policyFile;
    }

    public String getRequestFile() {
        return requestFile;
    }

    public void setRequestFile( String requestFile ) {
        this.requestFile = requestFile;
    }

    public String getPepId() {
        return pepId;
    }

    public void setPepId( String pepId ) {
        this.pepId = pepId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId( String sessionId ) {
        this.sessionId = sessionId;
    }

    public String getPolicyFileDeny() {
        return policyFileDeny;
    }

    public void setPolicyFileDeny( String policyFileWithDeny ) {
        this.policyFileDeny = policyFileWithDeny;
    }

}

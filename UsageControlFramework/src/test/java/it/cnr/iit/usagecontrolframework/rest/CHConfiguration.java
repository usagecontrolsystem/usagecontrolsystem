package it.cnr.iit.usagecontrolframework.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;

@EnableConfigurationProperties
@TestPropertySource( properties = "application-test.properties" )
@SpringBootConfiguration
public class CHConfiguration {
    @Value( "${ucs-config-file}" )
    private String ucsConfigFile;

    @Value( "${policy-file}" )
    private String policyFile;

    @Value( "${request-file}" )
    private String requestFile;

    @Value( "${pep-id}" )
    private String pepId;

    @Value( "${session-id}" )
    private String sessionId;

    @Value( "${ucs-uri}" )
    private String ucsUri;

    public String getUcsConfigFile() {
        return ucsConfigFile;
    }

    public void setUcsConfigFile( String ucsConfigFile ) {
        this.ucsConfigFile = ucsConfigFile;
    }

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

    public String getUcsUri() {
        return ucsUri;
    }

    public void setUcsUri( String ucsUri ) {
        this.ucsUri = ucsUri;
    }
}

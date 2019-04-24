package it.cnr.iit.peprest.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

import com.tngtech.jgiven.integration.spring.EnableJGiven;

@Configuration
@EnableJGiven
@ComponentScan( basePackages = { "it.cnr.iit.peprest" } )
@EnableConfigurationProperties
@TestPropertySource( properties = "application.properties" )
@SpringBootConfiguration
public class PEPRestTestContext {

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

    @Value( "${ucs.base-uri}" )
    private String ucsBaseUri;

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

    public String getUcsBaseUri() {
        return ucsBaseUri;
    }

    public void setUcsBaseUri( String ucsUri ) {
        ucsBaseUri = ucsUri;
    }
}

package it.cnr.iit.usagecontrolframework.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

import com.tngtech.jgiven.integration.spring.EnableJGiven;

@Configuration
@EnableJGiven
@ComponentScan( basePackages = { "it.cnr.iit.usagecontrolframework.rest" } )
@EnableConfigurationProperties
@TestPropertySource( properties = "application.properties" )
@SpringBootConfiguration
public class UCFTestContext {

    @Value( "${policy-file}" )
    private String policyFile;

    @Value( "${request-file}" )
    private String requestFile;

    @Value( "${pep-id}" )
    private String pepId;

    @Value( "${session-id}" )
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

}

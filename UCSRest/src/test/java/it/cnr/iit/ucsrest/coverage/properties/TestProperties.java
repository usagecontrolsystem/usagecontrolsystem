package it.cnr.iit.ucsrest.coverage.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties( prefix = "ucstest" )
public class TestProperties {

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

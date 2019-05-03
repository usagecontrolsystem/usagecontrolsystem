package it.cnr.iit.usagecontrolframework.properties;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.ContextHandlerProperties;

public class UCFContextHandlerProperties implements ContextHandlerProperties {

    @Value( "${class-name}" )
    private String className;

    @Value( "${communication-type}" )
    private String communicationType;

    @Value( "${base-uri}" )
    private String baseUri;

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

}

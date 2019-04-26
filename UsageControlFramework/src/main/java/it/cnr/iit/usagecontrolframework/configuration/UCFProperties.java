package it.cnr.iit.usagecontrolframework.configuration;

import java.io.File;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import it.cnr.iit.ucs.configuration.UCSConfiguration;
import it.cnr.iit.utility.JsonUtility;

@ConfigurationProperties( prefix = "ucf" )
public class UCFProperties {

    @Value( "${base-uri}" )
    private String baseUri;

    @Value( "${rest}" )
    private String rest;

    @Value( "${distributedType}" )
    private String distributedType;

    @Value( "${schedulerEnabled}" )
    private boolean schedulerEnabled;

    @Deprecated
    @Value( "${conf-file}" )
    private String configFile;

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri( String baseUri ) {
        this.baseUri = baseUri;
    }

    public String getRest() {
        return rest;
    }

    public void setRest( String rest ) {
        this.rest = rest;
    }

    public String getDistributedType() {
        return distributedType;
    }

    public void setDistributedType( String distributedType ) {
        this.distributedType = distributedType;
    }

    public boolean isSchedulerEnabled() {
        return schedulerEnabled;
    }

    public void setSchedulerEnabled( boolean schedulerEnabled ) {
        this.schedulerEnabled = schedulerEnabled;
    }

    @Deprecated
    public String getConfigFile() {
        return configFile;
    }

    @Deprecated
    public void setConfigFile( String configFile ) {
        this.configFile = configFile;
    }

    @Deprecated
    public Optional<UCSConfiguration> getConfiguration() {
        // TODO UCS-33 NOSONAR
        ClassLoader classLoader = UCFProperties.class.getClassLoader();
        File confFile = new File( classLoader.getResource( configFile ).getFile() );

        return JsonUtility.loadObjectFromJsonFile( confFile, UCSConfiguration.class );
    }

}
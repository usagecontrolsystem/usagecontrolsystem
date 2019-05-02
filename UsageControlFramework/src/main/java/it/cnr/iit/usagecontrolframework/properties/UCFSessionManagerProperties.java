package it.cnr.iit.usagecontrolframework.properties;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.SessionManagerProperties;
import it.cnr.iit.ucs.properties.components.Table;

public class UCFSessionManagerProperties implements SessionManagerProperties {

    @Value( "${class-name}" )
    private String className;

    @Value( "${communication-type}" )
    private String communicationType;

    @Value( "${base-uri}" )
    private String baseUri;

    @Value( "${type}" )
    private String type;

    @Value( "${host}" )
    private String host;

    @Value( "${cluster}" )
    private String cluster;

    @Value( "${keyspace}" )
    private String keyspace;

    @Value( "${username}" )
    private String username;

    @Value( "${password}" )
    private String password;

    @Value( "${replication-factor}" )
    private String replicationFactor;

    @Value( "${key}" )
    private String key;

    @Value( "${db-uri}" )
    private String dbUri;

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
    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    @Override
    public String getHost() {
        return host;
    }

    public void setHost( String host ) {
        this.host = host;
    }

    @Override
    public String getCluster() {
        return cluster;
    }

    public void setCluster( String cluster ) {
        this.cluster = cluster;
    }

    @Override
    public String getKeyspace() {
        return keyspace;
    }

    public void setKeyspace( String keyspace ) {
        this.keyspace = keyspace;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    @Override
    public String getReplicationFactor() {
        return replicationFactor;
    }

    public void setReplicationFactor( String replicationFactor ) {
        this.replicationFactor = replicationFactor;
    }

    @Override
    public String getDbUri() {
        return dbUri;
    }

    public void setDbUri( String dbUri ) {
        this.dbUri = dbUri;
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey( String key ) {
        this.key = key;
    }

    @Override
    public List<Table> getTables() {
        return null;
    }

}

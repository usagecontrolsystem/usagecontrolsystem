/*******************************************************************************
 * Copyright 2018 IIT-CNR
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package it.cnr.iit.ucs.configuration.fields.sessionManager;

import java.util.List;

/**
 * Session manager properties
 *
 * @author antonio
 *
 */
public class SessionManagerProperties {

    private String type;
    private String ip;
    private String cluster;
    private String keyspace;
    private String username;
    private String password;
    private String communication;
    private String className;
    private String replicationFactor;
    private String key;
    private String host;
    private String driver;
    private List<Table> tables;

    public String getType() {
        return type;
    }

    public String getIp() {
        return ip;
    }

    public String getCluster() {
        return cluster;
    }

    public String getKeyspace() {
        return keyspace;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Table> getTables() {
        return tables;
    }

    public String getCommunication() {
        return communication;
    }

    public String getClassName() {
        return className;
    }

    public String getReplicationFactor() {
        return replicationFactor;
    }

    public String getKey() {
        return key;
    }

    public String getHost() {
        return host;
    }

    public String getDriver() {
        return driver;
    }

}

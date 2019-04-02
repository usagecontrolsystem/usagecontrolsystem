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
package it.cnr.iit.ucs.configuration.pip;

public class PipRegisterProperties {

    private String driver;
    private String className;
    private String cluster;
    private String keyspace;
    private String replicator;
    private String host;
    private String port;

    public String getDriver() {
        return driver;
    }

    public String getClassName() {
        return className;
    }

    public String getCluster() {
        return cluster;
    }

    public String getKeySpace() {
        return keyspace;
    }

    public String getReplicator() {
        return replicator;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

}

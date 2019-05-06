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
package it.cnr.iit.peprest.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties( prefix = "pep" )
public class PEPProperties {

    @Value( "${id:1}" )
    private String id;

    @Value( "${base-uri}" )
    private String baseUri;

    @Value( "${revoke-type:HARD}" )
    private String revokeType;

    @Value( "${policy-path}" )
    private String policyPath;

    @Value( "${request-path}" )
    private String requestPath;

    @Value( "${api-status-changed}" )
    private String apiStatusChanged;

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri( String baseUri ) {
        this.baseUri = baseUri;
    }

    public String getRevokeType() {
        return revokeType;
    }

    public void setRevokeType( String revokeType ) {
        this.revokeType = revokeType;
    }

    public String getPolicyPath() {
        return policyPath;
    }

    public void setPolicyPath( String policyPath ) {
        this.policyPath = policyPath;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath( String requestPath ) {
        this.requestPath = requestPath;
    }

    public String getApiStatusChanged() {
        return apiStatusChanged;
    }

    public void setApiStatusChanged( String apiStatusChanged ) {
        this.apiStatusChanged = apiStatusChanged;
    }

}

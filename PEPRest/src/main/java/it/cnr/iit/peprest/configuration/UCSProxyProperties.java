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

@ConfigurationProperties( prefix = "ucs" )
public class UCSProxyProperties {

    @Value( "${base-uri}" )
    private String baseUri;

    @Value( "${api-try-access}" )
    private String apiTryAccess;

    @Value( "${api-start-access}" )
    private String apiStartAccess;

    @Value( "${api-end-access}" )
    private String apiEndAccess;

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri( String baseUri ) {
        this.baseUri = baseUri;
    }

    public String getApiTryAccess() {
        return apiTryAccess;
    }

    public void setApiTryAccess( String apiTryAccess ) {
        this.apiTryAccess = apiTryAccess;
    }

    public String getApiStartAccess() {
        return apiStartAccess;
    }

    public void setApiStartAccess( String apiStartAccess ) {
        this.apiStartAccess = apiStartAccess;
    }

    public String getApiEndAccess() {
        return apiEndAccess;
    }

    public void setApiEndAccess( String apiEndAccess ) {
        this.apiEndAccess = apiEndAccess;
    }

}

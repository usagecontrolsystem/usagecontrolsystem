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
public class UCSProperties {

    @Value( "${base-uri}" )
    private String baseUri;

    @Value( "${try-access-api}" )
    private String tryAccessApi;

    @Value( "${start-access-api}" )
    private String startAccessApi;

    @Value( "${end-access-api}" )
    private String endAccessApi;

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri( String baseUri ) {
        this.baseUri = baseUri;
    }

    public String getTryAccessApi() {
        return tryAccessApi;
    }

    public void setTryAccessApi( String tryAccessApi ) {
        this.tryAccessApi = tryAccessApi;
    }

    public String getStartAccessApi() {
        return startAccessApi;
    }

    public void setStartAccessApi( String startAccessApi ) {
        this.startAccessApi = startAccessApi;
    }

    public String getEndAccessApi() {
        return endAccessApi;
    }

    public void setEndAccessApi( String endAccessApi ) {
        this.endAccessApi = endAccessApi;
    }

}

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

final public class PEPRestConfiguration {

    private PEPConf pepConf;

    private RequestManagerConf requestManagerConf;

    public PEPRestConfiguration() {
        super();
    }

    public PEPConf getPepConf() {
        return pepConf;
    }

    public RequestManagerConf getRequestManagerConf() {
        return requestManagerConf;
    }

}

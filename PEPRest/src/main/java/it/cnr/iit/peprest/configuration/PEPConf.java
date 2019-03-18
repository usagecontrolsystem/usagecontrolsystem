/*******************************************************************************
 * Copyright 2018 IIT-CNR
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package it.cnr.iit.peprest.configuration;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "pep")
public class PEPConf {
  @XmlElement(name = "ip")
  private String ip;

  @XmlElement(name = "port")
  private String port;

  @XmlElement(name = "response")
  private String response;

  @XmlElement(name = "revoke")
  private String revoke;

  @XmlElement(name = "statuschanged")
  private String statusChanged;

  @XmlElement(name = "policypath")
  private String policyPath;

  @XmlElement(name = "requestpath")
  private String requestPath;

  @XmlElement(name = "pepid")
  private String id;

  public String getIp() {
    return ip;
  }

  public String getPort() {
    return port;
  }

  public String getResponse() {
    return response;
  }

  public String getStatusChanged() {
    return statusChanged;
  }

  public String getRevoke() {
    return revoke;
  }

  public String getPolicyPath() {
    return policyPath;
  }

  public String getRequestPath() {
    return requestPath;
  }

  public String getId() {
    return id;
  }

}

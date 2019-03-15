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
package it.cnr.iit.ucs.configuration.xmlclasses;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class XMLPep {

  @XmlElement(name = "class")
  private String className;

  @XmlElement(name = "communication")
  private String communication;

  @XmlElement(name = "ip")
  private String ip;

  @XmlElement(name = "port")
  private String port;

  @XmlElement(name = "revoke")
  private String revoke = "SOFT";

  @XmlElement(name = "oeApi")
  private String oeApi;

  @XmlElement(name = "tryAccessApi")
  private String tryAccessApi;

  @XmlElement(name = "startAccessApi")
  private String startAccessApi;

  @XmlElement(name = "endAccessApi")
  private String endAccessApi;

  @XmlElement(name = "id")
  private String id;

  public String getCommunication() {
    return communication;
  }

  public String getIp() {
    return ip;
  }

  public String getPort() {
    return port;
  }

  public String getClassName() {
    return className;
  }

  public String getRevoke() {
    return revoke;
  }

  public String getOnGoingEvaluation() {
    return oeApi;
  }

  public String getTryAccessResponse() {
    return tryAccessApi;
  }

  public String getStartAccessResponse() {
    return startAccessApi;
  }

  public String getEndAccessResponse() {
    return endAccessApi;
  }

  public String getId() {
    return id;
  }

}

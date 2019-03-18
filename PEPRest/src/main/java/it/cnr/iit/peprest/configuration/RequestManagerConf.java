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

@XmlRootElement(name = "requestmanager")
public class RequestManagerConf {
	@XmlElement(name = "ip")
	private String	ip;
	
	@XmlElement(name = "port")
	private String	port;
	
	@XmlElement(name = "try")
	private String	tryAccess;
	
	@XmlElement(name = "start")
	private String	startAccess;
	
	@XmlElement(name = "end")
	private String	endAccess;
	
	public String getIp() {
		return ip;
	}
	
	public String getPort() {
		return port;
	}
	
	public String getTryAccess() {
		return tryAccess;
	}
	
	public String getStartAccess() {
		return startAccess;
	}
	
	public String getEndAccess() {
		return endAccess;
	}
	
}

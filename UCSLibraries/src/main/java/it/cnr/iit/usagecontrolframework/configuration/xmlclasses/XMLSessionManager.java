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
package it.cnr.iit.usagecontrolframework.configuration.xmlclasses;

import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import it.cnr.iit.utility.JAXBUtility;

/**
 * Session manager xml
 * 
 * @author antonio
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLSessionManager {
	
	@XmlElement(name = "type")
	private String					type;
	
	@XmlElement(name = "ip")
	private String					ip;
	
	@XmlElement(name = "cluster")
	private String					cluster;
	
	@XmlElement(name = "keyspace")
	private String					keyspace;
	
	@XmlElement(name = "username")
	private String					username;
	
	@XmlElement(name = "password")
	private String					password;
	
	@XmlElement(name = "communication")
	private String					communication;
	
	@XmlElement(name = "class")
	private String					className;
	
	@XmlElement(name = "replicator")
	private String					replicator;
	
	@XmlElement(name = "key")
	private String					key;
	
	@XmlElement(name = "host")
	private String					host;
	
	@XmlElement(name = "driver")
	private String					driver;
	
	@XmlElementWrapper(name = "tables")
	@XmlElement(name = "table")
	private List<XMLTable>	tables;
	
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
	
	public List<XMLTable> getTables() {
		return tables;
	}
	
	public String getCommunication() {
		return communication;
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getReplicationFactor() {
		return replicator;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getSMAsString() throws JAXBException {
		return JAXBUtility.marshalToString(XMLSessionManager.class, this, "sm",
		    null);
	}
	
	public String getHost() {
		return host;
	}
	
	public String getDriver() {
		return driver;
	}
	
}

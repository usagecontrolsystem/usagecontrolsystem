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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * This class rpresnets the xml configuration for a Kademlia node.
 * <p>
 * The configuration of a Kademlia node requires the informations about the
 * contact itself amd some informations about the tables and the contact it has
 * to ping in order to create the network
 * </p>
 * 
 * @author antonio
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLKademliaConfiguration {
	
	@XmlElement(name = "nodeId")
	private String										nodeId;
	
	@XmlElement(name = "address")
	private String										address;
	
	@XmlElement(name = "udpPort")
	private int												udpPort;
	
	@XmlElement(name = "threads")
	private int												threads;
	
	@XmlElementWrapper(name = "contacts")
	@XmlElement(name = "contact")
	private List<XMLKademliaContact>	contacts	= new ArrayList<>();
	
	public String getNodeId() {
		return nodeId;
	}
	
	public String getAddress() {
		return address;
	}
	
	public int getPort() {
		return udpPort;
	}
	
	public int getThreads() {
		return threads;
	}
	
	public List<XMLKademliaContact> getContacts() {
		return contacts;
	}
	
}

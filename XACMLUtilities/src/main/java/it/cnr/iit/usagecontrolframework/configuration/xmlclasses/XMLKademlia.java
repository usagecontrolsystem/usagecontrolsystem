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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class XMLKademlia {
	@XmlAttribute(name = "id")
	private String									nodeId;
	@XmlAttribute(name = "ip")
	private String									ip;
	@XmlAttribute(name = "udpPort")
	private int											udpPort;
	@XmlAttribute(name = "owner")
	private String									owner;
	@XmlAttribute(name = "boot")
	private String									boot;
	@XmlAttribute(name = "number")
	private int											number;
	
	@XmlElement(name = "nodes")
	private ArrayList<XMLKademlia>	list	= new ArrayList<>();
	
	public String getNodeId() {
		return nodeId;
	}
	
	public String getIp() {
		return ip;
	}
	
	public int getUdpPort() {
		return udpPort;
	}
	
	public ArrayList<XMLKademlia> getList() {
		return list;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public boolean getBoot() {
		return Boolean.valueOf(boot);
	}
	
	public int getNumber() {
		return number;
	}
	
}

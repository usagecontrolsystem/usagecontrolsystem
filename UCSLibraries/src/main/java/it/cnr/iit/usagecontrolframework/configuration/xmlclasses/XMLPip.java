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

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import it.cnr.iit.utility.JAXBUtility;

/**
 * PIP xml
 * 
 * @author antonio
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public final class XMLPip {
	@XmlAttribute(name = "id")
	String							id;
	@XmlAttribute(name = "class")
	String							className;
	
	@XmlAttribute(name = "retrieval")
	String							retrieval;
	
	@XmlElement(name = "attributes")
	List<XMLAttribute>	attributes			= new ArrayList<XMLAttribute>();
	
	@XmlElement(name = "table")
	XMLTable						xmlTable;
	
	@XmlElement(name = "connection")
	String							connection;
	
	@XmlElement(name = "multiattribute")
	String							multiattribute	= "false";
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getRetrieval() {
		return retrieval;
	}
	
	public List<XMLAttribute> getAttributes() {
		return attributes;
	}
	
	/*
	 * public Map<String, String> getArgs() { Map<String, String> map = new
	 * HashMap<String, String>(); for (Argument argument : args) {
	 * map.put(argument.getName(), argument.getArgument()); } return map; }
	 * 
	 * public void setArgs(List<Argument> args) { this.args = args; }
	 */
	
	public XMLTable getTable() {
		return xmlTable;
	}
	
	public String getXMLPipAsString() throws JAXBException {
		return JAXBUtility.marshalToString(XMLPip.class, this, "pip", null);
	}
	
	public String getConnection() {
		return connection;
	}
	
	public String getMultiattribute() {
		return multiattribute;
	}
	
	public void setMultiattribute(String multiattribute) {
		this.multiattribute = multiattribute;
	}
	
}

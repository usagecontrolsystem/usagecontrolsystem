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
import javax.xml.bind.annotation.XmlRootElement;

import it.cnr.iit.utility.JAXBUtility;

/**
 * These are the classes required to perform marshalling/unmarshalling of the
 * xml file describing the properties of the UCS implementation.
 * 
 * <p>
 * We want our framework to be highly modular and configurable. The latter
 * characteristic has to be provided by the XML file through which we configure
 * the framework.
 * 
 * <br>
 * 
 * <b>NOTE</b>: It can be considered the opportunity of plugging only the
 * modules provided in the configuration, hence let this part of code to
 * dynamically modify the pom.xml file or to do something similar with equinox
 * or felix.
 * </p>
 * 
 * @author antonio
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ucs")
public final class Configuration {
	
	@XmlElement(name = "general")
	private XMLGeneral			generalInfo;
	
	@XmlElement(name = "distributed")
	private XMLDistributed	xmlDistributed;
	
	@XmlElement(name = "scheduler")
	private String					scheduler;
	
	@XmlElementWrapper(name = "pips")
	@XmlElement(name = "pip")
	private List<XMLPip>		pipList	= new ArrayList<>();
	
	public List<XMLPip> getPipList() {
		return pipList;
	}
	
	public void setPipList(List<XMLPip> pipList) {
		this.pipList = pipList;
	}
	
	@XmlElement(name = "sessionmanager")
	private XMLSessionManager			sessionManager;
	
	@XmlElement(name = "proxies")
	private XMLProxies						proxies;
	
	@XmlElement(name = "contexthandler")
	private XMLContextHandler			ch;
	
	@XmlElement(name = "pdp")
	private XMLPdp								pdp;
	
	@XmlElement(name = "pap")
	private XMLPap								pap;
	
	@XmlElement(name = "requestmanager")
	private XMLRequestManager			rm;
	
	@XmlElement(name = "obligationmanager")
	private XMLObligationManager	om;
	
	@XmlElementWrapper(name = "peps")
	@XmlElement(name = "pep")
	private List<XMLPep>					pep	= new ArrayList<>();
	
	public List<XMLPep> getPep() {
		return pep;
	}
	
	public String getConfigurationAsString() throws Exception {
		return JAXBUtility.marshalToString(Configuration.class, this,
		    "configuration", null);
	}
	
	public XMLSessionManager getSessionManager() {
		return sessionManager;
	}
	
	public XMLProxies getProxies() {
		return proxies;
	}
	
	public XMLContextHandler getCh() {
		return ch;
	}
	
	public XMLPdp getPdp() {
		return pdp;
	}
	
	public XMLPap getPap() {
		return pap;
	}
	
	public XMLRequestManager getRm() {
		return rm;
	}
	
	public XMLObligationManager getOm() {
		return om;
	}
	
	public XMLGeneral getXmlGeneral() {
		return generalInfo;
	}
	
	public XMLDistributed getDistributed() {
		return xmlDistributed;
	}
	
	public boolean getScheduler() {
		if (scheduler != null && scheduler.equalsIgnoreCase("TRUE")) {
			return true;
		} else
			return false;
	}
	
}

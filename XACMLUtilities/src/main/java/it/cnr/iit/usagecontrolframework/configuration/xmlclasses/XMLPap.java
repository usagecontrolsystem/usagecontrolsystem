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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * PAP xml
 * 
 * @author antonio
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLPap {
	@XmlElement(name = "class")
	private String	className;
	
	@XmlElement(name = "thread")
	private String	thread;
	
	@XmlElement(name = "communication")
	private String	communication;
	
	@XmlElement(name = "path")
	private String	path;
	
	public String getClassName() {
		return className;
	}
	
	public String getThread() {
		return thread;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getCommunication() {
		return communication;
	}
}

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
 * Table field xml
 * 
 * @author antonio
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLField {
	
	@XmlElement(name = "name")
	private String	name;
	
	@XmlElement(name = "type")
	private String	type;
	
	@XmlElement(name = "key")
	private String	key;
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public String getKey() {
		return key;
	}
	
}

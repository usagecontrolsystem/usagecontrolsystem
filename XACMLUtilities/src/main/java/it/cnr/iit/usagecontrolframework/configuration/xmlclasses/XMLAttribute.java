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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class XMLAttribute {
	
	@XmlElement(name = "arg")
	private List<Argument> arguments = new ArrayList<Argument>();
	
	public Map<String, String> getArgs() {
		Map<String, String> map = new HashMap<String, String>();
		for (Argument argument : arguments) {
			map.put(argument.getName(), argument.getArgument());
		}
		return map;
	}
	
	public void setArgs(List<Argument> args) {
		this.arguments = args;
	}
	
	/**
	 * private class argument
	 * 
	 * @author antonio
	 *
	 */
	private static class Argument {
		
		@XmlAttribute(name = "name")
		private String	name;
		
		@XmlAttribute(name = "argument")
		private String	argument;
		
		public String getName() {
			return name;
		}
		
		public String getArgument() {
			return argument;
		}
		
	}
	
}

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
package it.cnr.iit.usagecontrolframework.zzz.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import javax.xml.bind.JAXBException;

import iit.cnr.it.ucsinterface.pap.PAPInterface;
import iit.cnr.it.ucsinterface.pdp.PDPInterface;
import iit.cnr.it.ucsinterface.pip.PIPCHInterface;
import iit.cnr.it.ucsinterface.pip.exception.PIPException;
import iit.cnr.it.ucsinterface.sessionmanager.SessionManagerInterface;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.Configuration;
import it.cnr.iit.usagecontrolframework.contexthandler.ContextHandlerLC;
import it.cnr.iit.usagecontrolframework.contexthandler.exceptions.MalformedObjectException;
import it.cnr.iit.usagecontrolframework.entry.UsageControlFramework;
import it.cnr.iit.usagecontrolframework.proxies.PIPBuilder;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPAP;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPDP;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPEP;
import it.cnr.iit.usagecontrolframework.proxies.ProxySessionManager;
import it.cnr.iit.xacmlutilities.policy.utility.JAXBUtility;
import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

public class Main {
	
	UsageControlFramework usageControlFramework;
	
	public static void testRetrieve(String[] args)
	    throws IOException, JAXBException, PIPException {
		InputStream resources = Main.class.getClassLoader()
		    .getResourceAsStream("conf.xml");
		if (resources == null) {
			System.out.println("IS NULL");
		}
		BufferedReader bufferedReader = new BufferedReader(
		    new InputStreamReader(resources));
		StringBuilder tot = new StringBuilder();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			tot.append(line);
		}
		bufferedReader.close();
		resources.close();
		
		Configuration configuration = JAXBUtility
		    .unmarshalToObject(Configuration.class, tot.toString());
		
		ContextHandlerLC ch = new ContextHandlerLC(configuration.getCh());
		try {
			ch.isOk();
		} catch (MalformedObjectException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		PIPCHInterface pip = PIPBuilder.build(configuration.getPipList().get(0));
		PDPInterface pdp = new ProxyPDP(configuration.getPdp());
		SessionManagerInterface sm = new ProxySessionManager(
		    configuration.getSessionManager());
		PAPInterface pap = new ProxyPAP(configuration.getPap());
		
		String request = "";
		try {
			Scanner scanner = new Scanner(
			    new File("/home/antonio/projects/Policies/RequestNoTabs.xml"));
			while (scanner.hasNext()) {
				request += scanner.nextLine();
			}
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		pip.retrieve(JAXBUtility.unmarshalToObject(RequestType.class, request));
		
	}
	
	public void testConstructor() {
		usageControlFramework = new UsageControlFramework();
	}
	
	public static void main(String[] args) {
		Main main = new Main();
		main.testConstructor();
		ProxyPEP pepProxy = (ProxyPEP) main.usageControlFramework.getPEPProxy()
		    .get("");
		// pepProxy.start();
	}
}

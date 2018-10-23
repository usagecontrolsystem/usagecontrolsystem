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
package it.cnr.iit.usagecontrolframework.communication;

import java.io.IOException;

import org.springframework.boot.SpringApplication;

import it.cnr.iit.usagecontrolframework.communication.rest.RESTApplicationDeployer;
import it.cnr.iit.usagecontrolframework.zzz.test.Test;

public class Main {
	
	enum MODE {
		REST, TEST
	}
	
	public static void main(String args[])
	    throws InterruptedException, IOException {
		MODE mode = MODE.REST;
		if (mode == MODE.REST) {
			SpringApplication.run(RESTApplicationDeployer.class, args);
		} else {
			Test.main(args);
		}
	}
}

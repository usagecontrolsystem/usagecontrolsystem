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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import it.cnr.iit.usagecontrolframework.entry.UsageControlFramework;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPEP;

public class Test {
	UsageControlFramework			usageControlFramework	= new UsageControlFramework();
	ProxyPEP									pepProxy							= (ProxyPEP) usageControlFramework
	    .getPEPProxy().get("");
	
	private static final int	ROUNDS								= 10;
	
	static {
		System.setProperty("org.apache.commons.logging.Log",
		    "org.apache.commons.logging.impl.NoOpLog");
		// System.setProperty("org.wso2.balana.logger.type", "LOCAL");
		// System.setProperty("org.wso2.balana.logger.type", "ERROR");
		System.setProperty("com.j256.ormlite.logger.type", "LOCAL");
		System.setProperty("com.j256.ormlite.logger.level", "ERROR");
	}
	
	/**
	 * Class in charge of modifying the files the pip will read
	 * 
	 * @author antonio
	 *
	 */
	class TemperatureModifier implements Runnable {
		
		@Override
		public void run() {
			try {
				int randomNum = ThreadLocalRandom.current().nextInt(10, 20);
				Thread.sleep(randomNum * 1000);
				FileWriter fileWriter = new FileWriter(
				    new File("/home/antonio/projects/pips/temperature.txt"));
				fileWriter.write("40.0");
				fileWriter.flush();
				fileWriter.close();
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	class RoleModifier implements Runnable {
		
		@Override
		public void run() {
			try {
				int randomNum = ThreadLocalRandom.current().nextInt(2, 5);
				while (true) {
					Thread.sleep(randomNum * 1000);
					FileWriter fileWriter = new FileWriter(
					    new File("/home/antonio/projects/pips/subject.txt"));
					fileWriter.write("Antonio\tCNR\nGiacomo\tCNR");
					fileWriter.flush();
					fileWriter.close();
					Thread.sleep(randomNum * 1000);
					fileWriter = new FileWriter(
					    new File("/home/antonio/projects/pips/subject.txt"));
					fileWriter.write("Antonio\tIIT\nGiacomo\tIIT");
					fileWriter.flush();
					fileWriter.close();
				}
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	class PEPrequest implements Runnable {
		
		@Override
		public void run() {
			int randomNum = ThreadLocalRandom.current().nextInt(5, 10);
			for (int i = 0; i < randomNum; i++) {
				try {
					pepProxy.start();
					
					// Thread.sleep(100);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args)
	    throws InterruptedException, IOException {
		
		// System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
		Test test = new Test();
		PEPrequest p = test.new PEPrequest();
		Thread thread = new Thread(p);
		Thread thread1 = new Thread(test.new RoleModifier());
		thread.start();
		// System.in.read();
		thread1.start();
		
		/*
		 * Thread.sleep(60000); FileWriter fileWriter = new FileWriter( new
		 * File("/home/antonio/projects/pips/temperature.txt"));
		 * fileWriter.write("25.0"); fileWriter.flush(); fileWriter.close();
		 * System.out.println("RESET"); new Thread(p).start(); new Thread(test.new
		 * TemperatureModifier()).start();
		 */
	}
	
}

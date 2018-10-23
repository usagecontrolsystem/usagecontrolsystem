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
package iit.cnr.it.ucsinterface.contexthandler.scheduler;

import java.util.HashMap;

/**
 * This is the interface provided by the scheduler.
 * <p>
 * The scheduler is basically in charge of providing a decision to the
 * ContextHandler on which is the node in charge of managing a certain kind of
 * request. The scheduler will perform this decision basing on the performance
 * of the actual node and on the ones of the surroundings node. Moreover the
 * scheduler will use the length of the backlog queue in order to take a
 * decision.
 * </p>
 * 
 * @author antonio
 *
 */
public interface SchedulerInterface {
	
	/**
	 * This is the only function provided by the scheduler to the context handler.
	 * The context handler has to give to the scheduler the list of IPs of
	 * required attributes. The scheduler will take its decision basing on the
	 * implemented algorithm.
	 * 
	 * @param attributesIP
	 *          map in which the key is the IP of the node and the value is the
	 *          number of attributes stored in that node, hence it is the number
	 *          of attributes to be retrieved from that node
	 * @return a string representing the IP of the node in charge of handling the
	 *         request
	 */
	public String getIp(HashMap<String, Integer> attributesIP);
}

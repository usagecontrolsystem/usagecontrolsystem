/*******************************************************************************
 * Copyright 2018 IIT-CNR
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package it.cnr.iit.ucsinterface.performancemonitor;

/**
 * This is the interface offered by the PerformanceMonitor.
 * <p>
 * The PerformanceMonitor has to allow the scheduler to communicate with it in
 * order to know which node suits best the request to be analyzed. Hence the
 * PerformanceMonitor has to inform the scheduler about the ComputationalPower
 * and the Length of the backlogqueue of each node.
 * 
 * @author testucs
 *
 */
public interface PerformanceMonitorInterface {
    /**
     * Retrieves the dimension of the backlog queue
     * 
     * @param ip
     *          the ip of the node in which the scheduler is interested into
     * @return the performance metrics related to that ip
     */
    public int getBackLogQueueLength( String ip );

    /**
     * Retrieves the performance of the node having IP ip
     * 
     * @param ip
     *          the ip of the node in which the scheduler is interested into
     * @return the performance metrics related to that ip
     */
    public int getPerformance( String ip );
}

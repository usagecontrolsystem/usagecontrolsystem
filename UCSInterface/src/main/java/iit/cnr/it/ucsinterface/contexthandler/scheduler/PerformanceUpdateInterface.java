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
package iit.cnr.it.ucsinterface.contexthandler.scheduler;

/**
 * This is the other side of the {@link SchedulerPerformanceInterface}
 * <p>
 * The PerformanceInterface offers the CHScheduler methods to retrieve values
 * stored somewhere. This interface instead offers the methods to insert those
 * values in the place where it is possible to retrieve them
 * <p>
 *
 * @author antonio
 *
 */
public interface PerformanceUpdateInterface {
    /**
     * This is the function used to update the latency. To update the latency we
     * need the sourcenodeid, the destinationnodeid and the latency between these
     * nodes.
     *
     * @param source
     *          the source node that is the one that will call the scheduler
     * @param destination
     *          the destination node which will be charged for the evaluation
     * @param latency
     *          the latency between these nodes
     *
     */
    public void updateLatency( String source, String destination,
            PERFORMANCE_METRICS latency );

    /**
     * This is the function used to update the length of the backlog queue.
     * <h4>NOTE</h4> The timestamp, additional parameter required in this case
     * will be added by the object implementing this interface
     *
     * @param nodeId
     *          the id of the node whcih wants to update the length of its queue
     * @param backLogLength
     *          the actual length of the queue
     */
    public void updateBackLogQueue( String nodeId,
            PERFORMANCE_METRICS backLogLength );

    /**
     * This is the function used to insert the performance of the node.
     *
     * @param nodeId
     *          the id of the node which is storing its performance
     * @param performance
     *          the performance of the node
     */
    public void insertPerformance( String nodeId, PERFORMANCE_METRICS performance );
}

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
package it.cnr.iit.ucsinterface.barrier;

/**
 * This is the interface offered by the properties class to the BarrierMonitor.
 *
 * <p>
 * This is a single function interface, it is used by the monitor to get how
 * many threads a particular class can spawn. Depending on this number the
 * Monitor will change its behavior.
 * </p>
 *
 * @author antonio
 *
 */
public interface PropertiesBarrierInterface {
    /**
     * Retrieves the number of threads a particular class can spawn. The returned
     * number can be either 0 if the class in question cannot spawn a new thread,
     * this can be done especially on constrained devices to limit resource usage
     * or greater than 0. In the first case the monitor will call the proper
     * function without putting the message in the barrier, in the other the
     * monitor will simply put the message in the barrier.
     * <p>
     * This behavior allows to have asymmetric behavior on both ends of the
     * barrier, this means that, for example, on one end of the barrier we can
     * have many threads sending messages, while on the other end we have a single
     * process.
     * </p>
     *
     * @param string
     *          the name of the class
     * @return the number of thread a class can spawn
     */
    public int getThread( String string );
}

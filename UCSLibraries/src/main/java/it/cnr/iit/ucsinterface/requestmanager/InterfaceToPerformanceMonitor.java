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
package it.cnr.iit.ucsinterface.requestmanager;

/**
 * This is the interface offered by the RequestManager to the
 * PerformanceMonitor.
 * <p>
 * The RequestManager has to enable the PerformanceMonitor to know the size of
 * the backlog queue, i.e., the number of requests the request manager has still
 * to pass to the ContextHandler.
 * </p>
 * 
 * @author testucs
 *
 */
public interface InterfaceToPerformanceMonitor {
    /**
     * Retrieves the dimension of the backlog queue
     * 
     * @return an integer that is the length of the backlog queue
     */
    public int getBackLogQueueLength();
}

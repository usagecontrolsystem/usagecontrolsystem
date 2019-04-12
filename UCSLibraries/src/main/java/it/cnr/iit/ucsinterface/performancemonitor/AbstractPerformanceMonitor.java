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

import java.util.logging.Logger;

import it.cnr.iit.ucsinterface.requestmanager.InterfaceToPerformanceMonitor;

/**
 * This is an abstract class representing the Performance Monitor.
 * <p>
 * A performance monitor is basically a storage of characterisitcs of a node and
 * might be implemented in many different ways. We leave this choice to the
 * effective class, here we simply put all the components a PerformanceMonitor
 * needs to work properly. This is done in order to oblige the proigrammer to
 * follow a pre-defined path. <br>
 * A component every PerformanceMonitor must have is a RequestMnaanger. In order
 * not to misuse the RequestManager, only the Interface offered by the
 * RequestManager to the PerformanceMonitor is put.
 * </p>
 *
 * @author testucs
 *
 */
public abstract class AbstractPerformanceMonitor implements PerformanceMonitorInterface {
    protected static final Logger log = Logger
        .getLogger( AbstractPerformanceMonitor.class.getName() );

    private InterfaceToPerformanceMonitor requestManager;

    /**
     * Retrieves the request manager
     *
     * @return the request manager
     */
    final protected InterfaceToPerformanceMonitor getRequestManager() {
        return requestManager;
    }

    final protected void setRequestManager(
            InterfaceToPerformanceMonitor requestManager ) {
        this.requestManager = requestManager;
    }

}

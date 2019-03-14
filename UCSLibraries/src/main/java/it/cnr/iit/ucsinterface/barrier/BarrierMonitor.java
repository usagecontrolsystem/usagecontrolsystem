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

import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.ucs.AbstractProperties;

/**
 * This is the barrier monitor.
 *
 * <p>
 * This is basically a wrapper around a barrier, determining its behavior basing
 * on the configuration. Basically what happens is that the monitor knows how
 * many threads each of the component that interfaces with the barrier can
 * spawn. Hence if it knows that a component cannot spawn a thread, it works as
 * a function calling, thus when it receives a message from one part it simple
 * forwards it to the other. Instead if it's possible to have multithreading,
 * then the barreir is used and the message will be forwarded only when the
 * thread asks a new message.
 * </p>
 *
 * @author antonio
 *
 */
public abstract class BarrierMonitor<E extends Message> {
    protected Barrier<E> barrier;
    protected AbstractProperties properties;

    /**
     * Retrieves the message that is on the top of the queue. This is a blocking
     * operations. Messages are ordered by priority.
     *
     * @return the Message object that is on the top of the queue.
     */
    abstract public Message getMessage();

    /**
     * Adds a message in the queue. Since the queue is ordered by priority, the
     * message will be put in its proper position.
     *
     * @param message
     *          the message object we want to put inside the queue
     * @return true if everything goes ok, false otherwise
     */
    abstract public boolean putMessage( E message );
}

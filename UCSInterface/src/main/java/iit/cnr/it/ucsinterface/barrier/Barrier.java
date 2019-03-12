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
package iit.cnr.it.ucsinterface.barrier;

import java.util.concurrent.PriorityBlockingQueue;

import iit.cnr.it.ucsinterface.message.Message;

/**
 * This class represents an abstract barrier. In general a barrier is formed by
 * a queue of objects.
 * <p>
 * Instead of reinventing the wheel, it's better to take one of the class
 * already defined in the java library to handle concurrency. In this case we
 * have chosen the PriorityBlockingQueue. Basically by providing a Message class
 * that implements the Comparable interface then it is possible to have and
 * ordered queue where in the head of the queue we always have the message with
 * the highest priority. For further description go to <a href=
 * "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/PriorityBlockingQueue.html">java</a>
 * </p>
 * <p>
 * The barrier is both an abstract class and also a template one. The E extends
 * the abstract class Message.
 * </p>
 *
 * @author Antonio La Marra
 * @see{java.util.concurrent.PriorityBlockingQueue}
 */
public abstract class Barrier<E extends Message> {
    // this is the queue in which we will put the messages ordered by priority
    protected final PriorityBlockingQueue<E> queue;

    /**
     * Constructor for the Barrier class, this will enable all subclasses to call
     * the super constructor
     *
     * @param queue
     *          the queue we want to use inside this barrier
     */
    protected Barrier( PriorityBlockingQueue<E> queue ) {
        this.queue = queue;
    }
}

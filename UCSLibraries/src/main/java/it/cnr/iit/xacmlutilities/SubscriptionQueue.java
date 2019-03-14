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
package it.cnr.iit.xacmlutilities;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This is the class implementing the subscription queue.
 * 
 * <p>
 * The subscription queue is the queue in which the various PIPs put the
 * informations about which are the attributes to be monitored and eventually
 * the additional informations about those attributes.
 * 
 * <br>
 * In the former this queue was implemented using an home-made solution, in this
 * case instead we will use the LinkedBlockingQueue provided by JAVA.
 * </p>
 * 
 * @author antonio
 *
 */
public class SubscriptionQueue<T> {
    // the queue in which the various PIPs will put monitored attributes
    LinkedBlockingQueue<T> linkedBlockingQueue = new LinkedBlockingQueue<>();

    /**
     * Gets the head of the queue; waits until the queue is not empty
     * 
     * @return the head of the queue
     * 
     */
    public T pop() {
        try {
            return linkedBlockingQueue.take();
        } catch( InterruptedException ie ) {
            ie.printStackTrace();
            return null;
        }
    }

    /**
     * Pushes an object in the queue and notifies any blocked thread
     * 
     * @param elem
     *          object to add in the queue
     */
    public void push( T elem ) {
        try {
            linkedBlockingQueue.put( elem );
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the queue is empty
     * 
     * @return true if the queue is empty; false otherwise
     */
    public boolean empty() {
        return linkedBlockingQueue.isEmpty();
    }

    /**
     * Prints the queue; Used for debugging purposes
     */
    public synchronized void print() {
        Iterator<T> it = linkedBlockingQueue.iterator();
        int i = 0;
        while( it.hasNext() ) {
            System.out.println( "Queue[" + i + "] = " + it.next().toString() );
            i++;
        }
    }

}

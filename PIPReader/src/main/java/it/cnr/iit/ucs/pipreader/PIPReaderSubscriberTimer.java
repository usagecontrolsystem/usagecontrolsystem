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
package it.cnr.iit.ucs.pipreader;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Subscriber timer for the PIPReader.
 * <p>
 * Basically the subscriber timer is in charge of performing the task of
 * refreshing periodically the value of a certain attribute, if that value
 * changes, then it has to update the value in the subscriptions queue.
 *
 * <p>
 * By removing the public attribute to this class we have allowed only classes
 * in the same package to create or instantiate such a class
 * </p>
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
final class PIPReaderSubscriberTimer extends TimerTask {
    private Timer timer;
    PIPReader pip;

    PIPReaderSubscriberTimer( PIPReader pip ) {
        this.timer = new Timer();
        this.pip = pip;
    }

    @Override
    public void run() {
        pip.checkSubscriptions();
    }

    public void start() {
        timer.scheduleAtFixedRate( this, 0, 10L * 1000 );
    }
}

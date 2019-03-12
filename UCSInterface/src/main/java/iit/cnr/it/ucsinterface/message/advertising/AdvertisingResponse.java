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
package iit.cnr.it.ucsinterface.message.advertising;

import java.io.Serializable;

import iit.cnr.it.ucsinterface.message.Message;

/**
 * This is the response to be sent by the recipient of the advertising message.
 * <p>
 * Upon receiving this message a new row has to be added inside the cassandra
 * table if it doesn't exist
 * </p>
 *
 * @author antonio
 * @deprecated
 */
@Deprecated
class AdvertisingResponse extends Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The constructor of the class demanded to contain the response to be sent
     *
     * @param advMessage
     *          the message used for advertise the neighbours
     * @param host
     *          the ip of the node that is answering to the message
     */
    public AdvertisingResponse( String host, AdvertisingMessage advMessage ) {
        super( host, advMessage.getSource(), advMessage.getID() );
    }

}

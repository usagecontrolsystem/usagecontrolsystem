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
import iit.cnr.it.ucsinterface.message.PART;

/**
 * This is the message to be sent for advertising the host. Its structure is
 * very simple and straightforward
 *
 * @author antonio
 * @deprecated
 */
@Deprecated
class AdvertisingMessage extends Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor for the Advertising message
     *
     * @param host
     *          the ip of the host sending the advertising message
     */
    public AdvertisingMessage( String host ) {
        super( host, PART.ADVERTISING.toString() );
    }

}

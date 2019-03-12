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
package iit.cnr.it.ucsinterface.message.tryaccess;

import java.util.logging.Logger;

import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.PART;
import iit.cnr.it.ucsinterface.message.PURPOSE;

/**
 * This is the whole object that arrives as a message to the tryaccess.
 *
 * <p>
 * We assume that the tryaccess message wraps in a single point many different
 * aspects:
 * <ol>
 * <li>The uri of the pep in string format</li>
 * <li>The uxacml policy in the xml format converted as a string</li>
 * <li>The uxacml request in the xml format converted as a string</li>
 * </ol>
 * All this parts are wrapped inside the motivation that is the access point for
 * all theses parts.
 * </p>
 *
 * @author antonio
 *
 */
final public class TryAccessMessage extends Message {
    private static final Logger LOGGER = Logger.getLogger( TryAccessMessage.class.getName() );

    private static final long serialVersionUID = 1L;

    // content of the tryaccess
    private TryAccessMessageContent content = new TryAccessMessageContent();
    // flag to state if the message has been correctly initialized
    private volatile boolean isInitialized = false;

    /**
     * Constructor for a TryAccessMessage
     *
     * @param source
     *          the source of the message
     * @param destination
     *          the destination of the message
     */
    public TryAccessMessage( String source, String destination ) {
        super( source, destination );
        if( isInitialized() ) {
            isInitialized = true;
            purpose = PURPOSE.TRYACCESS;
            motivation = null;
        } else {
            LOGGER.severe( "ERROR IN MESSAGE creation" );
        }
    }

    /**
     * Constructor for a TryAccessMessage
     *
     */
    public TryAccessMessage() {
        super( PART.PEP.toString(), PART.CH.toString() );
        if( isInitialized() ) {
            isInitialized = true;
            purpose = PURPOSE.TRYACCESS;
            motivation = null;
        } else {
            LOGGER.severe( "ERROR IN MESSAGE creation" );
        }
    }

    @Override
    public int compareTo( Message o ) {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getPepUri() {
        if( isInitialized ) {
            return content.getPepUri();
        }
        return null;
    }

    public String getPolicy() {
        if( isInitialized ) {
            return content.getPolicy();
        }
        LOGGER.severe( "MESSAGE NOT INITIALIZED" );
        return null;
    }

    public String getPolicyId() {
        if( isInitialized ) {
            return content.getPolicyId();
        }
        return null;
    }

    public String getRequest() {
        if( isInitialized ) {
            return content.getRequest();
        }
        return null;
    }

    public TryAccessMessageContent getContent() {
        return content;
    }

}

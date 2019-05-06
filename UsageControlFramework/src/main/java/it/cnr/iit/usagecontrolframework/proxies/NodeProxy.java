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
package it.cnr.iit.usagecontrolframework.proxies;

import java.net.URI;
import java.util.Optional;
import java.util.logging.Logger;

import it.cnr.iit.ucs.constants.CONNECTION;
import it.cnr.iit.ucs.properties.components.GeneralProperties;
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationMessage;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessResponse;
import it.cnr.iit.ucsinterface.node.NodeInterface;
import it.cnr.iit.utility.JsonUtility;
import it.cnr.iit.utility.RESTUtils;
import it.cnr.iit.utility.Utility;
import it.cnr.iit.utility.errorhandling.Reject;

/**
 * This is the class effectively implementing the proxy to communicate with
 * every other node.
 *
 * <p>
 * For the moment the only communicating way provided is the one by means of
 * REST API, hence, whenever a message hass to be sent outside, we will use REST
 * to perform this operation. <br>
 * Due to the simplicity of this operation, nothing would prevent us from using
 * </p>
 *
 * @author antonio
 *
 */
public class NodeProxy implements NodeInterface {

    private static final Logger log = Logger.getLogger( NodeProxy.class.getName() );

    private CONNECTION connection = CONNECTION.REST_API;
    private URI uri;

    public NodeProxy( GeneralProperties properties ) {
        Reject.ifNull( properties );

        Optional<URI> uri = Utility.parseUri( properties.getBaseUri() );
        Reject.ifAbsent( uri );
        this.uri = uri.get(); // NOSONAR
    }

    @Override
    public void sendMessage( Message message ) {

        switch( connection ) {
            case REST_API:
                sendMessageViaREST( message );
                break;
            default:
                break;
        }

    }

    // TODO make this generic in ucsLibraries
    private Optional<String> getApi( Message message ) {
        if( message instanceof TryAccessMessage ) {
            return Optional.of( NodeInterface.TRYACCESS_REST );
        } else if( message instanceof StartAccessMessage ) {
            return Optional.of( NodeInterface.STARTACCESS_REST );
        } else if( message instanceof EndAccessMessage ) {
            return Optional.of( NodeInterface.ENDACCESS_REST );
        } else if( message instanceof TryAccessResponse ) {
            return Optional.of( NodeInterface.TRYACCESSRESPONSE_REST );
        } else if( message instanceof StartAccessResponse ) {
            return Optional.of( NodeInterface.STARTACCESSRESPONSE_REST );
        } else if( message instanceof EndAccessResponse ) {
            return Optional.of( NodeInterface.ENDACCESSRESPONSE_REST );
        } else if( message instanceof ReevaluationMessage ) {
            return Optional.of( NodeInterface.ONGOING_REST );
        } else if( message instanceof ReevaluationResponse ) {
            return Optional.of( NodeInterface.ONGOINGRESPONSE_REST );
        }

        return Optional.empty();
    }

    private void sendMessageViaREST( Message message ) {
        Optional<String> api = getApi( message );

        if( !api.isPresent() ) {
            log.severe( "Error posting message : could not determine api" );
            return;
        }

        Optional<String> data = JsonUtility.getJsonStringFromObject( message, false );

        if( !data.isPresent() ) {
            log.severe( "Error posting message : could not marshal message" );
            return;
        }

        try {
            RESTUtils.asyncPost(
                uri.toString(),
                api.get(),
                data.get() );
        } catch( Exception e ) {
            log.severe( "Error posting message : " + api.get() );
        }

    }

}

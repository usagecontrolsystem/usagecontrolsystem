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
package it.cnr.iit.ucsrest.proxies;

import java.net.URI;
import java.util.Optional;
import java.util.logging.Logger;

import it.cnr.iit.ucs.constants.OperationName;
import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.ucs.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucs.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucs.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucs.message.tryaccess.TryAccessResponse;
import it.cnr.iit.ucs.pep.PEPInterface;
import it.cnr.iit.ucs.properties.components.PepProperties;
import it.cnr.iit.utility.RESTUtils;
import it.cnr.iit.utility.errorhandling.Reject;

/**
 * This is a proxy towards a PEP that uses REST.
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public class PEPRestProxy implements PEPInterface {

    private static final Logger log = Logger.getLogger( PEPRestProxy.class.getName() );

    private PepProperties properties;
    private URI uri;

    public PEPRestProxy( PepProperties properties ) {
        Reject.ifNull( properties );
        this.properties = properties;
        Optional<URI> opturi = RESTUtils.parseUri( properties.getUri() );
        Reject.ifAbsent( opturi, "error parsing uri" );
        this.uri = opturi.get(); // NOSONAR
    }

    @Override
    // TODO return actual response.
    public Message onGoingEvaluation( ReevaluationResponse message ) {
        RESTUtils.asyncPost( uri.toString(), OperationName.ONGOINGRESPONSE_REST, message );
        return null;
    }

    @Override
    // TODO return actual response.
    public String receiveResponse( Message message ) {
        Optional<String> api = getApiForMessage( message );
        try {
            RESTUtils.asyncPost( uri.toString(), api.get(), message ); // NOSONAR
        } catch( Exception e ) {
            log.severe( "Error posting message : " + api );
            return "KO";
        }
        return "OK";
    }

    private Optional<String> getApiForMessage( Message message ) {
        if( message instanceof TryAccessResponse ) {
            return Optional.of( properties.getApiTryAccessResponse() );
        } else if( message instanceof StartAccessResponse ) {
            return Optional.of( properties.getApiStartAccessResponse() );
        } else if( message instanceof EndAccessResponse ) {
            return Optional.of( properties.getApiEndAccessResponse() );
        }
        return Optional.empty();
    }

}

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
package it.cnr.iit.peprest.proxy;

import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import it.cnr.iit.peprest.configuration.UCSProxyProperties;
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.PURPOSE;
import it.cnr.iit.utility.RESTUtils;

@Component
public class UCSProxy {

    private static final Logger log = Logger.getLogger( UCSProxy.class.getName() );

    @Autowired
    private UCSProxyProperties ucs;

    public boolean sendMessage( Message message ) {
        try {
            Optional<ResponseEntity<Void>> response = RESTUtils.post(
                ucs.getBaseUri(),
                getApiNameFromPurpose( message.getPurpose() ),
                message );
            return response.isPresent() && response.get().getStatusCode().is2xxSuccessful();
        } catch( Exception e ) {
            log.severe( "Error posting message : " + e.getMessage() );
        }
        return false;
    }

    private String getApiNameFromPurpose( PURPOSE purpose ) {
        switch( purpose ) {
            case TRYACCESS:
                return ucs.getApiTryAccess();
            case STARTACCESS:
                return ucs.getApiStartAccess();
            case ENDACCESS:
                return ucs.getApiEndAccess();
            default:
                return "";
        }
    }
}

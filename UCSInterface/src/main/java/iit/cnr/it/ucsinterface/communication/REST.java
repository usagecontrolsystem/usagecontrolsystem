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
package iit.cnr.it.ucsinterface.communication;

import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * This class is in chrge of managing REST communication.
 * <p>
 * This class proviudes an helper to send request using REST API.
 * </p>
 *
 * @author antonio
 *
 */
final public class REST {
    private static final Logger LOGGER = Logger.getLogger( REST.class.getName() );

    /* avoids class instantiation */
    private REST() {}

    /**
     * Sends a request to a remote party
     *
     * @param uri
     *          the uri of the remote party
     * @param body
     *          the body of the request
     * @param clazz
     *          the class of the response we're expecting
     * @return the response of the other party
     */
    public static <T> T sendRequest( String uri, Object body, Class<T> clazz ) {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<T> response = restTemplate.postForEntity( uri, body, clazz );

        if( response.getStatusCode() != HttpStatus.OK ) {
            // fail("error, HTTP status message is != 200");
            LOGGER.severe( "[REST] Some errors occurred, event dispatching terminates wrongly, HTTP status message is : "
                    + response.getStatusCode() );
            return null;
        } else {
            // assertTrue(successResp.getSuccess().equals(true));
            LOGGER.info( "[REST] Event dispatching terminates correctly "
                    + response.getBody() );
            return response.getBody();
        }
    }

}

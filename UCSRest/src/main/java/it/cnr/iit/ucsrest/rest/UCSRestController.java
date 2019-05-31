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
package it.cnr.iit.ucsrest.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.cnr.iit.ucs.constants.OperationName;
import it.cnr.iit.ucs.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucs.ucs.UCSInterface;
import it.cnr.iit.utility.errorhandling.Reject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * This class includes all the interfaces we will offer via rest.
 * Maybe it is better to offer an interface for each possible request that can
 * be sent to the UCS. In this way we will have many entry points. However all
 * these will be mapped on the same RequestManager function so that it becomes a
 * lot easier to perform task as prioritising between the queues and so on.
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
@ApiModel( value = "UCSFramework", description = "Usage Control Framework enforcement engine REST API" )
@RestController
@RequestMapping( "/" )
public class UCSRestController {
    private static final Logger log = Logger.getLogger( UCSRestController.class.getName() );

    @Autowired
    private UCSInterface ucs;

    @ApiOperation( httpMethod = "POST", value = "Receives request from PEP for tryaccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = OperationName.TRYACCESS_REST, consumes = MediaType.APPLICATION_JSON_VALUE )
    // TODO UCS-34 NOSONAR
    public void sendMessage( @RequestBody( ) TryAccessMessage message ) {
        Reject.ifNull( message );
        ucs.tryAccess( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from PEP for startaccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = OperationName.STARTACCESS_REST, consumes = MediaType.APPLICATION_JSON_VALUE )
    // TODO UCS-34 NOSONAR
    public void sendMessage( @RequestBody( ) StartAccessMessage message ) {
        Reject.ifNull( message );
        log.log( Level.INFO, "Startaccess received {0}", System.currentTimeMillis() );
        ucs.startAccess( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from PEP for endaccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = OperationName.ENDACCESS_REST, consumes = MediaType.APPLICATION_JSON_VALUE )
    // TODO UCS-34 NOSONAR
    public void sendMessage( @RequestBody( ) EndAccessMessage message ) {
        Reject.ifNull( message );
        log.log( Level.INFO, "Endaccess received {0}", System.currentTimeMillis() );
        ucs.endAccess( message );
    }

}

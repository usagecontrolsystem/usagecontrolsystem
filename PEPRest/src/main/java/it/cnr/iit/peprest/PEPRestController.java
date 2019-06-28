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
package it.cnr.iit.peprest;

import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import it.cnr.iit.peprest.messagetrack.CallerResponse;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@ApiModel( value = "UCS", description = "Usage Control System enforcement engine REST API" )
@RestController
@RequestMapping( "/" )
@EnableAutoConfiguration
public class PEPRestController {

    protected static final Logger log = Logger.getLogger( PEPRestController.class.getName() );

    private PEPRest pepRest;

    @ApiOperation( httpMethod = "POST", value = "Starts the PEP" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = "/start" )
    // TODO UCS-34 NOSONAR
    public String startEvaluation() {
        return pepRest.tryAccess();
    }

    @ApiOperation( httpMethod = "GET", value = "Retrieves the status of a message" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @GetMapping( value = "/status" )
    // TODO UCS-34 NOSONAR
    public CallerResponse getMessageStatus( @RequestParam( value = "messageId" ) String messageId ) {
        if( messageId == null || messageId.isEmpty() ) {
            throw new HttpClientErrorException( HttpStatus.NO_CONTENT );
        }
        Optional<CallerResponse> callerResponse = pepRest.getMessageStorage().getMessageStatus( messageId );
        if( callerResponse.isPresent() ) {
            return callerResponse.get();
        } else {
            throw new IllegalArgumentException( "Invalid message id passed: " + messageId );
        }
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from PEP for endaccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = "/finish", consumes = MediaType.TEXT_PLAIN_VALUE )
    // TODO UCS-34 NOSONAR
    public void finish( @RequestBody( ) String sessionId ) {
        if( sessionId == null || sessionId.isEmpty() ) {
            throw new HttpClientErrorException( HttpStatus.NO_CONTENT );
        }
        pepRest.endAccess( sessionId );
    }

    @Autowired
    public void setPepRest( PEPRest pepRest ) {
        this.pepRest = pepRest;
    }
}

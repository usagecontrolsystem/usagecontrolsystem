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

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.cnr.iit.peprest.messagetrack.CallerResponse;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@ApiModel( value = "UCSFramework", description = "Usage Control Framework enforcement engine REST API" )
@RestController
@RequestMapping( "/" )
@EnableAutoConfiguration
public class PEPRestCommunication {
    boolean initialized = false;

    private PEPRest pepRest;

    @RequestMapping( method = RequestMethod.GET, value = "/isAlive", consumes = MediaType.ALL_VALUE )
    public void isAlive() {
        System.out.println( "in isAlive():heath check OK" );
    }

    @ApiOperation( httpMethod = "POST", value = "Starts the PEP" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @RequestMapping( method = RequestMethod.POST, value = "/sendSynchronous" )
    public void sendMessage() throws InterruptedException, ExecutionException {
        pepRest.run();
    }

    @ApiOperation( httpMethod = "POST", value = "Starts the PEP" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @RequestMapping( method = RequestMethod.POST, value = "/startEvaluation" )
    public String startEvaluation() throws InterruptedException, ExecutionException {
        return pepRest.tryAccess();
    }

    /**
     * Retrieves the status of a message. Possible status of a message are highlighted in
     *
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @ApiOperation( httpMethod = "GET", value = "Starts the PEP" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @RequestMapping( method = RequestMethod.GET, value = "/flowStatus" )
    public CallerResponse getMessageStatus( @RequestAttribute( value = "messageId" ) String messageId )
            throws InterruptedException, ExecutionException {
        return pepRest.getMessageHistory().getMessageStatus( messageId ).get();
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from PEP for endaccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @RequestMapping( method = RequestMethod.POST, value = "/finish", consumes = MediaType.TEXT_PLAIN_VALUE )
    public void finish( @RequestBody( ) String sessionId )
            throws InterruptedException, ExecutionException {
        // BEGIN parameter checking
        if( sessionId == null ) {
            System.out.println( "SESSION is null" );
            throw new HttpMessageNotReadableException( HttpStatus.NO_CONTENT + " : No session id" );
        }
        // END parameter checking
        pepRest.end( sessionId );
    }

    @Autowired
    public void setPepRest( PEPRest pepRest ) {
        this.pepRest = pepRest;
    }
}

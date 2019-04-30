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
package it.cnr.iit.usagecontrolframework.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.cnr.iit.ucsinterface.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationMessage;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucsinterface.message.remoteretrieval.MessagePipCh;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessResponse;
import it.cnr.iit.ucsinterface.node.NodeInterface;
import it.cnr.iit.ucsinterface.ucs.UCSInterface;
import it.cnr.iit.utility.LogProfiler;
import it.cnr.iit.utility.errorhandling.Reject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * This class includes all the interfaces we will offer via rest.
 * <p>
 * Maybe it is better to offer an interface for each possible request that can
 * be sent to the UCS. In this way we will have many entry points. However all
 * these will be mapped on the same RequestManager function so that it becomes a
 * lot easier to perform task as prioritising between the queues and so on.
 * </p>
 *
 * @author antonio
 *
 */
@ApiModel( value = "UCSFramework", description = "Usage Control Framework enforcement engine REST API" )
@RestController
@RequestMapping( "/" )
public class UCFRestController {

    private static final Logger log = Logger.getLogger( UCFRestController.class.getName() );

    @Autowired
    private UCSInterface ucs;

    @ApiOperation( httpMethod = "POST", value = "Receives request from PEP for tryaccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = NodeInterface.TRYACCESS_REST, consumes = MediaType.APPLICATION_JSON_VALUE )
    // TODO UCS-34 NOSONAR
    public void sendMessage( @RequestBody( ) TryAccessMessage message ) {
        Reject.ifNull( message );
        LogProfiler.getInstance().log( "REST CALLED" );
        ucs.tryAccess( message );

    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from PEP for startaccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = NodeInterface.STARTACCESS_REST, consumes = MediaType.APPLICATION_JSON_VALUE )
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
    @PostMapping( value = NodeInterface.ENDACCESS_REST, consumes = MediaType.APPLICATION_JSON_VALUE )
    // TODO UCS-34 NOSONAR
    public void sendMessage( @RequestBody( ) EndAccessMessage message ) {
        Reject.ifNull( message );
        log.log( Level.INFO, "Endaccess received {0}", System.currentTimeMillis() );
        ucs.endAccess( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from PEP for endaccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = NodeInterface.ONGOING_REST, consumes = MediaType.TEXT_PLAIN_VALUE )
    // TODO UCS-34 NOSONAR
    public void sendMessage( @RequestBody( ) ReevaluationMessage message ) {
        Reject.ifNull( message );
        log.log( Level.INFO, "Reevaluation received at {0}", System.currentTimeMillis() );
        ucs.onGoingEvaluation( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from PIP for attribute retrieval operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = "/retrieveRemote", consumes = MediaType.TEXT_PLAIN_VALUE )
    // TODO UCS-34 NOSONAR
    public void retrieveRemote( @RequestBody( ) MessagePipCh message ) {
        Reject.ifNull( message );
        ucs.retrieveRemote( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives response from PIP for attribute retrieval operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = "/retrieveRemoteResponse", consumes = MediaType.TEXT_PLAIN_VALUE )
    // TODO UCS-34 NOSONAR
    public void retrieveRemoteResponse( @RequestBody( ) MessagePipCh message ) {
        Reject.ifNull( message );
        ucs.retrieveRemoteResponse( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from PEP for tryaccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = NodeInterface.TRYACCESSRESPONSE_REST, consumes = MediaType.TEXT_PLAIN_VALUE )
    // TODO UCS-34 NOSONAR
    public void tryAccessResponse( @RequestBody( ) TryAccessResponse message ) {
        Reject.ifNull( message );
        log.log( Level.INFO, "Trytaccess received at {0}", System.currentTimeMillis() );
        ucs.tryAccessResponse( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from PEP for tryaccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = NodeInterface.STARTACCESSRESPONSE_REST, consumes = MediaType.TEXT_PLAIN_VALUE )
    // TODO UCS-34 NOSONAR
    public void startAccessResponse( @RequestBody( ) StartAccessResponse message ) {
        Reject.ifNull( message );
        log.log( Level.INFO, "Startaccess received at {0}", System.currentTimeMillis() );
        ucs.startAccessResponse( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from PEP for tryaccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = NodeInterface.ENDACCESSRESPONSE_REST, consumes = MediaType.TEXT_PLAIN_VALUE )
    // TODO UCS-34 NOSONAR
    public void endAccessResponse( @RequestBody( ) EndAccessResponse message ) {
        Reject.ifNull( message );
        log.log( Level.INFO, "Endaccess received at {0}", System.currentTimeMillis() );
        ucs.endAccessResponse( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from PEP for endaccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = NodeInterface.ONGOINGRESPONSE_REST, consumes = MediaType.TEXT_PLAIN_VALUE )
    // TODO UCS-34 NOSONAR
    public void reevaluationResponse( @RequestBody( ) ReevaluationResponse message ) {
        Reject.ifNull( message );
        log.log( Level.INFO, "On going Evaluation received at {0}", System.currentTimeMillis() );
        ucs.onGoingEvaluationResponse( message );
    }

    @Bean
    public UCSInterface getUCSInterface() {
        return new UsageControlFramework();
    }
}

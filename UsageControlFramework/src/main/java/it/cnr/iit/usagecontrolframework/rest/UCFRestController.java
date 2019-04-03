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

import java.util.logging.Logger;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.cnr.iit.ucs.exceptions.NotFoundException;
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
import it.cnr.iit.usagecontrolframework.entry.UsageControlFramework;
import it.cnr.iit.utility.LogProfiler;

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
 * lot easier to perform task as prioritizing between the queues and so on.
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

    private UsageControlFramework usageControlFramework = new UsageControlFramework();

    @ApiOperation( httpMethod = "POST", value = "Receives request from PEP for tryaccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @RequestMapping( method = RequestMethod.POST, value = NodeInterface.TRYACCESS_REST, consumes = MediaType.APPLICATION_JSON_VALUE )
    public void sendMessage( @RequestBody( ) TryAccessMessage message ) {
        // BEGIN parameter checking
        if( message == null ) {
            throw new NotFoundException();
        }
        // END parameter checking
        LogProfiler.getInstance().log( "REST CALLED" );

        usageControlFramework.tryAccess( message );

    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from PEP for startaccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @RequestMapping( method = RequestMethod.POST, value = NodeInterface.STARTACCESS_REST, consumes = MediaType.APPLICATION_JSON_VALUE )
    public void sendMessage( @RequestBody( ) StartAccessMessage message ) {
        // BEGIN parameter checking
        if( message == null ) {
            throw new NotFoundException();
        }
        // END parameter checking
        log.info( "[TIME] Startaccess received " + System.currentTimeMillis() );
        usageControlFramework.startAccess( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from PEP for endaccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @RequestMapping( method = RequestMethod.POST, value = NodeInterface.ENDACCESS_REST, consumes = MediaType.APPLICATION_JSON_VALUE )
    public void sendMessage( @RequestBody( ) EndAccessMessage message ) {
        // BEGIN parameter checking
        if( message == null ) {
            throw new NotFoundException();
        }
        // END parameter checking
        log.info( "[TIME] Endaccess received " + System.currentTimeMillis() );
        usageControlFramework.endAccess( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from PEP for endaccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @RequestMapping( method = RequestMethod.POST, value = NodeInterface.ONGOING_REST, consumes = MediaType.TEXT_PLAIN_VALUE )
    public void sendMessage( @RequestBody( ) ReevaluationMessage message ) {
        // BEGIN parameter checking
        if( message == null ) {
            throw new NotFoundException();
        }
        // END parameter checking

        log.info( "[TIME] Reevaluation received " + System.currentTimeMillis() );
        usageControlFramework.onGoingEvaluation( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from PIP for attribute retrieval operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @RequestMapping( method = RequestMethod.POST, value = "/retrieveRemote", consumes = MediaType.TEXT_PLAIN_VALUE )
    public void retrieveRemote( @RequestBody( ) MessagePipCh message ) {
        // BEGIN parameter checking
        if( message == null ) {
            throw new NotFoundException();
        }
        // END parameter checking
        usageControlFramework.retrieveRemote( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives response from PIP for attribute retrieval operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @RequestMapping( method = RequestMethod.POST, value = "/retrieveRemoteResponse", consumes = MediaType.TEXT_PLAIN_VALUE )
    public void retrieveRemoteResponse( @RequestBody( ) MessagePipCh message ) {
        // BEGIN parameter checking
        if( message == null ) {
            throw new NotFoundException();
        }
        // END parameter checking

        usageControlFramework.retrieveRemoteResponse( message );
        // usageControlFramework.getRequestManager().sendMessageToCH(messagePipCh);
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from PEP for tryaccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @RequestMapping( method = RequestMethod.POST, value = NodeInterface.TRYACCESSRESPONSE_REST, consumes = MediaType.TEXT_PLAIN_VALUE )
    public void tryAccessResponse( @RequestBody( ) TryAccessResponse message ) {
        // BEGIN parameter checking
        if( message == null ) {
            throw new NotFoundException();
        }
        // END parameter checking
        usageControlFramework.tryAccessResponse( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from PEP for tryaccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @RequestMapping( method = RequestMethod.POST, value = NodeInterface.STARTACCESSRESPONSE_REST, consumes = MediaType.TEXT_PLAIN_VALUE )
    public void startAccessResponse( @RequestBody( ) StartAccessResponse message ) {
        // BEGIN parameter checking
        if( message == null ) {
            throw new NotFoundException();
        }
        // END parameter checking
        log.info( "[TIME] Startaccess received " + System.currentTimeMillis() );
        usageControlFramework.startAccessResponse( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from PEP for tryaccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @RequestMapping( method = RequestMethod.POST, value = NodeInterface.ENDACCESSRESPONSE_REST, consumes = MediaType.TEXT_PLAIN_VALUE )
    public void endAccessResponse( @RequestBody( ) EndAccessResponse message ) {
        // BEGIN parameter checking
        if( message == null ) {
            throw new NotFoundException();
        }
        // END parameter checking
        log.info( "[TIME] Endaccess received " + System.currentTimeMillis() );
        usageControlFramework.endAccessResponse( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from PEP for endaccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @RequestMapping( method = RequestMethod.POST, value = NodeInterface.ONGOINGRESPONSE_REST, consumes = MediaType.TEXT_PLAIN_VALUE )
    public void reevaluationResponse( @RequestBody( ) ReevaluationResponse message ) {
        // BEGIN parameter checking
        if( message == null ) {
            throw new NotFoundException();
        }
        // END parameter checking

        log.info( "[TIME] On going Evaluation received " + System.currentTimeMillis() );
        usageControlFramework.onGoingEvaluationResponse( message );
    }
}

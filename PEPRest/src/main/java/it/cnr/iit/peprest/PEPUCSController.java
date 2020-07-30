package it.cnr.iit.peprest;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.cnr.iit.ucs.constants.OperationName;
import it.cnr.iit.ucs.message.endaccess.EndAccessResponseMessage;
import it.cnr.iit.ucs.message.reevaluation.ReevaluationResponseMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessResponseMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessResponseMessage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@ApiModel( value = "UCS", description = "Usage Control System enforcement engine REST API" )
@RestController
@RequestMapping( "/" )
@EnableAutoConfiguration
public class PEPUCSController {
    protected static final Logger log = Logger.getLogger( PEPUCSController.class.getName() );

    private PEPRest pepRest;

    @ApiOperation( httpMethod = "POST", value = "Receives request from CH for onGoingEvaluation operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = OperationName.ONGOINGRESPONSE_REST, consumes = MediaType.APPLICATION_JSON_VALUE )
    // TODO UCS-34 NOSONAR
    public void onGoingEvaluation( @RequestBody( ) ReevaluationResponseMessage message ) {
        pepRest.onGoingEvaluation( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from CH for tryAccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = "/tryAccessResponse", consumes = MediaType.APPLICATION_JSON_VALUE )
    // TODO UCS-34 NOSONAR
    public void tryAccessResponse( @RequestBody( ) TryAccessResponseMessage message ) {
        pepRest.sendResponse( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from CH for startAccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = "/startAccessResponse", consumes = MediaType.APPLICATION_JSON_VALUE )
    // TODO UCS-34 NOSONAR
    public void startAccessResponse( @RequestBody( ) StartAccessResponseMessage message ) {
        pepRest.sendResponse( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from CH for tryAccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = "/endAccessResponse", consumes = MediaType.APPLICATION_JSON_VALUE )
    // TODO UCS-34 NOSONAR
    public void endAccessResponse( @RequestBody( ) EndAccessResponseMessage message ) {
        pepRest.sendResponse( message );
    }

    @Autowired
    public void setPepRest( PEPRest pepRest ) {
        this.pepRest = pepRest;
    }

}

package it.cnr.iit.peprest;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.cnr.iit.ucsinterface.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessResponse;
import it.cnr.iit.ucsinterface.node.NodeInterface;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@ApiModel( value = "UCSFramework", description = "Usage Control Framework enforcement engine REST API" )
@RestController
@RequestMapping( "/" )
@EnableAutoConfiguration
public class PEPUCSCommunication {
    protected static final Logger LOG = Logger.getLogger( PEPUCSCommunication.class.getName() );

    private PEPRest pepRest;

    @ApiOperation( httpMethod = "POST", value = "Receives request from CH for onGoingEvaluation operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = NodeInterface.ONGOINGRESPONSE_REST, consumes = MediaType.APPLICATION_JSON_VALUE )
    // TODO UCS-34 NOSONAR
    public void onGoingEvaluation( @RequestBody( ) ReevaluationResponse message ) {
        pepRest.onGoingEvaluation( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from CH for tryAccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = "/tryAccessResponse", consumes = MediaType.APPLICATION_JSON_VALUE )
    // TODO UCS-34 NOSONAR
    public void tryAccessResponse( @RequestBody( ) TryAccessResponse message ) {
        pepRest.receiveResponse( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from CH for startAccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = "/startAccessResponse", consumes = MediaType.APPLICATION_JSON_VALUE )
    // TODO UCS-34 NOSONAR
    public void startAccessResponse( @RequestBody( ) StartAccessResponse message ) {
        pepRest.receiveResponse( message );
    }

    @ApiOperation( httpMethod = "POST", value = "Receives request from CH for tryAccess operation" )
    @ApiResponses( value = {
        @ApiResponse( code = 500, message = "Invalid message received" ),
        @ApiResponse( code = 200, message = "OK" ) } )
    @PostMapping( value = "/endAccessResponse", consumes = MediaType.APPLICATION_JSON_VALUE )
    // TODO UCS-34 NOSONAR
    public void endAccessResponse( @RequestBody( ) EndAccessResponse message ) {
        pepRest.receiveResponse( message );
    }

    @Autowired
    public void setPepRest( PEPRest pepRest ) {
        this.pepRest = pepRest;
    }

}

package iit.cnr.it.peprest;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import iit.cnr.it.ucsinterface.message.endaccess.EndAccessResponse;
import iit.cnr.it.ucsinterface.message.reevaluation.ReevaluationResponse;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessResponse;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessResponse;
import iit.cnr.it.ucsinterface.node.NodeInterface;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@ApiModel(value = "UCSFramework", description = "Usage Control Framework enforcement engine REST API")
@RestController
@RequestMapping("/")
@EnableAutoConfiguration
public class PEPUCSCommunication {
	  protected static final Logger LOGGER = Logger.getLogger(PEPUCSCommunication.class.getName());

	  boolean initialized = false;
	 
	  private PEPRest pepRest;

	  @ApiOperation(httpMethod = "POST", value = "Receives request from CH for onGoingEvaluation operation")
	  @ApiResponses(value = {
	      @ApiResponse(code = 500, message = "Invalid message received"),
	      @ApiResponse(code = 200, message = "OK") })
	  @RequestMapping(method = RequestMethod.POST, value = NodeInterface.ONGOINGRESPONSE_REST, consumes = MediaType.APPLICATION_JSON_VALUE)
	  public void onGoingEvaluation(@RequestBody() ReevaluationResponse message) {
	      pepRest.onGoingEvaluation(message);  
	  }

	  @ApiOperation(httpMethod = "POST", value = "Receives request from CH for tryAccess operation")
	  @ApiResponses(value = {
	      @ApiResponse(code = 500, message = "Invalid message received"),
	      @ApiResponse(code = 200, message = "OK") })
	  @RequestMapping(method = RequestMethod.POST, value = "/tryAccessResponse", consumes = MediaType.APPLICATION_JSON_VALUE)
	  public void tryAccessResponse(@RequestBody() TryAccessResponse message) {
		  pepRest.receiveResponse(message);
	  }

	  @ApiOperation(httpMethod = "POST", value = "Receives request from CH for startAccess operation")
	  @ApiResponses(value = {
	      @ApiResponse(code = 500, message = "Invalid message received"),
	      @ApiResponse(code = 200, message = "OK") })
	  @RequestMapping(method = RequestMethod.POST, value = "/startAccessResponse", consumes = MediaType.APPLICATION_JSON_VALUE)
	  public void startAccessResponse(@RequestBody() StartAccessResponse message) {
		  pepRest.receiveResponse(message);
	  }

	  @ApiOperation(httpMethod = "POST", value = "Receives request from CH for tryAccess operation")
	  @ApiResponses(value = {
	      @ApiResponse(code = 500, message = "Invalid message received"),
	      @ApiResponse(code = 200, message = "OK") })
	  @RequestMapping(method = RequestMethod.POST, value = "/endAccessResponse", consumes = MediaType.APPLICATION_JSON_VALUE)
	  public void endAccessResponse(@RequestBody() EndAccessResponse message) {
		  pepRest.receiveResponse(message);
	  }

	@Autowired
	public void setPepRest(PEPRest pepRest) {
		this.pepRest = pepRest;
	}

}

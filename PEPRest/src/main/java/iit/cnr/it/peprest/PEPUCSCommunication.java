package iit.cnr.it.peprest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

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
	boolean initialized = false;
	  
	  @Autowired
	  private PEPRest pepRest;
	  
	  @ApiOperation(httpMethod = "POST", value = "Receives request from PEP for tryaccess operation")
	  // provides a documentation of the different http error messages and their
	  // meaning from the application perspective
	  @ApiResponses(value = {
	      @ApiResponse(code = 500, message = "Invalid message received"),
	      @ApiResponse(code = 200, message = "OK") })
	  @RequestMapping(method = RequestMethod.POST, value = NodeInterface.ONGOINGRESPONSE_REST, consumes = MediaType.TEXT_PLAIN_VALUE)
	  public void onGoingEvaluation(@RequestBody() String message) {
	    // BEGIN parameter checking
	    if (message == null) {
	      return;
	    }
	    // END parameter checking
	    ReevaluationResponse reevaluation = new Gson().fromJson(message,
	        ReevaluationResponse.class);
	    pepRest.onGoingEvaluation(reevaluation);
	  }

	  @ApiOperation(httpMethod = "POST", value = "Receives request from PEP for tryaccess operation")
	  // provides a documentation of the different http error messages and their
	  // meaning from the application perspective
	  @ApiResponses(value = {
	      @ApiResponse(code = 500, message = "Invalid message received"),
	      @ApiResponse(code = 200, message = "OK") })
	  @RequestMapping(method = RequestMethod.POST, value = "/tryAccessResponse", consumes = MediaType.TEXT_PLAIN_VALUE)
	  public void tryAccessResponse(@RequestBody() String message) {
	    // BEGIN parameter checking
	    if (message == null) {
	    }
	    // END parameter checking
	    pepRest
	        .receiveResponse(new Gson().fromJson(message, TryAccessResponse.class));
	  }

	  @ApiOperation(httpMethod = "POST", value = "Receives request from PEP for tryaccess operation")
	  // provides a documentation of the different http error messages and their
	  // meaning from the application perspective
	  @ApiResponses(value = {
	      @ApiResponse(code = 500, message = "Invalid message received"),
	      @ApiResponse(code = 200, message = "OK") })
	  @RequestMapping(method = RequestMethod.POST, value = "/startAccessResponse", consumes = MediaType.TEXT_PLAIN_VALUE)
	  public void startAccessResponse(@RequestBody() String message) {
	    // BEGIN parameter checking
	    if (message == null) {
	    }
	    // END parameter checking
	    pepRest.receiveResponse(
	        new Gson().fromJson(message, StartAccessResponse.class));
	  }

	  @ApiOperation(httpMethod = "POST", value = "Receives request from PEP for tryaccess operation")
	  // provides a documentation of the different http error messages and their
	  // meaning from the application perspective
	  @ApiResponses(value = {
	      @ApiResponse(code = 500, message = "Invalid message received"),
	      @ApiResponse(code = 200, message = "OK") })
	  @RequestMapping(method = RequestMethod.POST, value = "/endAccessResponse", consumes = MediaType.TEXT_PLAIN_VALUE)
	  public void endAccessResponse(@RequestBody() String message) {
	    // BEGIN parameter checking
	    if (message == null) {
	    }
	    // END parameter checking
	    pepRest
	        .receiveResponse(new Gson().fromJson(message, EndAccessResponse.class));
	  }

}

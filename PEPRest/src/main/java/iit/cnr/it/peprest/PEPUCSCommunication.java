package iit.cnr.it.peprest;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import iit.cnr.it.ucsinterface.message.endaccess.EndAccessResponse;
import iit.cnr.it.ucsinterface.message.reevaluation.ReevaluationResponse;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessResponse;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessResponse;
import iit.cnr.it.ucsinterface.node.NodeInterface;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

//TODO we should be using a *Message as a Request body parameters in apis instead of strings
@ApiModel(value = "UCSFramework", description = "Usage Control Framework enforcement engine REST API")
@RestController
@RequestMapping("/")
@EnableAutoConfiguration
public class PEPUCSCommunication {
	  protected static final Logger LOGGER = Logger.getLogger(PEPUCSCommunication.class.getName());

	  boolean initialized = false;

	  @Autowired
	  private PEPRest pepRest;

	  @ApiOperation(httpMethod = "POST", value = "Receives request from CH for onGoingEvaluation operation")
	  @ApiResponses(value = {
	      @ApiResponse(code = 500, message = "Invalid message received"),
	      @ApiResponse(code = 200, message = "OK") })
	  @RequestMapping(method = RequestMethod.POST, value = NodeInterface.ONGOINGRESPONSE_REST, consumes = MediaType.TEXT_PLAIN_VALUE)
	  public void onGoingEvaluation(@RequestBody() String message) {
	    // BEGIN parameter checking
		Optional<?> optMessage = messageFromJson(message, ReevaluationResponse.class);
		if (!optMessage.isPresent()) {
	    	LOGGER.warning("error deserializing in onGoingEvaluation");
	    	throw new HttpMessageNotReadableException(HttpStatus.SC_NO_CONTENT+" : Invalid message Content");
		}
	    // END parameter checking
	    pepRest.onGoingEvaluation((ReevaluationResponse) optMessage.get());
	  }

	  @ApiOperation(httpMethod = "POST", value = "Receives request from CH for tryAccess operation")
	  @ApiResponses(value = {
	      @ApiResponse(code = 500, message = "Invalid message received"),
	      @ApiResponse(code = 200, message = "OK") })
	  @RequestMapping(method = RequestMethod.POST, value = "/tryAccessResponse2", consumes = MediaType.APPLICATION_JSON_VALUE)
	  public void tryAccessResponse2(@RequestBody() TryAccessResponse message) {
		// BEGIN parameter checking
		if (message == null) {
	    	LOGGER.warning("error deserializing in tryAccessResponse");
	    	throw new HttpMessageNotReadableException(HttpStatus.SC_NO_CONTENT+" : Invalid message Content");
		}
	    // END parameter checking
		pepRest.receiveResponse(message);
	  }
	  
	  
	  @ApiOperation(httpMethod = "POST", value = "Receives request from CH for tryAccess operation")
	  @ApiResponses(value = {
	      @ApiResponse(code = 500, message = "Invalid message received"),
	      @ApiResponse(code = 200, message = "OK") })
	  @RequestMapping(method = RequestMethod.POST, value = "/tryAccessResponse", consumes = MediaType.APPLICATION_JSON_VALUE)
	  public void tryAccessResponse(@RequestBody() String message) {
		// BEGIN parameter checking
		Optional<?> optMessage = messageFromJson(message, TryAccessResponse.class);
	    if (!optMessage.isPresent()) {
	    	LOGGER.warning("error deserializing in tryAccessResponse");
	    	throw new HttpMessageNotReadableException(HttpStatus.SC_NO_CONTENT+" : Invalid message Content");
		}
	    // END parameter checking
		pepRest.receiveResponse((TryAccessResponse) optMessage.get());
	  }

	  @ApiOperation(httpMethod = "POST", value = "Receives request from CH for startAccess operation")
	  @ApiResponses(value = {
	      @ApiResponse(code = 500, message = "Invalid message received"),
	      @ApiResponse(code = 200, message = "OK") })
	  @RequestMapping(method = RequestMethod.POST, value = "/startAccessResponse", consumes = MediaType.TEXT_PLAIN_VALUE)
	  public void startAccessResponse(@RequestBody() String message) {
		// BEGIN parameter checking
	    Optional<?> optMessage = messageFromJson(message, StartAccessResponse.class);
	    if (!optMessage.isPresent()) {
	    	LOGGER.warning("error deserializing in startAccessResponse");
	    	throw new HttpMessageNotReadableException(HttpStatus.SC_NO_CONTENT+" : Invalid message Content");
	    }
	    // END parameter checking

		pepRest.receiveResponse((StartAccessResponse) optMessage.get());
	  }

	  @ApiOperation(httpMethod = "POST", value = "Receives request from CH for tryAccess operation")
	  @ApiResponses(value = {
	      @ApiResponse(code = 500, message = "Invalid message received"),
	      @ApiResponse(code = 200, message = "OK") })
	  @RequestMapping(method = RequestMethod.POST, value = "/endAccessResponse", consumes = MediaType.TEXT_PLAIN_VALUE)
	  public void endAccessResponse(@RequestBody() String message) {
	    // BEGIN parameter checking
	    Optional<?> optMessage = messageFromJson(message, EndAccessResponse.class);
	    if (!optMessage.isPresent()) {
	    	LOGGER.warning("error deserializing in endAccessResponse");
	    	throw new HttpMessageNotReadableException(HttpStatus.SC_NO_CONTENT+" : Invalid message Content");
	    }
	    // END parameter checking
	    
	    pepRest.receiveResponse((EndAccessResponse) optMessage.get());
	  }

	  // TODO to be deleted if rest interface is changed to not use strings
	  public static <T> Optional<T> messageFromJson(String json, Class<T> classType) {
		ObjectMapper objMapper = new ObjectMapper();
		try {
			T obj = objMapper.readValue(json, classType);
			if (obj != null) 
				return Optional.of(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	  }
}

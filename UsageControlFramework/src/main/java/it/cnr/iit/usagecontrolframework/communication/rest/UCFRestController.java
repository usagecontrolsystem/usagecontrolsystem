/*******************************************************************************
 * Copyright 2018 IIT-CNR
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package it.cnr.iit.usagecontrolframework.communication.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import iit.cnr.it.ucsinterface.message.endaccess.EndAccessMessage;
import iit.cnr.it.ucsinterface.message.endaccess.EndAccessResponse;
import iit.cnr.it.ucsinterface.message.reevaluation.ReevaluationMessage;
import iit.cnr.it.ucsinterface.message.reevaluation.ReevaluationResponse;
import iit.cnr.it.ucsinterface.message.remoteretrieval.MessagePipCh;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessMessage;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessResponse;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessMessage;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessResponse;
import iit.cnr.it.ucsinterface.node.NodeInterface;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.cnr.iit.usagecontrolframework.entry.UsageControlFramework;

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
@ApiModel(value = "UCSFramework", description = "Usage Control Framework enforcement engine REST API")
@RestController
@RequestMapping("/")
public class UCFRestController {

  boolean initialized = false;
  private UsageControlFramework usageControlFramework = new UsageControlFramework();

  // ---------------------------------------------------------------------------
  // ORIGINAL OPERATIONS
  // ---------------------------------------------------------------------------
  @ApiOperation(httpMethod = "POST", value = "Receives request from PEP for tryaccess operation")
  // provides a documentation of the different http error messages and their
  // meaning from the application perspective
  @ApiResponses(value = {
      @ApiResponse(code = 500, message = "Invalid message received"),
      @ApiResponse(code = 200, message = "OK") })
  @RequestMapping(method = RequestMethod.POST, value = NodeInterface.TRYACCESS_REST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public void sendMessage(@RequestBody() TryAccessMessage message) {
    // BEGIN parameter checking
    if (message == null) {
      throw new NotFoundException();
    }
    // END parameter checking
    // System.out.println("message not null");
    System.out
        .println("[TIME] Tryaccess received " + System.currentTimeMillis());
    usageControlFramework.tryAccess(message);

  }

  @ApiOperation(httpMethod = "POST", value = "Receives request from PEP for startaccess operation")
  // provides a documentation of the different http error messages and their
  // meaning from the application perspective
  @ApiResponses(value = {
      @ApiResponse(code = 500, message = "Invalid message received"),
      @ApiResponse(code = 200, message = "OK") })
  @RequestMapping(method = RequestMethod.POST, value = NodeInterface.STARTACCESS_REST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public void sendMessage(@RequestBody() StartAccessMessage message) {
    // BEGIN parameter checking
    if (message == null) {
      throw new NotFoundException();
    }
    // END parameter checking
    // System.out.println("message not null");
    System.out
        .println("[TIME] Startaccess received " + System.currentTimeMillis());
    usageControlFramework.startAccess(message);
  }

  @ApiOperation(httpMethod = "POST", value = "Receives request from PEP for endaccess operation")
  // provides a documentation of the different http error messages and their
  // meaning from the application perspective
  @ApiResponses(value = {
      @ApiResponse(code = 500, message = "Invalid message received"),
      @ApiResponse(code = 200, message = "OK") })
  @RequestMapping(method = RequestMethod.POST, value = NodeInterface.ENDACCESS_REST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public void sendMessage(@RequestBody() EndAccessMessage message) {
    // BEGIN parameter checking
    if (message == null) {
      throw new NotFoundException();
    }
    // END parameter checking
    // System.out.println("message not null");
    System.out
        .println("[TIME] Endaccess received " + System.currentTimeMillis());
    usageControlFramework.endAccess(message);
  }

  @ApiOperation(httpMethod = "POST", value = "Receives request from PEP for endaccess operation")
  // provides a documentation of the different http error messages and their
  // meaning from the application perspective
  @ApiResponses(value = {
      @ApiResponse(code = 500, message = "Invalid message received"),
      @ApiResponse(code = 200, message = "OK") })
  @RequestMapping(method = RequestMethod.POST, value = NodeInterface.ONGOING_REST, consumes = MediaType.TEXT_PLAIN_VALUE)
  public void sendMessage(@RequestBody() String messageString) {
    // BEGIN parameter checking
    if (messageString == null) {
      throw new NotFoundException();
    }
    // END parameter checking
    // System.out.println("message not null");
    ReevaluationMessage message = new Gson().fromJson(messageString,
        ReevaluationMessage.class);
    System.out
        .println("[TIME] Reevaluation received " + System.currentTimeMillis());
    usageControlFramework.onGoingEvaluation(message);
  }

  // ---------------------------------------------------------------------------
  // REMOTE OPERATIONS
  // ---------------------------------------------------------------------------
  @ApiOperation(httpMethod = "POST", value = "Receives request from PIP for attribute retrieval operation")
  // provides a documentation of the different http error messages and their
  // meaning from the application perspective
  @ApiResponses(value = {
      @ApiResponse(code = 500, message = "Invalid message received"),
      @ApiResponse(code = 200, message = "OK") })
  @RequestMapping(method = RequestMethod.POST, value = "/retrieveRemote", consumes = MediaType.TEXT_PLAIN_VALUE)
  public void retrieveRemote(@RequestBody() String message) {
    // BEGIN parameter checking
    if (message == null) {
      throw new NotFoundException();
    }
    // END parameter checking

    // System.out.println("message not null");
    // System.out.println("Message arrived via rest: " + message);
    MessagePipCh messagePipCh = new Gson().fromJson(message,
        MessagePipCh.class);
    usageControlFramework.retrieveRemote(messagePipCh);
  }

  @ApiOperation(httpMethod = "POST", value = "Receives response from PIP for attribute retrieval operation")
  // provides a documentation of the different http error messages and their
  // meaning from the application perspective
  @ApiResponses(value = {
      @ApiResponse(code = 500, message = "Invalid message received"),
      @ApiResponse(code = 200, message = "OK") })
  @RequestMapping(method = RequestMethod.POST, value = "/retrieveRemoteResponse", consumes = MediaType.TEXT_PLAIN_VALUE)
  public void retrieveRemoteResponse(@RequestBody() String message) {
    // BEGIN parameter checking
    if (message == null) {
      throw new NotFoundException();
    }
    // END parameter checking

    // System.out.println("Message arrived via rest: " + message);
    MessagePipCh messagePipCh = new Gson().fromJson(message,
        MessagePipCh.class);
    usageControlFramework.retrieveRemoteResponse(messagePipCh);
    // usageControlFramework.getRequestManager().sendMessageToCH(messagePipCh);
  }

  // ---------------------------------------------------------------------------
  // REMOTE EVALUATION RESPONSES
  // ---------------------------------------------------------------------------
  // TODO
  @ApiOperation(httpMethod = "POST", value = "Receives request from PEP for tryaccess operation")
  // provides a documentation of the different http error messages and their
  // meaning from the application perspective
  @ApiResponses(value = {
      @ApiResponse(code = 500, message = "Invalid message received"),
      @ApiResponse(code = 200, message = "OK") })
  @RequestMapping(method = RequestMethod.POST, value = NodeInterface.TRYACCESSRESPONSE_REST, consumes = MediaType.TEXT_PLAIN_VALUE)
  public void tryAccessResponse(@RequestBody() String message) {
    // BEGIN parameter checking
    if (message == null) {
      throw new NotFoundException();
    }
    TryAccessResponse response = new Gson().fromJson(message,
        TryAccessResponse.class);
    // END parameter checking
    // System.out.println("message not null");
    usageControlFramework.tryAccessResponse(response);
  }

  @ApiOperation(httpMethod = "POST", value = "Receives request from PEP for tryaccess operation")
  // provides a documentation of the different http error messages and their
  // meaning from the application perspective
  @ApiResponses(value = {
      @ApiResponse(code = 500, message = "Invalid message received"),
      @ApiResponse(code = 200, message = "OK") })
  @RequestMapping(method = RequestMethod.POST, value = NodeInterface.STARTACCESSRESPONSE_REST, consumes = MediaType.TEXT_PLAIN_VALUE)
  public void startAccessResponse(@RequestBody() String message) {
    // BEGIN parameter checking
    if (message == null) {
      throw new NotFoundException();
    }
    // END parameter checking
    StartAccessResponse response = new Gson().fromJson(message,
        StartAccessResponse.class);
    // System.out.println("message not null");
    System.out
        .println("[TIME] Startaccess received " + System.currentTimeMillis());
    usageControlFramework.startAccessResponse(response);
  }

  @ApiOperation(httpMethod = "POST", value = "Receives request from PEP for tryaccess operation")
  // provides a documentation of the different http error messages and their
  // meaning from the application perspective
  @ApiResponses(value = {
      @ApiResponse(code = 500, message = "Invalid message received"),
      @ApiResponse(code = 200, message = "OK") })
  @RequestMapping(method = RequestMethod.POST, value = NodeInterface.ENDACCESSRESPONSE_REST, consumes = MediaType.TEXT_PLAIN_VALUE)
  public void endAccessResponse(@RequestBody() String message) {
    // BEGIN parameter checking
    if (message == null) {
      throw new NotFoundException();
    }
    // END parameter checking
    // System.out.println("message not null");
    EndAccessResponse response = new Gson().fromJson(message,
        EndAccessResponse.class);
    System.out
        .println("[TIME] Endaccess received " + System.currentTimeMillis());
    usageControlFramework.endAccessResponse(response);
  }

  @ApiOperation(httpMethod = "POST", value = "Receives request from PEP for endaccess operation")
  // provides a documentation of the different http error messages and their
  // meaning from the application perspective
  @ApiResponses(value = {
      @ApiResponse(code = 500, message = "Invalid message received"),
      @ApiResponse(code = 200, message = "OK") })
  @RequestMapping(method = RequestMethod.POST, value = NodeInterface.ONGOINGRESPONSE_REST, consumes = MediaType.TEXT_PLAIN_VALUE)
  public void reevaluationResponse(@RequestBody() String message) {
    // BEGIN parameter checking
    if (message == null) {
      throw new NotFoundException();
    }
    // END parameter checking

    // System.out.println("message not null");
    ReevaluationResponse response = new Gson().fromJson(message,
        ReevaluationResponse.class);
    System.out.println(
        "[TIME] On going Evaluation received " + System.currentTimeMillis());
    usageControlFramework.onGoingEvaluationResponse(response);
  }
}

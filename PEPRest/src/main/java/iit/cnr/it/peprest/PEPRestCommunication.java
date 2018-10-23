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
package iit.cnr.it.peprest;

import java.util.concurrent.ExecutionException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
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
public class PEPRestCommunication {
  boolean initialized = false;
  private PEPRest pepRest = new PEPRest();

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

  @ApiOperation(httpMethod = "POST", value = "Starts the PEP")
  // provides a documentation of the different http error messages and their
  // meaning from the application perspective
  @ApiResponses(value = {
      @ApiResponse(code = 500, message = "Invalid message received"),
      @ApiResponse(code = 200, message = "OK") })
  @RequestMapping(method = RequestMethod.POST, value = "/send")
  public void sendMessage() throws InterruptedException, ExecutionException {
    // BEGIN parameter checking
    // END parameter checking
    pepRest.run();
  }

  @ApiOperation(httpMethod = "POST", value = "Receives request from PEP for endaccess operation")
  // provides a documentation of the different http error messages and their
  // meaning from the application perspective
  @ApiResponses(value = {
      @ApiResponse(code = 500, message = "Invalid message received"),
      @ApiResponse(code = 200, message = "OK") })
  @RequestMapping(method = RequestMethod.POST, value = "/finish", consumes = MediaType.TEXT_PLAIN_VALUE)
  public void finish(@RequestBody() String sessionId)
      throws InterruptedException, ExecutionException {
    // BEGIN parameter checking
    if (sessionId == null) {
      System.out.println("SESSION is null");
    }
    // END parameter checking
    pepRest.end(sessionId);
  }

  @Bean
  public TaskExecutor taskExecutor() {
    return new SimpleAsyncTaskExecutor();
  }

  @Bean
  public CommandLineRunner schedulingRunner(TaskExecutor executor) {
    return new CommandLineRunner() {

      @Override
      public void run(String... arg0) throws Exception {
        executor.execute(pepRest);
      }
    };
  }
}

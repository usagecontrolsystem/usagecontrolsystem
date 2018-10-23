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
package iit.cnr.it.ucsinterface.node;

import iit.cnr.it.ucs.configuration.BasicConfiguration;
import iit.cnr.it.ucsinterface.constants.CONNECTION;
import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.endaccess.EndAccessMessage;
import iit.cnr.it.ucsinterface.message.endaccess.EndAccessResponse;
import iit.cnr.it.ucsinterface.message.reevaluation.ReevaluationMessage;
import iit.cnr.it.ucsinterface.message.reevaluation.ReevaluationResponse;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessMessage;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessResponse;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessMessage;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessResponse;
import iit.cnr.it.utility.RESTUtils;

/**
 * This is the class effectively implementing the proxy to communicate with
 * every other node.
 * 
 * <p>
 * For the moment the only communicating way provided is the one by means of
 * REST API, hence, whenever a message hass to be sent outside, we will use REST
 * to perform this operation. <br>
 * Due to the simplicity of this operation, nothing would prevent us from using
 * </p>
 * 
 * @author antonio
 *
 */
public class NodeProxy implements NodeInterface {

  private CONNECTION connection = CONNECTION.REST_API;

  @Override
  public void sendMessage(Message message) {

    switch (connection) {
    case REST_API:
      sendMessageViaREST(message);
      break;
    case API:
      useApi(message); // TODO
      break;
    case SOCKET:
      sendMessageViaSocket(message); // TODO
    }

  }

  private void sendMessageViaREST(Message message) {
    if (message instanceof TryAccessMessage) {
      RESTUtils.asyncPost(buildUrl(NodeInterface.TRYACCESS_REST, message),
          (TryAccessMessage) message);
    }
    if (message instanceof StartAccessMessage) {
      RESTUtils.asyncPost(buildUrl(NodeInterface.STARTACCESS_REST, message),
          (StartAccessMessage) message);
    }
    if (message instanceof EndAccessMessage) {
      RESTUtils.asyncPost(buildUrl(NodeInterface.ENDACCESS_REST, message),
          (EndAccessMessage) message);
    }
    if (message instanceof TryAccessResponse) {
      RESTUtils.asyncPostAsString(
          buildUrl(NodeInterface.TRYACCESSRESPONSE_REST, message),
          (TryAccessResponse) message);
    }
    if (message instanceof StartAccessResponse) {
      RESTUtils.asyncPostAsString(
          buildUrl(NodeInterface.STARTACCESSRESPONSE_REST, message),
          (StartAccessResponse) message);
    }
    if (message instanceof EndAccessResponse) {
      RESTUtils.asyncPostAsString(
          buildUrl(NodeInterface.ENDACCESSRESPONSE_REST, message),
          (EndAccessResponse) message);
    }
    if (message instanceof ReevaluationMessage) {
      RESTUtils.asyncPostAsString(buildUrl(NodeInterface.ONGOING_REST, message),
          (ReevaluationMessage) message);
    }
    if (message instanceof ReevaluationResponse) {
      RESTUtils.asyncPostAsString(
          buildUrl(NodeInterface.ONGOINGRESPONSE_REST, message),
          (ReevaluationResponse) message);
    }
  }

  private void sendMessageViaSocket(Message message) {
    // TODO Auto-generated method stub

  }

  private void useApi(Message message) {
    // TODO Auto-generated method stub

  }

  /**
   * Provided the name of the rest API to call, it builds up the complete url to
   * be used to call that interface
   * 
   * @param function
   *          the name of the function
   * @return the complete url to be used in the rest call
   */
  private String buildUrl(String function, Message message) {
    StringBuilder url = new StringBuilder();
    url.append("http://" + message.getDestination() + ":");
    url.append(BasicConfiguration.getBasicConfiguration().getPort());
    url.append(function);
    return url.toString();
  }

}

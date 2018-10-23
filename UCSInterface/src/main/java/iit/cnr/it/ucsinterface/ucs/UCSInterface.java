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
package iit.cnr.it.ucsinterface.ucs;

import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.endaccess.EndAccessMessage;
import iit.cnr.it.ucsinterface.message.endaccess.EndAccessResponse;
import iit.cnr.it.ucsinterface.message.reevaluation.ReevaluationMessage;
import iit.cnr.it.ucsinterface.message.reevaluation.ReevaluationResponse;
import iit.cnr.it.ucsinterface.message.remoteretrieval.MessagePipCh;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessMessage;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessResponse;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessMessage;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessResponse;

/**
 * This is the UCSInterface.
 * <p>
 * This interface is meant to be used by <b>external nodes or PEPs</b> wanting
 * to communicate with the UCS that offers it. <br>
 * As said also in the documentation, this interface will offer many methods in
 * order to follow a Do One Thing Well strategy. Functions offered by this
 * interface are:
 * <ul>
 * <li><i>tryAccess</i> in order to allow an external component to ask for a
 * tryAccess evaluation
 * <li>
 * <li><i>tryAccessResponse</i> in order to receive the response to the
 * tryAccess evaluation requested to an external node</li>
 * <li><i>startAccess</i> in order to allow an external component to ask for a
 * startAccess evaluation
 * <li>
 * <li><i>startAccessResponse</i> in order to receive the response to the
 * startAccess evaluation requested to an external node</li>
 * <li><i>endAccess</i> in order to allow an external component to ask for a
 * endAccess evaluation
 * <li>
 * <li><i>endAccessResponse</i> in order to receive the response to the
 * endAccess evaluation requested to an external node</li>
 * <li><i>onGoingEvaluation</i> in order to allow an external component to ask
 * for an onGoingEvaluation
 * <li>
 * <li><i>onGoingEvaluation</i> in order to receive the response to the
 * onGoingEvaluation requested to an external node</li>
 * <li><i>retrieveRemote</i> in order to retrieve an attribute from a remote
 * node
 * <li>
 * <li><i>retrieveRemoteResponse</i> in order to receive the value of the remote
 * attribute previously requested</li>
 * </ul>
 * 
 * </p>
 * 
 * 
 * @author antonio
 *
 */
public interface UCSInterface {

  /**
   * Request of a tryAccess evaluation.
   * <p>
   * The tryAccess is the first operation required by a PEP before starting the
   * effective action on a certain resource. It asks (in general) for evaluation
   * of immutable attributes, i.e., attributes that do not change their value very
   * often. In this implementation this function can be called either by a PEP
   * directly connected to this UCS, either by a Node that has scheduled the
   * execution of a tryAccess directed to it to this node.
   * </p>
   * 
   * @param tryAccessMessage
   *          the tryAccessMessage object, it contains all the informations
   *          required for the recipient to call back the caller and all the
   *          informations to correctly evaluate the request.
   */
  public void tryAccess(TryAccessMessage tryAccessMessage);

  /**
   * Response to a tryAccess evaluation.
   * <p>
   * This function can be called only in one case: the UCS to which this UCS has
   * asked the evaluation has finished and has provided a result, hence the
   * response has to be provided to the PEP
   * </p>
   * 
   * @param tryAccessResponse
   *          the response provided by the UCS to which we have asked the
   *          evaluation
   */
  public void tryAccessResponse(TryAccessResponse tryAccessResponse);

  /**
   * Request of a startAccess evaluation.
   * <p>
   * The startAccess is the second operation the PEP performs. It does it before
   * performing the action in order to notify the UCS that the action is started
   * and to query the UCS if it still has the permission to perform the action. In
   * general in this case also mutable attributes are taken into account. This
   * operation triggers also the monitoring of the attributes used to evaluate the
   * policy in order to react if something changes. This operation might also be
   * asked from the scheduler of another UCS, exactly as it happens in tryAccess.
   * </p>
   * 
   * @param startAccessMessage
   *          the startAccess message object. It contains all the informations
   *          required for the recipient to call back the caller, in general in
   *          this message the caller puts only the sessionID that has to be
   *          evaluated
   */
  public void startAccess(StartAccessMessage startAccessMessage);

  /**
   * Response to a startAccess evaluation
   * <p>
   * This function can be called only in one case, the UCS to which this UCS has
   * asked the evaluation has finished and has provided a result, hence the
   * response has to be provided to the PEP.
   * </p>
   * 
   * @param startAccessResponse
   *          the response provided by the UCS to which we have asked the
   *          evaluation
   */
  public void startAccessResponse(StartAccessResponse startAccessResponse);

  /**
   * Request of an endAccess evaluation.
   * <p>
   * The endAccess is the last operation the PEP performs. It does it in order to
   * notify the UCS that its session has finished. There are two reasons for
   * ending a session:
   * <ol>
   * <li>the PEP has performed the operation and does not need the resource
   * anymore</li>
   * <li>one of the mutable attributes present in the policy to be evaluated for
   * the startAccess has changed its value and the PEP does not have the rights to
   * use the resource anymore</li>
   * </ol>
   * In any case when the PEP sends an endAccess is to notify the end of a session
   * and this allows the UCS to free some resources. This operation might also be
   * assked from the scheduler of another UCS, exactly as it happens in tryAccess
   * and startAccess.
   * </p>
   * 
   * @param endAccessMessage
   *          the message containing the infomrations required to perform the
   *          endAccess
   */
  public void endAccess(EndAccessMessage endAccessMessage);

  /**
   * Response to an endAccess evaluation.
   * <p>
   * This function can be called only in one case, the UCS to which this UCS has
   * asked the evaluation has finished and has provided a result, hence the
   * response must be provided to the PEP
   * </p>
   * 
   * @param endAccessResponse
   *          the response provided by the UCS to which we have asked the
   *          evaluation
   */
  public void endAccessResponse(EndAccessResponse endAccessResponse);

  /**
   * Request of a reevaluation.
   * <p>
   * Whenever one of the attributes used to evaluate a request changes its value,
   * the UCS has to reevaluate the Policy against the request. Hence this function
   * can only be called by another UCS asking for a reevaluation.
   * </p>
   * TODO update the structure of the message by considering also the attributes
   * that have changed
   * 
   * @param onGoingEvaluation
   *          the message containing all the informations required for a
   *          reevaluation.
   */
  public void onGoingEvaluation(ReevaluationMessage onGoingEvaluation);

  /**
   * Response to a reevaluation
   * 
   * @param onGoingEvaluationResponse
   */
  public void onGoingEvaluationResponse(ReevaluationResponse onGoingEvaluationResponse);

  /**
   * Allows a ContextHandler to retrieve an Attribute from an AttributeManager
   * that is not directly linked with its PIP but it's linked with one of the
   * nodes inside its DHT
   * 
   * @param messagePipCh
   *          the message to be exchanged between PIP and CH
   */
  public void retrieveRemote(MessagePipCh messagePipCh);

  /**
   * Response to a retrieveRemote
   * 
   * @param messagePipCh
   *          the message to be exchanged between PIP and CH
   */
  public void retrieveRemoteResponse(MessagePipCh messagePipCh);

  /**
   * Allows a PEP to register to this UCS. TODO
   * 
   * @param message
   */
  public void register(Message message);
}

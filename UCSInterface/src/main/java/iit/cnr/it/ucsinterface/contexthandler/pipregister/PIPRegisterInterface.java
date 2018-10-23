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
package iit.cnr.it.ucsinterface.contexthandler.pipregister;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import iit.cnr.it.ucsinterface.pip.PIPCHInterface;

/**
 * This is the interface provided to the UCS to the pip register.
 * <p>
 * The PIP register is basically a database in which the UCS register the PIPs
 * it has direct control. This can be useful in the case in which some other UCS
 * needs attributes under the control of a remote UCS. <br>
 * The PIPRegister has to register new pips inside the database and it has to
 * provide the possibility of retrieving the PIPs from it.
 * </p>
 * 
 * @author antonio
 *
 */
public interface PIPRegisterInterface {
  // the logger to be used
  public static final Logger LOGGER = Logger
      .getLogger(PIPRegisterInterface.class.getName());

  /**
   * Adds all the pip inserted inside the list to the database.
   * 
   * @param pipList
   *          the list of PIPs the UCS has direct control
   * @param host
   *          the host on which the UCS is running
   * @param port
   *          the port on which the UCS is listening
   * @return true if everything goes ok, false otherwise
   */
  public boolean addPips(List<PIPCHInterface> pipList, String host,
      String port);

  /**
   * Retrieves the informations related to a particular pip starting from its
   * attribute id
   * 
   * @param attributeId
   *          the attribute id of the pip
   * @return the PIPQueryTable object representing that pip, null otherwise
   */
  public PIPQueryTable getPipQueryInformations(String attributeId);

  /**
   * Retrieves the list of attributes under a certain IP passed as paramter. In
   * this way we can check which are the attributes under the control of a
   * specific node.
   * 
   * @param ip
   *          the ip of the node in which we're interested into
   * @return the list of attributes belonging to that node
   */
  public ArrayList<PIPQueryTable> getAttributesForIP(String ip);
}

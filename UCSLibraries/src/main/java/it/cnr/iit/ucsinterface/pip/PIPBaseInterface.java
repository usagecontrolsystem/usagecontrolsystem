/*******************************************************************************
 * Copyright 2018 IIT-CNR
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package it.cnr.iit.ucsinterface.pip;

import java.util.List;
import java.util.Map;

import it.cnr.iit.xacmlutilities.Attribute;

/**
 * Base interface for a PIP.
 * <p>
 * Basically each PIP has to offer as interfaces some getters that allow the
 * context handelr to know in advance the characteristics of the PIP with which
 * it's dealing with.
 * </p>
 *
 * @author antonio
 *
 */
public interface PIPBaseInterface {
    /**
     * This function retrieves the description of the PIP. <br>
     * basically by description we mean the attribute ids this PIP monitors, this
     * is useful when the Context Handler has to register itself into the table
     * that is used by the PipRetrieval to retrieve remote attributes.
     *
     * @return the attribute id this PIP monitors
     */
    public List<String> getAttributeIds();

    /**
     * This function retrieves the list of attributes this PIP can monitor.
     * <p>
     * This function has been added in order to allow the possibility of having
     * Multi-Attributes attribute managers.
     * </p>
     *
     * @return the list of attributes this PIP is in charge of monitoring
     */
    public List<Attribute> getAttributes();

    public Map<String, Attribute> getAttributesCharacteristics();
}

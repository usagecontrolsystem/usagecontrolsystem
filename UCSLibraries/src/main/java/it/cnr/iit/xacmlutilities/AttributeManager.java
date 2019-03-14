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
package it.cnr.iit.xacmlutilities;

import java.util.ArrayList;

/**
 * This class represents an AttributeManager.
 * <p>
 * By definition we have no control over the AttributeManager, hence this object
 * simply descirbes the attribute maanger. The characteristics are:
 * <ol>
 * <li>Number of attributes</li>
 * <li>For each attribute we have:</li>
 * <ul>
 * <li>Attribute id</li>
 * <li>Category</li>
 * <li>DataType</li>
 * </ul>
 * <li>Eventual expected categoy in order to retrieve attribute specific to a
 * certain thing</li>
 * </ol>
 * </p>
 * 
 * @author antonio
 *
 */
public class AttributeManager {
    private ArrayList<Attribute> attributes;

    public AttributeManager() {
        attributes = new ArrayList<Attribute>();
    }

    public ArrayList<Attribute> getAttributes() {
        return this.attributes;
    }
}

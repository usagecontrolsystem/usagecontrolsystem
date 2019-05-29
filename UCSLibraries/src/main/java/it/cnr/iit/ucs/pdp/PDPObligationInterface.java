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
package it.cnr.iit.ucs.pdp;

/**
 * This is the interface an obligation object has to implement.
 * 
 * <p>
 * For now the interface offered by an Obligation object will contain only 2
 * functions: the first one is to know which is the entity that has to perform
 * the obligations, the other is to know which is the obligation in string
 * format.
 * </p>
 * 
 * @author antonio
 *
 */
public interface PDPObligationInterface {

    /**
     * Retrieves the name of the entity that has to perform the obligation in
     * String format
     * 
     * @return the entity that has to perform the obligation in String format
     */
    public String getEntity();

    /**
     * The obligation to be performed by the entity in string format
     * 
     * @return the obligation to be performed in String format
     */
    public String getObligation();
}

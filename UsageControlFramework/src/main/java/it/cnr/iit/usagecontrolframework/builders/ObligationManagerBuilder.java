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
package it.cnr.iit.usagecontrolframework.builders;

import java.lang.reflect.Constructor;
import java.util.List;

import it.cnr.iit.ucs.configuration.ObligationManagerProperties;
import it.cnr.iit.ucsinterface.obligationmanager.ObligationManagerInterface;
import it.cnr.iit.ucsinterface.pip.PIPOMInterface;

/**
 * This class is in charge of building the obligation manager.
 * <p>
 * </p>
 * <
 *
 * @author antonio
 *
 */
final public class ObligationManagerBuilder {

    /**
     * Private constructor to avoid instantiation
     */
    private ObligationManagerBuilder() {

    }

    /**
     * TODO comments
     *
     * @param properties
     * @param pips
     * @param pipRetrieval
     * @return
     */
    final public static ObligationManagerInterface build( ObligationManagerProperties properties,
            List<PIPOMInterface> pips, PIPOMInterface pipRetrieval ) {
        // BEGIN parameter checking
        if( properties == null || ( pips.size() == 0 && pipRetrieval == null ) ) {
            return null;
        }
        // END parameter checking
        try {
            String className = properties.getClassName();
            Constructor<?> constructor = Class.forName( className )
                .getConstructor( ObligationManagerProperties.class );
            ObligationManagerInterface obligationManagerInterface = (ObligationManagerInterface) constructor
                .newInstance( properties );
            obligationManagerInterface.setPIPs( pips, pipRetrieval );
            if( obligationManagerInterface.isInitialized() ) {
                return obligationManagerInterface;
            }
        } catch( Exception exception ) {
            exception.printStackTrace();
        }

        return null;
    }

}

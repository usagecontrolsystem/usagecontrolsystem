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
import java.util.logging.Logger;

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
public final class ObligationManagerBuilder {

    private static final Logger log = Logger.getLogger( ObligationManagerBuilder.class.getName() );

    /**
     * Private constructor to avoid instantiation
     */
    private ObligationManagerBuilder() {

    }

    /**
     * Builds the interface for the obligation manager
     *
     * @param properties
     * @param pips
     * @param pipRetrieval
     * @return
     */
    public static final ObligationManagerInterface build( ObligationManagerProperties properties,
            List<PIPOMInterface> pips, PIPOMInterface pipRetrieval ) {
        // BEGIN parameter checking
        if( properties == null || ( pips.isEmpty() && pipRetrieval == null ) ) {
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
            return obligationManagerInterface;
        } catch( Exception exception ) {
            log.severe( exception.getMessage() );
        }

        return null;
    }

}

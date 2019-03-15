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
package it.cnr.iit.usagecontrolframework.configuration;

import java.lang.reflect.Constructor;
import java.util.logging.Logger;

import it.cnr.iit.ucs.configuration.xmlclasses.XMLPip;
import it.cnr.iit.ucsinterface.pip.PIPBase;
import it.cnr.iit.ucsinterface.pip.PIPRetrieval;

/**
 * This class is in charge of building up the various PIPs.
 * <p>
 * This class is basically a factory class in charge of instantiating the
 * various PIPs the ContextHandler has access to.
 * </p>
 *
 * @author antonio
 *
 */
final public class PIPBuilder {
    private static final Logger LOGGER = Logger.getLogger( PIPBuilder.class.getName() );

    private PIPBuilder() {}

    /**
     * Builds up a pip basing on the class set in the configuration file
     *
     * @param xmlPip
     *          the configuration of the pip
     * @return the PIPInterface to be used to deal with the PIP if everything goes
     *         ok, null otherwise
     */
    final public static PIPBase build( XMLPip xmlPip ) {
        // BEGIN parameter checking
        if( xmlPip == null || xmlPip.getClassName() == null ) {
            LOGGER.warning( "Cannot build PIPBase from xmlPip" );
            return null;
        }
        // END parameter checking

        try {
            Class<?> clazz = Class.forName( xmlPip.getClassName() );
            Constructor<?> constructor = clazz.getConstructor( String.class );
            LOGGER.info( "\n" + xmlPip.getXMLPipAsString() );
            PIPBase pipBase = (PIPBase) constructor.newInstance( xmlPip.getXMLPipAsString() );
            LOGGER.info( "PIP valid : " + ( pipBase != null ) );
            return pipBase;
        } catch( Exception e ) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * This function is the same as the one above. The only thing that changes is
     * that here we will build up a PIP to be used to retrieve attributes stored
     * in remote Attribute manager to which it doesn't have direct access to,
     * hence it needs to communicate with other UCSs in order to know the value of
     * that attribute.
     *
     * @param xmlPip
     *          the PIP configuration in xml format
     * @return the PIPRetrieval object
     */
    final public static PIPRetrieval buildPIPRetrieval( XMLPip xmlPip ) {
        // BEGIN parameter checking
        if( xmlPip == null || xmlPip.getClassName() == null ) {
            return null;
        }
        // END parameter checking

        try {
            Class<?> clazz = Class.forName( xmlPip.getClassName() );
            Constructor<?> constructor = clazz.getConstructor( String.class );
            PIPRetrieval pipRetrieval = (PIPRetrieval) constructor.newInstance( xmlPip.getXMLPipAsString() );
            return pipRetrieval;
        } catch( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }

}

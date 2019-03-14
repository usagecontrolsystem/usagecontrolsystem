/*******************************************************************************
 * Copyright 2018 IIT-CNR
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package it.cnr.iit.usagecontrolframework.property;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.PropertyResourceBundle;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import it.cnr.iit.ucsinterface.ucs.AbstractProperties;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.Configuration;
import it.cnr.iit.utility.JAXBUtility;

/**
 * This class is in charge of reading the xml provided for the description of
 * the UCS.
 *
 * <p>
 * To be consistent in the whole project, the property file will be written
 * using xml markups, since we do not have na xsd schema, we will do the
 * following: we will design the model for the xml and then we will create the
 * various objects related to the various parts. In this way it's also a lot
 * simpler the process of passing to the various constructors the required
 * parameters.
 * </p>
 * <p>
 * The core functionality of this class is the Configuration class, this, and
 * all the classes it uses, are simple POJOs {plain Old Java Objects} which
 * basically allow the JAXB to map the XML tags inside java objects. In this way
 * the process of passing parameters to constructors is a lot more effective.
 * Also we gain modularity since it is very easy to add a new parameter we may
 * want to configure because to add the parameter it is sufficient to add the
 * tag in the xml file and add the corresponding element or attribute inside the
 * correct class.
 * </p>
 *
 * <p>
 * <b>READER NOTE: </b> instead of providing comments for each class, it would
 * make the code more unreadable and the names that have bben used are very
 * simple, we decided to provide a simple README in the package that contains
 * all the xml part. In this way it is also easier to update the documentation
 * as we add attributes and elements.
 * </p>
 *
 * @author antonio
 *
 */
public final class Properties extends AbstractProperties {
    private static final String APP_PROPERTIES = "classpath:application.properties";
    private static final String UCS_CONFIG = "ucs-config-file";
    private static final String DEFAULT_UCS_CONFIG = "conf.xml";

    Configuration configuration;

    private volatile boolean initialized = false;

    /**
     * Constructor for the properties class
     */
    public Properties() {
        if( !buildProperties() ) {
            return;
        }
        initialized = true;
    }

    /**
     * Build the properties returning a value that says if the building was
     * successful or not
     *
     * @return true if everything goes ok, false otherwise
     */
    private boolean buildProperties() {
        try {
            URL url = Resources.getResource( getXMLConfigurationFileName() );
            String xml = Resources.toString( url, Charsets.UTF_8 );

            configuration = JAXBUtility.unmarshalToObject( Configuration.class, xml );
            return true;
        } catch( Exception exception ) {
            exception.printStackTrace();
        }

        return false;
    }

    public Configuration getConfiguration() {
        if( initialized == false ) {
            return null;
        }
        return configuration;
    }

    @Override
    public int getThread( String string ) {
        return 0;
    }

    private String getXMLConfigurationFileName() {
        String fname = DEFAULT_UCS_CONFIG;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream( APP_PROPERTIES );
            PropertyResourceBundle rb = new PropertyResourceBundle( fis );
            if( rb.containsKey( UCS_CONFIG ) ) {
                fname = rb.getString( UCS_CONFIG );
            }
        } catch( IOException e ) {}

        if( fis != null ) {
            try {
                fis.close();
            } catch( Exception e ) {}
        }

        return fname;
    }

}

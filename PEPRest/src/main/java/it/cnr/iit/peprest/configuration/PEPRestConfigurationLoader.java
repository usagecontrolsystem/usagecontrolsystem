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
package it.cnr.iit.peprest.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.PropertyResourceBundle;
import java.util.logging.Logger;

import it.cnr.iit.utility.JsonUtility;

public final class PEPRestConfigurationLoader {

    protected static final Logger LOGGER = Logger.getLogger( PEPRestConfigurationLoader.class.getName() );

    private static final String PROPERTIES_FILE = "application.properties";
    private static final String PROPERTIES_CONFIG_KEY = "pep-config-file";
    private static final String DEFAULT_CONFIG_FILE = "conf.json";

    public static final String CONFIG_ERR_MESSAGE = "error loading the pep configuration file ...";

    private PEPRestConfigurationLoader() {}

    public static Optional<PEPRestConfiguration> getConfiguration( String fileName ) {
        File confFile = new File( PEPRestConfigurationLoader.class.getClassLoader().getResource( fileName ).getFile() );

        return JsonUtility.loadObjectFromJsonFile( confFile, PEPRestConfiguration.class );
    }

    public static Optional<PEPRestConfiguration> getConfiguration() {
        return getConfiguration( getConfigurationFileName() );
    }

    // TODO use spring
    private static String getConfigurationFileName() {
        String fname = DEFAULT_CONFIG_FILE;

        FileInputStream fis = null;
        try {
            File confFile = new File( PEPRestConfigurationLoader.class.getClassLoader()
                .getResource( PROPERTIES_FILE ).getFile() );
            fis = new FileInputStream( confFile );
            PropertyResourceBundle rb = new PropertyResourceBundle( fis );
            if( rb.containsKey( PROPERTIES_CONFIG_KEY ) ) {
                fname = rb.getString( PROPERTIES_CONFIG_KEY );
            }
        } catch( IOException e ) {
            System.out.println( e.getMessage() );
            e.printStackTrace();
        }

        if( fis != null ) {
            try {
                fis.close();
            } catch( Exception e ) {
                // nothing to do here
            }
        }

        return fname;
    }

}

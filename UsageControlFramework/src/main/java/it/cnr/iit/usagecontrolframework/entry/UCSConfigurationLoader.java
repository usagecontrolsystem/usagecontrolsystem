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
package it.cnr.iit.usagecontrolframework.entry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.PropertyResourceBundle;
import java.util.logging.Logger;

import it.cnr.iit.ucs.configuration.UCSConfiguration;
import it.cnr.iit.utility.JsonUtility;

public final class UCSConfigurationLoader {
    protected static final Logger LOGGER = Logger.getLogger( UCSConfigurationLoader.class.getName() );

    private static final String APP_PROPERTIES = "classpath:application.properties";
    private static final String PROPERTIES_CONFIG_KEY = "ucs-config-file";
    private static final String DEFAULT_CONFIG_FILE = "demo1.json";

    private UCSConfigurationLoader() {}

    public static UCSConfiguration getConfiguration( String fileName ) {
        File confFile = new File( UCSConfigurationLoader.class.getClassLoader().getResource( fileName ).getFile() );

        Optional<UCSConfiguration> optConfiguration = JsonUtility.loadObjectFromJsonFile( confFile,
            UCSConfiguration.class );
        if( !optConfiguration.isPresent() ) {
            LOGGER.severe( "Unable to load ucs configuration." );
            // TODO throw exception ?
            return null;
        }
        return optConfiguration.get();
    }

    public static UCSConfiguration getConfiguration() {
        return getConfiguration( getConfigurationFileName() );
    }

    // TODO use spring
    private static String getConfigurationFileName() {
        String fname = DEFAULT_CONFIG_FILE;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream( APP_PROPERTIES );
            PropertyResourceBundle rb = new PropertyResourceBundle( fis );
            if( rb.containsKey( PROPERTIES_CONFIG_KEY ) ) {
                fname = rb.getString( PROPERTIES_CONFIG_KEY );
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

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
package it.cnr.iit.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * This class contains all the utility function we need throughout this project.
 *
 * @author antonio
 *
 */
final public class Utility {
    private static final Logger LOGGER = Logger.getLogger( Utility.class.getName() );

    private Utility() {

    }

    /**
     * Reads a file using the passed parameter as absolute path. Returns a String
     * representing the content of the file.
     *
     * @param string
     *          a string that represents the absolute path to the file
     * @return the String that represents the content of the file
     */
    public static String readFileAbsPath( String filePath ) {
        if( !isValidPath( filePath ) ) {
            return null;
        }

        String absFilePath = findFileAbsPathUsingClassLoader( filePath );
        if( absFilePath != null ) {
            filePath = absFilePath;
        } else {
            LOGGER.info( "Attempting to read file using provided filePath." );
        }

        try {
            Scanner scanner = new Scanner( new File( filePath ) );
            StringBuilder stringB = new StringBuilder();
            while( scanner.hasNext() ) {
                stringB.append( scanner.nextLine() );
            }
            scanner.close();
            return stringB.toString();
            // return new String( Files.readAllBytes( Paths.get( filePath ) ), Charset.forName( "UTF-8" ) );
        } catch( IOException exception ) {
            LOGGER.severe( "Unable to read file due to error: " + exception.getLocalizedMessage() );
            return null;
        }
    }

    private static boolean isValidPath( String filePath ) {
        // BEGIN parameter checking
        if( filePath == null || filePath.isEmpty() ) {
            LOGGER.severe( "String for filePath can not be empty." );
            return false;
        }
        return true;
        // END parameter checking
    }

    /**
     * Return the absolute location of the file for the reader
     * @param relPath
     * @return
     */
    public static String findFileAbsPathUsingClassLoader( String relPath ) {
        if( !isValidPath( relPath ) ) {
            return null;
        }
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL input = classLoader.getResource( relPath );
            return input.getPath();
        } catch( Exception e ) {
            LOGGER.severe( "Unable to find absolute path due to error: " + e.getMessage() );
            return null;
        }
    }

    /**
     * Secure reading of the file from the absolute path TODO
     */
    public static String secureReadFileAbsPath( String string ) {
        return null;
    }

    public static <T> T retrieveConfiguration( String configFile, Class<T> configClass ) {
        try (
                InputStream stream = Thread.currentThread().getContextClassLoader().getResource( configFile ).openStream();
                BufferedReader buffer = new BufferedReader( new InputStreamReader( stream ) );) {
            String xml = "", line = "";
            while( ( line = buffer.readLine() ) != null ) {
                xml += line;
            }
            T configObj = JAXBUtility.unmarshalToObject( configClass, xml );
            return configObj;
        } catch( Exception e ) {
            LOGGER.severe( "Unable to read config file due to error: " + e.getLocalizedMessage() );
            throw new IllegalStateException( "Unable to read config file due to error: " + e.getLocalizedMessage() );
        }
    }

    public static Optional<String> getJsonStringFromObject( Object obj, boolean pretty ) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = pretty ? mapper.writerWithDefaultPrettyPrinter() : mapper.writer();

        try {
            return Optional.of( writer.writeValueAsString( obj ) );
        } catch( JsonProcessingException e ) {
            LOGGER.severe( "Error marshalling object of class : " + obj.getClass().getName() + ", " + e.getMessage() );
            return Optional.empty();
        }
    }

    public static <T> Optional<T> loadObjectFromJsonString( String jsonString, Class<T> clazz ) {
        ObjectMapper obj = new ObjectMapper();
        try {
            return Optional.of( obj.readValue( jsonString, clazz ) );
        } catch( IOException e ) {
            LOGGER.severe( "Error unmarshalling object of class : " + clazz.getName() + ", " + e.getMessage() );
            return Optional.empty();
        }
    }

    public static <T> Optional<T> loadObjectFromJsonFile( File file, Class<T> clazz ) {
        String data = null;
        try {
            data = new String( Files.readAllBytes( Paths.get( file.getAbsolutePath() ) ), Charset.forName( "UTF-8" ) );
        } catch( IOException e ) {
            LOGGER.severe( "Error reading file : " + file.getAbsolutePath() + ", " + e.getMessage() );
            return Optional.empty();
        }

        return loadObjectFromJsonString( data, clazz );
    }

    public static void dumpObjectToJsonFile( Object obj, String path, boolean pretty ) {
        try (FileOutputStream stream = new FileOutputStream( path )) {
            Optional<String> data = getJsonStringFromObject( obj, pretty );
            if( data.isPresent() ) {
                stream.write( data.get().getBytes() );
            }
        } catch( IOException e ) {
            LOGGER.severe( "Error writing file : " + path + ", " + e.getMessage() );
        }
    }

}

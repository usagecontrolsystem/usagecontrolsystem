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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Logger;

import it.cnr.iit.utility.errorhandling.Reject;

/**
 * This class contains all the utility function we need throughout this project.
 *
 * @author antonio
 *
 */
public final class FileUtility {

    private static final Logger log = Logger.getLogger( FileUtility.class.getName() );

    private FileUtility() {} // NOSONAR

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
            log.fine( "Attempting to read file using provided filePath." );
        }
        // TODO UCS-33 NOSONAR
        try (Scanner scanner = new Scanner( new File( filePath ) )) {
            StringBuilder stringB = new StringBuilder();
            while( scanner.hasNext() ) {
                stringB.append( scanner.nextLine() );
            }
            return stringB.toString();
        } catch( IOException exception ) {
            log.severe( "Unable to read file due to error: " + exception.getLocalizedMessage() );
            return null;
        }
    }

    private static boolean isValidPath( String filePath ) {
        if( filePath == null || filePath.isEmpty() ) {
            log.severe( "String for filePath can not be empty." );
            return false;
        }
        return true;
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
        File file = new File( relPath );
        if( file.exists() ) {
            return relPath;
        }
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            return Paths.get( classLoader.getResource( relPath ).toURI() ).toString();
        } catch( NullPointerException e ) {
            return null;
        } catch( Exception e ) {
            log.severe( "Unable to find absolute path due to error: " + e.getMessage() );
            return null;
        }
    }

    public static String findFolderPathUsingClassLoader( String path, Class<?> clazz ) {
        if( !isValidPath( path ) ) {
            return null;
        }
        try {
            return clazz.getResource( path ).getPath();
        } catch( NullPointerException e ) {
            return null;
        }
    }

    /**
     * Secure reading of the file from the absolute path
     */
    public static String secureReadFileAbsPath( String string ) { // NOSONAR
        return null;
    }

    public static boolean createPathIfNotExists( String path ) {
        Reject.ifNull( path );
        return createPathIfNotExists( new File( path ) );
    }

    public static boolean createPathIfNotExists( File file ) {
        Reject.ifNull( file );
        if( !file.exists() ) {
            return file.mkdir();
        }
        return true;
    }

    public static String readFileAsString( String resource ) throws URISyntaxException, IOException {
        ClassLoader classLoader = FileUtility.class.getClassLoader();
        Path path = Paths.get( classLoader.getResource( resource ).toURI() );
        byte[] data = Files.readAllBytes( path );
        return new String( data );
    }

    public static String stripExtension( String name ) {
        if( name.contains( "." ) ) {
            return name.substring( 0, name.lastIndexOf( '.' ) );
        }
        return name;
    }

}

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

/**
 * This is emuerate represents the datatype enumerate
 * <p>
 * In UXACML synatx datatypes can be many, we list here the ones that we will
 * use.
 * </p>
 * 
 * @author antonio
 *
 */
public enum DataType {
    INTEGER( "http://www.w3.org/2001/XMLSchema#integer" ),
    DOUBLE( "http://www.w3.org/2001/XMLSchema#double" ),
    STRING( "http://www.w3.org/2001/XMLSchema#string" ),
    ANYURI( "http://www.w3.org/2001/XMLSchema#anyURI" ),
    DATE( "http://www.w3.org/2001/XMLSchema#date" );

    // the string that describes the datatype
    private final String xacmlString;

    /**
     * Constructor for the datatype object
     * 
     * @param string
     *          the string that describes the datatype
     */
    DataType( String string ) {
        this.xacmlString = string;
    }

    @Override
    public String toString() {
        return xacmlString;
    }

    /**
     * Convert a string to a datatype
     * 
     * @param string
     *          the string to be converted
     * @return the datatype object correspondent to the string, null otherwise
     */
    public static DataType toDATATYPE( String string ) {
        if( string.equalsIgnoreCase( INTEGER.toString() ) ) {
            return INTEGER;
        }
        if( string.equalsIgnoreCase( DOUBLE.toString() ) ) {
            return DOUBLE;
        }
        if( string.equalsIgnoreCase( STRING.toString() ) ) {
            return STRING;
        }
        if( string.equalsIgnoreCase( ANYURI.toString() ) ) {
            return ANYURI;
        }
        if( string.equalsIgnoreCase( DATE.toString() ) ) {
            return DATE;
        }
        return null;
    }
}

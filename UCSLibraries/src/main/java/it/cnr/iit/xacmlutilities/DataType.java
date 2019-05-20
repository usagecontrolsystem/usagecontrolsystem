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
 * This is enumerate represents the dataType enumerate
 * <p>
 * In UXACML syntax data-types can be many, we list here the ones that we will
 * use.
 * </p>
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public enum DataType {
    INTEGER( "http://www.w3.org/2001/XMLSchema#integer" ),
    DOUBLE( "http://www.w3.org/2001/XMLSchema#double" ),
    STRING( "http://www.w3.org/2001/XMLSchema#string" ),
    ANYURI( "http://www.w3.org/2001/XMLSchema#anyURI" ),
    DATE( "http://www.w3.org/2001/XMLSchema#date" );

    // the string that describes the dataType
    private final String dataType;

    /**
     * Constructor for the dataType object
     *
     * @param dataType
     *          the string that describes the dataType
     */
    DataType( String dataType ) {
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return dataType;
    }

    /**
     * Convert a string to a dataType
     *
     * @param dataType
     *          the string to be converted
     * @return the dataType object correspondent to the string, null otherwise
     */
    public static DataType toDATATYPE( String dataType ) {
        if( dataType.equalsIgnoreCase( INTEGER.toString() ) ) {
            return INTEGER;
        }
        if( dataType.equalsIgnoreCase( DOUBLE.toString() ) ) {
            return DOUBLE;
        }
        if( dataType.equalsIgnoreCase( STRING.toString() ) ) {
            return STRING;
        }
        if( dataType.equalsIgnoreCase( ANYURI.toString() ) ) {
            return ANYURI;
        }
        if( dataType.equalsIgnoreCase( DATE.toString() ) ) {
            return DATE;
        }
        return null;
    }
}

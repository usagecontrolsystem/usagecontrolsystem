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
package it.cnr.iit.xacml;

/**
 * This is enumerate represents the dataType enumerate
 * In UXACML syntax data-types can be many, we list here the ones that we will
 * use.
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public enum DataType {
    STRING( "http://www.w3.org/2001/XMLSchema#string" ),
    BOOLEAN( "http://www.w3.org/2001/XMLSchema#boolean" ),
    INTEGER( "http://www.w3.org/2001/XMLSchema#integer" ),
    DOUBLE( "http://www.w3.org/2001/XMLSchema#double" ),
    TIME( "http://www.w3.org/2001/XMLSchema#time" ),
    DATE( "http://www.w3.org/2001/XMLSchema#date" ),
    DATETIME( "http://www.w3.org/2001/XMLSchema#dateTime" ),
    ANYURI( "http://www.w3.org/2001/XMLSchema#anyURI" ),
    HEXBINARY( "http://www.w3.org/2001/XMLSchema#hexBinary" ),
    BASE64BINARY( "http://www.w3.org/2001/XMLSchema#base64Binary" ),
    DAYTIMEDURATION( "http://www.w3.org/2001/XMLSchema#dayTimeDuration" ),
    YEARMONTHDURATION( "http://www.w3.org/2001/XMLSchema#yearMonthDuration" );

    private final String payload;

    DataType( String payload ) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return payload;
    }

    /**
     * Convert a string to a dataType
     *
     * @param payload
     *          the string to be converted
     * @return the dataType object correspondent to the string, null otherwise
     */
    public static DataType toDATATYPE( String payload ) {
        if( payload.equalsIgnoreCase( STRING.toString() ) ) {
            return STRING;
        } else if( payload.equalsIgnoreCase( BOOLEAN.toString() ) ) {
            return BOOLEAN;
        } else if( payload.equalsIgnoreCase( INTEGER.toString() ) ) {
            return INTEGER;
        } else if( payload.equalsIgnoreCase( DOUBLE.toString() ) ) {
            return DOUBLE;
        } else if( payload.equalsIgnoreCase( TIME.toString() ) ) {
            return TIME;
        } else if( payload.equalsIgnoreCase( DATE.toString() ) ) {
            return DATE;
        } else if( payload.equalsIgnoreCase( DATETIME.toString() ) ) {
            return DATETIME;
        } else if( payload.equalsIgnoreCase( ANYURI.toString() ) ) {
            return ANYURI;
        } else if( payload.equalsIgnoreCase( HEXBINARY.toString() ) ) {
            return HEXBINARY;
        } else if( payload.equalsIgnoreCase( BASE64BINARY.toString() ) ) {
            return BASE64BINARY;
        } else if( payload.equalsIgnoreCase( DAYTIMEDURATION.toString() ) ) {
            return DAYTIMEDURATION;
        } else if( payload.equalsIgnoreCase( YEARMONTHDURATION.toString() ) ) {
            return YEARMONTHDURATION;
        }
        return null;
    }
}

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
 * In XACML syntax the category can be one of:
 * Subject, Resource, Action, Environment.
 * This enumerate is used to hold the String that represents the category in the
 * UXACML format
 *
 * @author Antonio La Marra
 */
public enum Category {
    ACTION( "urn:oasis:names:tc:xacml:3.0:attribute-category:action" ),
    RESOURCE( "urn:oasis:names:tc:xacml:3.0:attribute-category:resource" ),
    SUBJECT( "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" ),
    ENVIRONMENT( "urn:oasis:names:tc:xacml:3.0:attribute-category:environment" ),
    UCS("urn:uxacml:names:xacml:3.0:attribute-category:ucs");

    private final String payload;

    Category( String payload ) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return payload;
    }

    /**
     * Converts the string passed as parameter to a CATEGORY enumerate
     *
     * @param payload
     *          the string we want to convert
     * @return the CATEGORY object correspondent to that string if any, null
     *         otherwise
     */
    public static Category toCATEGORY( String payload ) {
        if( payload.equalsIgnoreCase( ACTION.toString() ) ) {
            return ACTION;
        } else if( payload.equalsIgnoreCase( RESOURCE.toString() ) ) {
            return RESOURCE;
        } else if( payload.equalsIgnoreCase( SUBJECT.toString() ) ) {
            return SUBJECT;
        } else if( payload.equalsIgnoreCase( ENVIRONMENT.toString() ) ) {
            return ENVIRONMENT;
        }
        return null;
    }

    public boolean contains( String payload ) {
        return this.payload.contains( payload );
    }

}

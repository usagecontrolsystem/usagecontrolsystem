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
 * This is the category enumerate.
 *
 * <p>
 * In XACML syntax Category can be one of these 4 possible values:
 * <ol>
 * <li>Subject</li>
 * <li>Resource</li>
 * <li>Action</li>
 * <li>Environment</li>
 * </ol>
 * This enumerate is used to hold the String that represents the category in the
 * UXACML format
 * </p>
 *
 * @author antonio
 *
 */
public enum Category {
    ACTION( "urn:oasis:names:tc:xacml:3.0:attribute-category:action" ),
    RESOURCE( "urn:oasis:names:tc:xacml:3.0:attribute-category:resource" ),
    SUBJECT( "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" ),
    ENVIRONMENT( "urn:oasis:names:tc:xacml:3.0:attribute-category:environment" );

    // the string representing the category in xacmlformat
    private final String payload;

    /**
     * constructor for the category enumerate
     *
     * @param payload
     *          the string that identifies the category
     */
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
        }
        if( payload.equalsIgnoreCase( RESOURCE.toString() ) ) {
            return RESOURCE;
        }
        if( payload.equalsIgnoreCase( SUBJECT.toString() ) ) {
            return SUBJECT;
        }
        if( payload.equalsIgnoreCase( ENVIRONMENT.toString() ) ) {
            return ENVIRONMENT;
        }
        return null;
    }

    public boolean contains( String payload ) {
        return this.payload.contains( payload );
    }

}

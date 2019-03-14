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
package it.cnr.iit.ucsinterface.contexthandler.scheduler;

/**
 * By using this enumerate we want to express the performance in numeric and
 * string terms.
 *
 * @author antonio
 *
 */
public enum PERFORMANCE_METRICS {
    HIGH( 3 ),
    MEDIUM( 2 ),
    LOW( 1 );

    private int value;
    private String valueString;

    PERFORMANCE_METRICS( int value ) {
        this.value = value;
        switch( value ) {
            case 1:
                valueString = "LOW";
                break;
            case 2:
                valueString = "MEDIUM";
                break;
            case 3:
                valueString = "HIGH";
                break;
            default:
                valueString = null;
        }
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return valueString;
    }

    public static PERFORMANCE_METRICS fromString( String string ) {
        // BEGIN parameter checking
        if( string == null || string.isEmpty() ) {
            return null;
        }
        // END parameter checking

        if( string.equals( HIGH.valueString ) ) {
            return HIGH;
        }
        if( string.equals( LOW.valueString ) ) {
            return LOW;
        }
        if( string.equals( MEDIUM.valueString ) ) {
            return MEDIUM;
        }
        return null;
    }
}

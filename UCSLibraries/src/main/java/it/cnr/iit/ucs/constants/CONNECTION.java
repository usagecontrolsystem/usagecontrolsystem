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
package it.cnr.iit.ucs.constants;

/**
 *  Constants to be used in the communication tag in the xml file
 *  */
public enum CONNECTION {
    API( "API" ),
    REST_API( "REST_API" ),
    SOCKET( "SOCKET" );

    private String string;

    private CONNECTION( String string ) {
        this.string = string;
    }

    public static CONNECTION getCONNECTION( String string ) {
        if( string.equals( API.toString() ) ) {
            return API;
        } else if( string.equals( REST_API.toString() ) ) {
            return REST_API;
        } else if( string.equals( SOCKET.toString() ) ) {
            return SOCKET;
        } else {
            return null;
        }

    }

    @Override
    public String toString() {
        return string;
    }

}

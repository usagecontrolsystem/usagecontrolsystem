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
package oasis.names.tc.xacml.core.schema.wd_17;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType( name = "EffectType" )
@XmlEnum
public enum EffectType {

    @XmlEnumValue( "Permit" )
    PERMIT("Permit" ),
    @XmlEnumValue( "Deny" )
    DENY("Deny" );
    private final String value;

    EffectType( String v ) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EffectType fromValue( String v ) {
        for( EffectType c : EffectType.values() ) {
            if( c.value.equals( v ) ) {
                return c;
            }
        }
        throw new IllegalArgumentException( v );
    }

}

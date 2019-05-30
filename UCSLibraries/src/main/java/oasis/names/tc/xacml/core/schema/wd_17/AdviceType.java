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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType( XmlAccessType.FIELD )
@XmlType( name = "AdviceType", propOrder = {
    "attributeAssignment"
} )
public class AdviceType {

    @XmlElement( name = "AttributeAssignment" )
    protected List<AttributeAssignmentType> attributeAssignment;
    @XmlAttribute( name = "AdviceId", required = true )
    @XmlSchemaType( name = "anyURI" )
    protected String adviceId;

    public List<AttributeAssignmentType> getAttributeAssignment() {
        if( attributeAssignment == null ) {
            attributeAssignment = new ArrayList<>();
        }
        return this.attributeAssignment;
    }

    public String getAdviceId() {
        return adviceId;
    }

    public void setAdviceId( String value ) {
        this.adviceId = value;
    }

}

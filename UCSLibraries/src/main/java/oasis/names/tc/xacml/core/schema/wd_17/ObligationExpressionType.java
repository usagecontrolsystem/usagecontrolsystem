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
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType( XmlAccessType.FIELD )
@XmlType( name = "ObligationExpressionType" )
public class ObligationExpressionType {

    @XmlElement( name = "AttributeAssignmentExpression" )
    protected List<AttributeAssignmentExpressionType> attributeAssignmentExpression;
    @XmlAttribute( name = "ObligationId", required = true )
    // @XmlSchemaType(name = "anyURI")
    protected String obligationId;
    @XmlAttribute( name = "FulfillOn", required = true )
    protected EffectType fulfillOn;

    public List<AttributeAssignmentExpressionType> getAttributeAssignmentExpression() {
        if( attributeAssignmentExpression == null ) {
            attributeAssignmentExpression = new ArrayList<>();
        }
        return this.attributeAssignmentExpression;
    }

    public String getObligationId() {
        return obligationId;
    }

    public void setObligationId( String value ) {
        this.obligationId = value;
    }

    public EffectType getFulfillOn() {
        return fulfillOn;
    }

    public void setFulfillOn( EffectType value ) {
        this.fulfillOn = value;
    }

}

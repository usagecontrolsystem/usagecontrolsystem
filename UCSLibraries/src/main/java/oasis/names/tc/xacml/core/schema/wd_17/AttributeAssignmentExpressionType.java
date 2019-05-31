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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType( XmlAccessType.FIELD )
@XmlType( name = "AttributeAssignmentExpressionType", propOrder = {
    "expression"
} )
public class AttributeAssignmentExpressionType {

    @XmlElementRef( name = "Expression", namespace = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17", type = JAXBElement.class )
    protected JAXBElement<?> expression;
    @XmlAttribute( name = "AttributeId", required = true )
    @XmlSchemaType( name = "anyURI" )
    protected String attributeId;
    @XmlAttribute( name = "Category" )
    @XmlSchemaType( name = "anyURI" )
    protected String category;
    @XmlAttribute( name = "Issuer" )
    protected String issuer;

    public JAXBElement<?> getExpression() { // NOSONAR
        return expression;
    }

    public void setExpression( JAXBElement<?> value ) {
        this.expression = value;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId( String value ) {
        this.attributeId = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory( String value ) {
        this.category = value;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer( String value ) {
        this.issuer = value;
    }

}

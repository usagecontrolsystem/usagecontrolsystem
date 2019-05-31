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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType( XmlAccessType.FIELD )
@XmlType( name = "ApplyType", propOrder = {
    "description",
    "expression"
} )
public class ApplyType
        extends ExpressionType {

    @XmlElement( name = "Description" )
    protected String description;
    @XmlElementRef( name = "Expression", namespace = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17", type = JAXBElement.class,
        required = false )
    protected List<JAXBElement<?>> expression;
    @XmlAttribute( name = "FunctionId", required = true )
    @XmlSchemaType( name = "anyURI" )
    protected String functionId;

    public String getDescription() {
        return description;
    }

    public void setDescription( String value ) {
        this.description = value;
    }

    public List<JAXBElement<?>> getExpression() { // NOSONAR
        if( expression == null ) {
            expression = new ArrayList<>();
        }
        return this.expression;
    }

    String getFunctionId() {
        return functionId;
    }

    public void setFunctionId( String value ) {
        this.functionId = value;
    }

}

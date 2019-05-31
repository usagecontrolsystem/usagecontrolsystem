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
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType( XmlAccessType.FIELD )
@XmlType( name = "ConditionType", propOrder = { "expression" } )
public class ConditionType {

    @XmlElementRef( name = "Expression",
        namespace = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17",
        type = JAXBElement.class )
    protected JAXBElement<?> expression;
    @XmlAttribute( name = "DecisionTime", required = true )
    protected String decisionTime;

    public JAXBElement<?> getExpression() { // NOSONAR
        return expression;
    }

    public void setExpression( JAXBElement<?> value ) {
        this.expression = value;
    }

    public String getDecisionTime() {
        return decisionTime;
    }

    public void setDecisionTime( String value ) {
        this.decisionTime = value;
    }

}

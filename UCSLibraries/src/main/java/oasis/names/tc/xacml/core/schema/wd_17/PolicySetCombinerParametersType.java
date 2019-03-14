/*******************************************************************************
 * Copyright 2018 IIT-CNR
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2017.04.24 alle 12:34:54 PM CEST 
//


package oasis.names.tc.xacml.core.schema.wd_17;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per PolicySetCombinerParametersType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="PolicySetCombinerParametersType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}CombinerParametersType">
 *       &lt;attribute name="PolicySetIdRef" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicySetCombinerParametersType")
public class PolicySetCombinerParametersType
    extends CombinerParametersType
{

    @XmlAttribute(name = "PolicySetIdRef", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String policySetIdRef;

    /**
     * Recupera il valore della proprietà policySetIdRef.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicySetIdRef() {
        return policySetIdRef;
    }

    /**
     * Imposta il valore della proprietà policySetIdRef.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicySetIdRef(String value) {
        this.policySetIdRef = value;
    }

}

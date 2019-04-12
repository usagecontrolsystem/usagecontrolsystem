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
//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine.
// Generato il: 2017.04.24 alle 12:34:54 PM CEST
//

package oasis.names.tc.xacml.core.schema.wd_17;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Classe Java per ResultType complex type.
 *
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 *
 * <pre>
 * &lt;complexType name="ResultType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}Decision"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}Status" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}Obligations" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}AssociatedAdvice" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}Attributes" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}PolicyIdentifierList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType( XmlAccessType.FIELD )
@XmlType( name = "ResultType", propOrder = {
    "decision",
    "status",
    "obligations",
    "associatedAdvice",
    "attributes",
    "policyIdentifierList"
} )
public class ResultType {

    @XmlElement( name = "Decision", required = true )
    @XmlSchemaType( name = "string" )
    protected DecisionType decision;
    @XmlElement( name = "Status" )
    protected StatusType status;
    @XmlElement( name = "Obligations" )
    protected ObligationsType obligations;
    @XmlElement( name = "AssociatedAdvice" )
    protected AssociatedAdviceType associatedAdvice;
    @XmlElement( name = "Attributes" )
    protected List<AttributesType> attributes;
    @XmlElement( name = "PolicyIdentifierList" )
    protected PolicyIdentifierListType policyIdentifierList;

    /**
     * Recupera il valore della proprietà decision.
     *
     * @return
     *     possible object is
     *     {@link DecisionType }
     *
     */
    public DecisionType getDecision() {
        return decision;
    }

    /**
     * Imposta il valore della proprietà decision.
     *
     * @param value
     *     allowed object is
     *     {@link DecisionType }
     *
     */
    public void setDecision( DecisionType value ) {
        this.decision = value;
    }

    /**
     * Recupera il valore della proprietà status.
     *
     * @return
     *     possible object is
     *     {@link StatusType }
     *
     */
    public StatusType getStatus() {
        return status;
    }

    /**
     * Imposta il valore della proprietà status.
     *
     * @param value
     *     allowed object is
     *     {@link StatusType }
     *
     */
    public void setStatus( StatusType value ) {
        this.status = value;
    }

    /**
     * Recupera il valore della proprietà obligations.
     *
     * @return
     *     possible object is
     *     {@link ObligationsType }
     *
     */
    public ObligationsType getObligations() {
        return obligations;
    }

    /**
     * Imposta il valore della proprietà obligations.
     *
     * @param value
     *     allowed object is
     *     {@link ObligationsType }
     *
     */
    public void setObligations( ObligationsType value ) {
        this.obligations = value;
    }

    /**
     * Recupera il valore della proprietà associatedAdvice.
     *
     * @return
     *     possible object is
     *     {@link AssociatedAdviceType }
     *
     */
    public AssociatedAdviceType getAssociatedAdvice() {
        return associatedAdvice;
    }

    /**
     * Imposta il valore della proprietà associatedAdvice.
     *
     * @param value
     *     allowed object is
     *     {@link AssociatedAdviceType }
     *
     */
    public void setAssociatedAdvice( AssociatedAdviceType value ) {
        this.associatedAdvice = value;
    }

    /**
     * Gets the value of the attributes property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attributes property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttributes().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AttributesType }
     *
     *
     */
    public List<AttributesType> getAttributes() {
        if( attributes == null ) {
            attributes = new ArrayList<>();
        }
        return this.attributes;
    }

    /**
     * Recupera il valore della proprietà policyIdentifierList.
     *
     * @return
     *     possible object is
     *     {@link PolicyIdentifierListType }
     *
     */
    public PolicyIdentifierListType getPolicyIdentifierList() {
        return policyIdentifierList;
    }

    /**
     * Imposta il valore della proprietà policyIdentifierList.
     *
     * @param value
     *     allowed object is
     *     {@link PolicyIdentifierListType }
     *
     */
    public void setPolicyIdentifierList( PolicyIdentifierListType value ) {
        this.policyIdentifierList = value;
    }

}

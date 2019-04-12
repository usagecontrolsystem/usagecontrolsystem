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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Classe Java per RuleType complex type.
 *
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 *
 * <pre>
 * &lt;complexType name="RuleType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}Description" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}Target" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}Condition" maxOccurs="3" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}ObligationExpressions" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}AdviceExpressions" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="RuleId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Effect" use="required" type="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}EffectType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType( XmlAccessType.FIELD )
@XmlType( name = "RuleType", propOrder = {
    "description",
    "target",
    "condition",
    "obligationExpressions",
    "adviceExpressions"
} )
public class RuleType {

    @XmlElement( name = "Description" )
    protected String description;
    @XmlElement( name = "Target" )
    protected TargetType target;
    @XmlElement( name = "Condition" )
    protected List<ConditionType> condition;
    @XmlElement( name = "ObligationExpressions" )
    protected ObligationExpressionsType obligationExpressions;
    @XmlElement( name = "AdviceExpressions" )
    protected AdviceExpressionsType adviceExpressions;
    @XmlAttribute( name = "RuleId", required = true )
    protected String ruleId;
    @XmlAttribute( name = "Effect", required = true )
    protected EffectType effect;

    /**
     * Recupera il valore della proprietà description.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDescription() {
        return description;
    }

    /**
     * Imposta il valore della proprietà description.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDescription( String value ) {
        this.description = value;
    }

    /**
     * Recupera il valore della proprietà target.
     *
     * @return
     *     possible object is
     *     {@link TargetType }
     *
     */
    public TargetType getTarget() {
        return target;
    }

    /**
     * Imposta il valore della proprietà target.
     *
     * @param value
     *     allowed object is
     *     {@link TargetType }
     *
     */
    public void setTarget( TargetType value ) {
        this.target = value;
    }

    /**
     * Gets the value of the condition property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the condition property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCondition().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConditionType }
     *
     *
     */
    public List<ConditionType> getCondition() {
        if( condition == null ) {
            condition = new ArrayList<>();
        }
        return this.condition;
    }

    /**
     * Recupera il valore della proprietà obligationExpressions.
     *
     * @return
     *     possible object is
     *     {@link ObligationExpressionsType }
     *
     */
    public ObligationExpressionsType getObligationExpressions() {
        return obligationExpressions;
    }

    /**
     * Imposta il valore della proprietà obligationExpressions.
     *
     * @param value
     *     allowed object is
     *     {@link ObligationExpressionsType }
     *
     */
    public void setObligationExpressions( ObligationExpressionsType value ) {
        this.obligationExpressions = value;
    }

    /**
     * Recupera il valore della proprietà adviceExpressions.
     *
     * @return
     *     possible object is
     *     {@link AdviceExpressionsType }
     *
     */
    public AdviceExpressionsType getAdviceExpressions() {
        return adviceExpressions;
    }

    /**
     * Imposta il valore della proprietà adviceExpressions.
     *
     * @param value
     *     allowed object is
     *     {@link AdviceExpressionsType }
     *
     */
    public void setAdviceExpressions( AdviceExpressionsType value ) {
        this.adviceExpressions = value;
    }

    /**
     * Recupera il valore della proprietà ruleId.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRuleId() {
        return ruleId;
    }

    /**
     * Imposta il valore della proprietà ruleId.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRuleId( String value ) {
        this.ruleId = value;
    }

    /**
     * Recupera il valore della proprietà effect.
     *
     * @return
     *     possible object is
     *     {@link EffectType }
     *
     */
    public EffectType getEffect() {
        return effect;
    }

    /**
     * Imposta il valore della proprietà effect.
     *
     * @param value
     *     allowed object is
     *     {@link EffectType }
     *
     */
    public void setEffect( EffectType value ) {
        this.effect = value;
    }

}

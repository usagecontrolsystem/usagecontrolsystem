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
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB)
// Reference Implementation, v2.2.8-b130911.1802
// Vedere <a
// href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello
// schema di origine.
// Generato il: 2017.04.24 alle 12:34:54 PM CEST
//

package oasis.names.tc.xacml.core.schema.wd_17;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import it.cnr.iit.utility.JAXBUtility;

/**
 * <p>
 * Classe Java per PolicyType complex type.
 * 
 * <p>
 * Il seguente frammento di schema specifica il contenuto previsto contenuto in
 * questa classe.
 * 
 * <pre>
 * &lt;complexType name="PolicyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}Description" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}PolicyIssuer" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}PolicyDefaults" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}Target"/>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}CombinerParameters" minOccurs="0"/>
 *           &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}RuleCombinerParameters" minOccurs="0"/>
 *           &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}VariableDefinition"/>
 *           &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}Rule"/>
 *         &lt;/choice>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}ObligationExpressions" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}AdviceExpressions" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="PolicyId" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="Version" use="required" type="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}VersionType" />
 *       &lt;attribute name="RuleCombiningAlgId" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="MaxDelegationDepth" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyType",
    propOrder = { "description", "policyIssuer", "policyDefaults", "target",
        "combinerParametersOrRuleCombinerParametersOrVariableDefinition",
        "obligationExpressions", "adviceExpressions" })
public class PolicyType {
	
	@XmlElement(name = "Description")
	protected String										description;
	@XmlElement(name = "PolicyIssuer")
	protected PolicyIssuerType					policyIssuer;
	@XmlElement(name = "PolicyDefaults")
	protected DefaultsType							policyDefaults;
	@XmlElement(name = "Target", required = true)
	protected TargetType								target;
	@XmlElements({
	    @XmlElement(name = "CombinerParameters",
	        type = CombinerParametersType.class),
	    @XmlElement(name = "RuleCombinerParameters",
	        type = RuleCombinerParametersType.class),
	    @XmlElement(name = "VariableDefinition",
	        type = VariableDefinitionType.class),
	    @XmlElement(name = "Rule", type = RuleType.class) })
	protected List<Object>							combinerParametersOrRuleCombinerParametersOrVariableDefinition;
	@XmlElement(name = "ObligationExpressions")
	protected ObligationExpressionsType	obligationExpressions;
	@XmlElement(name = "AdviceExpressions")
	protected AdviceExpressionsType			adviceExpressions;
	@XmlAttribute(name = "PolicyId", required = true)
	@XmlSchemaType(name = "anyURI")
	protected String										policyId;
	@XmlAttribute(name = "Version", required = true)
	protected String										version;
	@XmlAttribute(name = "RuleCombiningAlgId", required = true)
	@XmlSchemaType(name = "anyURI")
	protected String										ruleCombiningAlgId;
	@XmlAttribute(name = "MaxDelegationDepth")
	protected BigInteger								maxDelegationDepth;
	
	/**
	 * Recupera il valore della proprietà description.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Imposta il valore della proprietà description.
	 * 
	 * @param value
	 *          allowed object is {@link String }
	 * 
	 */
	public void setDescription(String value) {
		this.description = value;
	}
	
	/**
	 * Recupera il valore della proprietà policyIssuer.
	 * 
	 * @return possible object is {@link PolicyIssuerType }
	 * 
	 */
	public PolicyIssuerType getPolicyIssuer() {
		return policyIssuer;
	}
	
	/**
	 * Imposta il valore della proprietà policyIssuer.
	 * 
	 * @param value
	 *          allowed object is {@link PolicyIssuerType }
	 * 
	 */
	public void setPolicyIssuer(PolicyIssuerType value) {
		this.policyIssuer = value;
	}
	
	/**
	 * Recupera il valore della proprietà policyDefaults.
	 * 
	 * @return possible object is {@link DefaultsType }
	 * 
	 */
	public DefaultsType getPolicyDefaults() {
		return policyDefaults;
	}
	
	/**
	 * Imposta il valore della proprietà policyDefaults.
	 * 
	 * @param value
	 *          allowed object is {@link DefaultsType }
	 * 
	 */
	public void setPolicyDefaults(DefaultsType value) {
		this.policyDefaults = value;
	}
	
	/**
	 * Recupera il valore della proprietà target.
	 * 
	 * @return possible object is {@link TargetType }
	 * 
	 */
	public TargetType getTarget() {
		return target;
	}
	
	/**
	 * Imposta il valore della proprietà target.
	 * 
	 * @param value
	 *          allowed object is {@link TargetType }
	 * 
	 */
	public void setTarget(TargetType value) {
		this.target = value;
	}
	
	/**
	 * Gets the value of the
	 * combinerParametersOrRuleCombinerParametersOrVariableDefinition property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot.
	 * Therefore any modification you make to the returned list will be present
	 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
	 * for the combinerParametersOrRuleCombinerParametersOrVariableDefinition
	 * property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getCombinerParametersOrRuleCombinerParametersOrVariableDefinition()
	 *     .add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link CombinerParametersType } {@link RuleCombinerParametersType }
	 * {@link VariableDefinitionType } {@link RuleType }
	 * 
	 * 
	 */
	public List<Object> getCombinerParametersOrRuleCombinerParametersOrVariableDefinition() {
		if (combinerParametersOrRuleCombinerParametersOrVariableDefinition == null) {
			combinerParametersOrRuleCombinerParametersOrVariableDefinition = new ArrayList<Object>();
		}
		return this.combinerParametersOrRuleCombinerParametersOrVariableDefinition;
	}
	
	/**
	 * Recupera il valore della proprietà obligationExpressions.
	 * 
	 * @return possible object is {@link ObligationExpressionsType }
	 * 
	 */
	public ObligationExpressionsType getObligationExpressions() {
		return obligationExpressions;
	}
	
	/**
	 * Imposta il valore della proprietà obligationExpressions.
	 * 
	 * @param value
	 *          allowed object is {@link ObligationExpressionsType }
	 * 
	 */
	public void setObligationExpressions(ObligationExpressionsType value) {
		this.obligationExpressions = value;
	}
	
	/**
	 * Recupera il valore della proprietà adviceExpressions.
	 * 
	 * @return possible object is {@link AdviceExpressionsType }
	 * 
	 */
	public AdviceExpressionsType getAdviceExpressions() {
		return adviceExpressions;
	}
	
	/**
	 * Imposta il valore della proprietà adviceExpressions.
	 * 
	 * @param value
	 *          allowed object is {@link AdviceExpressionsType }
	 * 
	 */
	public void setAdviceExpressions(AdviceExpressionsType value) {
		this.adviceExpressions = value;
	}
	
	/**
	 * Recupera il valore della proprietà policyId.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getPolicyId() {
		return policyId;
	}
	
	/**
	 * Imposta il valore della proprietà policyId.
	 * 
	 * @param value
	 *          allowed object is {@link String }
	 * 
	 */
	public void setPolicyId(String value) {
		this.policyId = value;
	}
	
	/**
	 * Recupera il valore della proprietà version.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getVersion() {
		return version;
	}
	
	/**
	 * Imposta il valore della proprietà version.
	 * 
	 * @param value
	 *          allowed object is {@link String }
	 * 
	 */
	public void setVersion(String value) {
		this.version = value;
	}
	
	/**
	 * Recupera il valore della proprietà ruleCombiningAlgId.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getRuleCombiningAlgId() {
		return ruleCombiningAlgId;
	}
	
	/**
	 * Imposta il valore della proprietà ruleCombiningAlgId.
	 * 
	 * @param value
	 *          allowed object is {@link String }
	 * 
	 */
	public void setRuleCombiningAlgId(String value) {
		this.ruleCombiningAlgId = value;
	}
	
	/**
	 * Recupera il valore della proprietà maxDelegationDepth.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getMaxDelegationDepth() {
		return maxDelegationDepth;
	}
	
	/**
	 * Imposta il valore della proprietà maxDelegationDepth.
	 * 
	 * @param value
	 *          allowed object is {@link BigInteger }
	 * 
	 */
	public void setMaxDelegationDepth(BigInteger value) {
		this.maxDelegationDepth = value;
	}
	
	public String getPolicyTypeAsString() {
		try {
			String string = JAXBUtility.marshalToString(PolicyType.class, this,
			    "Policy", JAXBUtility.SCHEMA);
			return string;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public PolicyType copy() {
		try {
			return JAXBUtility.unmarshalToObject(PolicyType.class,
			    this.getPolicyTypeAsString());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public void removeOtherRules(String ruleId) {
		for (int i = 0; i < combinerParametersOrRuleCombinerParametersOrVariableDefinition
		    .size(); i++) {
			Object obj = combinerParametersOrRuleCombinerParametersOrVariableDefinition
			    .get(i);
			if (obj instanceof RuleType) {
				RuleType rule = (RuleType) obj;
				if (!rule.getRuleId().equals(ruleId)) {
					combinerParametersOrRuleCombinerParametersOrVariableDefinition
					    .remove(i);
				}
			}
		}
	}
	
}

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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType( XmlAccessType.FIELD )
@XmlType( name = "PolicySetType", propOrder = {
    "description",
    "policyIssuer",
    "policySetDefaults",
    "target",
    "policySetOrPolicyOrPolicySetIdReference",
    "obligationExpressions",
    "adviceExpressions"
} )
public class PolicySetType {

    @XmlElement( name = "Description" )
    protected String description;
    @XmlElement( name = "PolicyIssuer" )
    protected PolicyIssuerType policyIssuer;
    @XmlElement( name = "PolicySetDefaults" )
    protected DefaultsType policySetDefaults;
    @XmlElement( name = "Target", required = true )
    protected TargetType target;
    @XmlElementRefs( {
        @XmlElementRef( name = "CombinerParameters", namespace = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17", type = JAXBElement.class,
            required = false ),
        @XmlElementRef( name = "PolicySetIdReference", namespace = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17",
            type = JAXBElement.class, required = false ),
        @XmlElementRef( name = "Policy", namespace = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17", type = JAXBElement.class,
            required = false ),
        @XmlElementRef( name = "PolicyCombinerParameters", namespace = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17",
            type = JAXBElement.class, required = false ),
        @XmlElementRef( name = "PolicySet", namespace = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17", type = JAXBElement.class,
            required = false ),
        @XmlElementRef( name = "PolicyIdReference", namespace = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17", type = JAXBElement.class,
            required = false ),
        @XmlElementRef( name = "PolicySetCombinerParameters", namespace = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17",
            type = JAXBElement.class, required = false )
    } )
    protected List<JAXBElement<?>> policySetOrPolicyOrPolicySetIdReference;
    @XmlElement( name = "ObligationExpressions" )
    protected ObligationExpressionsType obligationExpressions;
    @XmlElement( name = "AdviceExpressions" )
    protected AdviceExpressionsType adviceExpressions;
    @XmlAttribute( name = "PolicySetId", required = true )
    @XmlSchemaType( name = "anyURI" )
    protected String policySetId;
    @XmlAttribute( name = "Version", required = true )
    protected String version;
    @XmlAttribute( name = "PolicyCombiningAlgId", required = true )
    @XmlSchemaType( name = "anyURI" )
    protected String policyCombiningAlgId;
    @XmlAttribute( name = "MaxDelegationDepth" )
    protected BigInteger maxDelegationDepth;

    public String getDescription() {
        return description;
    }

    public void setDescription( String value ) {
        this.description = value;
    }

    public PolicyIssuerType getPolicyIssuer() {
        return policyIssuer;
    }

    public void setPolicyIssuer( PolicyIssuerType value ) {
        this.policyIssuer = value;
    }

    public DefaultsType getPolicySetDefaults() {
        return policySetDefaults;
    }

    public void setPolicySetDefaults( DefaultsType value ) {
        this.policySetDefaults = value;
    }

    public TargetType getTarget() {
        return target;
    }

    public void setTarget( TargetType value ) {
        this.target = value;
    }

    public List<JAXBElement<?>> getPolicySetOrPolicyOrPolicySetIdReference() { // NOSONAR
        if( policySetOrPolicyOrPolicySetIdReference == null ) {
            policySetOrPolicyOrPolicySetIdReference = new ArrayList<>();
        }
        return this.policySetOrPolicyOrPolicySetIdReference;
    }

    public ObligationExpressionsType getObligationExpressions() {
        return obligationExpressions;
    }

    public void setObligationExpressions( ObligationExpressionsType value ) {
        this.obligationExpressions = value;
    }

    public AdviceExpressionsType getAdviceExpressions() {
        return adviceExpressions;
    }

    public void setAdviceExpressions( AdviceExpressionsType value ) {
        this.adviceExpressions = value;
    }

    public String getPolicySetId() {
        return policySetId;
    }

    public void setPolicySetId( String value ) {
        this.policySetId = value;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion( String value ) {
        this.version = value;
    }

    public String getPolicyCombiningAlgId() {
        return policyCombiningAlgId;
    }

    public void setPolicyCombiningAlgId( String value ) {
        this.policyCombiningAlgId = value;
    }

    public BigInteger getMaxDelegationDepth() {
        return maxDelegationDepth;
    }

    public void setMaxDelegationDepth( BigInteger value ) {
        this.maxDelegationDepth = value;
    }

}

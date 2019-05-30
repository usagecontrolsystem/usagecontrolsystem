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
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType( XmlAccessType.FIELD )
@XmlType( name = "PolicyIdentifierListType", propOrder = {
    "policyIdReferenceOrPolicySetIdReference"
} )
public class PolicyIdentifierListType {

    @XmlElementRefs( {
        @XmlElementRef( name = "PolicySetIdReference", namespace = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17",
            type = JAXBElement.class, required = false ),
        @XmlElementRef( name = "PolicyIdReference", namespace = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17", type = JAXBElement.class,
            required = false )
    } )
    protected List<JAXBElement<IdReferenceType>> policyIdReferenceOrPolicySetIdReference;

    public List<JAXBElement<IdReferenceType>> getPolicyIdReferenceOrPolicySetIdReference() {
        if( policyIdReferenceOrPolicySetIdReference == null ) {
            policyIdReferenceOrPolicySetIdReference = new ArrayList<>();
        }
        return this.policyIdReferenceOrPolicySetIdReference;
    }

}

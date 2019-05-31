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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType( XmlAccessType.FIELD )
@XmlType( name = "PolicyIssuerType", propOrder = {
    "content",
    "attribute"
} )
public class PolicyIssuerType {

    @XmlElement( name = "Content" )
    protected ContentType content;
    @XmlElement( name = "Attribute" )
    protected List<AttributeType> attribute;

    public ContentType getContent() {
        return content;
    }

    public void setContent( ContentType value ) {
        this.content = value;
    }

    public List<AttributeType> getAttribute() {
        if( attribute == null ) {
            attribute = new ArrayList<>();
        }
        return this.attribute;
    }

}

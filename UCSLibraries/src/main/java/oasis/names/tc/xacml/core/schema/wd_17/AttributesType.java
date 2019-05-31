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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType( XmlAccessType.FIELD )
@XmlType( name = "AttributesType", propOrder = {
    "content",
    "attribute"
} )
public class AttributesType {

    @XmlElement( name = "Content" )
    protected ContentType content;
    @XmlElement( name = "Attribute" )
    protected List<AttributeType> attribute;
    @XmlAttribute( name = "Category", required = true )
    @XmlSchemaType( name = "anyURI" )
    protected String category;
    @XmlAttribute( name = "id", namespace = "http://www.w3.org/XML/1998/namespace" )
    @XmlJavaTypeAdapter( CollapsedStringAdapter.class )
    @XmlID
    @XmlSchemaType( name = "ID" )
    protected String id;

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

    public String getCategory() {
        return category;
    }

    public void setCategory( String value ) {
        this.category = value;
    }

    public String getId() {
        return id;
    }

    public void setId( String value ) {
        this.id = value;
    }

}

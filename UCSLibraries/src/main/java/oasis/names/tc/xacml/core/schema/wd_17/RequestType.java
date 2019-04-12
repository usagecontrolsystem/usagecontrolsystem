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
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB)
// Reference Implementation, v2.2.8-b130911.1802
// Vedere <a
// href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello
// schema di origine.
// Generato il: 2017.04.24 alle 12:34:54 PM CEST
//

package oasis.names.tc.xacml.core.schema.wd_17;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import it.cnr.iit.xacmlutilities.Category;

/**
 * <p>
 * Classe Java per RequestType complex type.
 *
 * <p>
 * Il seguente frammento di schema specifica il contenuto previsto contenuto in
 * questa classe.
 *
 * <pre>
 * &lt;complexType name="RequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}RequestDefaults" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}Attributes" maxOccurs="unbounded"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}MultiRequests" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ReturnPolicyIdList" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="CombinedDecision" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType( XmlAccessType.FIELD )
@XmlType( name = "RequestType",
    propOrder = { "requestDefaults", "attributes", "multiRequests" } )
public final class RequestType {

    @XmlElement( name = "RequestDefaults" )
    protected RequestDefaultsType requestDefaults;
    @XmlElement( name = "Attributes", required = true )
    protected List<AttributesType> attributes;
    @XmlElement( name = "MultiRequests" )
    protected MultiRequestsType multiRequests;
    @XmlAttribute( name = "ReturnPolicyIdList", required = true )
    protected boolean returnPolicyIdList;
    @XmlAttribute( name = "CombinedDecision", required = true )
    protected boolean combinedDecision;

    private static final Logger log = Logger.getLogger( RequestType.class.getName() );

    /**
     * Recupera il valore della proprietà requestDefaults.
     *
     * @return possible object is {@link RequestDefaultsType }
     *
     */
    public RequestDefaultsType getRequestDefaults() {
        return requestDefaults;
    }

    /**
     * Imposta il valore della proprietà requestDefaults.
     *
     * @param value
     *          allowed object is {@link RequestDefaultsType }
     *
     */
    public void setRequestDefaults( RequestDefaultsType value ) {
        this.requestDefaults = value;
    }

    /**
     * Gets the value of the attributes property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot.
     * Therefore any modification you make to the returned list will be present
     * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
     * for the attributes property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getAttributes().add(newItem);
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
     * Recupera il valore della proprietà multiRequests.
     *
     * @return possible object is {@link MultiRequestsType }
     *
     */
    public MultiRequestsType getMultiRequests() {
        return multiRequests;
    }

    /**
     * Imposta il valore della proprietà multiRequests.
     *
     * @param value
     *          allowed object is {@link MultiRequestsType }
     *
     */
    public void setMultiRequests( MultiRequestsType value ) {
        this.multiRequests = value;
    }

    /**
     * Recupera il valore della proprietà returnPolicyIdList.
     *
     */
    public boolean isReturnPolicyIdList() {
        return returnPolicyIdList;
    }

    /**
     * Imposta il valore della proprietà returnPolicyIdList.
     *
     */
    public void setReturnPolicyIdList( boolean value ) {
        this.returnPolicyIdList = value;
    }

    /**
     * Recupera il valore della proprietà combinedDecision.
     *
     */
    public boolean isCombinedDecision() {
        return combinedDecision;
    }

    /**
     * Imposta il valore della proprietà combinedDecision.
     *
     */
    public void setCombinedDecision( boolean value ) {
        this.combinedDecision = value;
    }

    /**
     * Retrieves the first attribute value stored inside the request attributes.
     * By assumption in our case in the original request we have just the
     * attributes related to the name of the subject who performs the action, the
     * name of the action he wants to perform and the name of the resource on
     * which he wants to perform the action. Any other schema is not allowed.
     *
     * @param requestAttributes
     *          the attributes of the request
     * @param subject
     *          the category in which we're interested into
     * @return the value of the attribute, null otherwise
     */
    public String extractValue( Category subject ) {
        try {
            for( AttributesType attributeType : attributes ) {
                if( attributeType.getCategory().equals( subject.toString() ) ) {
                    return attributeType.getAttribute().get( 0 ).getAttributeValue().get( 0 )
                        .getContent().get( 0 ).toString();
                }
            }
        } catch( Exception e ) {
            log.severe( e.getMessage() );
        }

        return null;
    }

    public String getAttribute( String category, String attributeId ) {
        String res = null;

        AttributesType attbs = attributes.stream()
            .filter( a -> a.getCategory().endsWith( category ) ).findFirst()
            .orElse( null );

        if( attbs != null ) {
            AttributeType attr = attbs.getAttribute().stream()
                .filter( a -> a.getAttributeId().endsWith( attributeId ) ).findFirst()
                .orElse( null );

            if( attr != null ) {
                res = attr.getAttributeValue().get( 0 ).getContent().get( 0 ).toString();
            }
        }

        return res;
    }

    public boolean addAttribute( String category, String dataType,
            String attributeId, String value ) {

        AttributeValueType attributeValueType = new AttributeValueType();
        attributeValueType.setDataType( dataType );
        attributeValueType.getContent().add( value );
        AttributeType attributeType = new AttributeType();
        attributeType.setAttributeId( attributeId );
        attributeType.setIncludeInResult( false );
        attributeType.setIssuer( "false" );
        attributeType.getAttributeValue().add( attributeValueType );
        AttributesType attributesType = new AttributesType();
        attributesType.setCategory( category );
        attributesType.getAttribute().add( attributeType );
        boolean added = false;
        for( AttributesType at : this.getAttributes() ) {
            if( at.getCategory().equals( category ) ) {
                at.getAttribute().add( attributeType );
                added = true;
            }
        }
        if( !added ) {
            this.getAttributes().add( attributesType );
        }

        return true;
    }

    public boolean addAttribute( String category, String dataType,
            String attributeId, List<String> value ) {
        AttributeValueType attributeValueType = new AttributeValueType();
        attributeValueType.setDataType( dataType );
        attributeValueType.getContent().addAll( value );
        AttributeType attributeType = new AttributeType();
        attributeType.setAttributeId( attributeId );
        attributeType.setIncludeInResult( false );
        attributeType.setIssuer( "false" );
        attributeType.getAttributeValue().add( attributeValueType );
        AttributesType attributesType = new AttributesType();
        attributesType.setCategory( category );
        attributesType.getAttribute().add( attributeType );
        boolean added = false;
        for( AttributesType at : this.getAttributes() ) {
            if( at.getCategory().equals( category ) ) {
                at.getAttribute().add( attributeType );
                added = true;
            }
        }
        if( !added ) {
            this.getAttributes().add( attributesType );
        }

        return true;
    }

}

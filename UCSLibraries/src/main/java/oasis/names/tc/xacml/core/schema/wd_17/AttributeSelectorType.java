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
 * <p>Classe Java per AttributeSelectorType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="AttributeSelectorType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}ExpressionType">
 *       &lt;attribute name="Category" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="ContextSelectorId" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="Path" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="DataType" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="MustBePresent" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttributeSelectorType")
public class AttributeSelectorType
    extends ExpressionType
{

    @XmlAttribute(name = "Category", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String category;
    @XmlAttribute(name = "ContextSelectorId")
    @XmlSchemaType(name = "anyURI")
    protected String contextSelectorId;
    @XmlAttribute(name = "Path", required = true)
    protected String path;
    @XmlAttribute(name = "DataType", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String dataType;
    @XmlAttribute(name = "MustBePresent", required = true)
    protected boolean mustBePresent;

    /**
     * Recupera il valore della proprietà category.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategory() {
        return category;
    }

    /**
     * Imposta il valore della proprietà category.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategory(String value) {
        this.category = value;
    }

    /**
     * Recupera il valore della proprietà contextSelectorId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContextSelectorId() {
        return contextSelectorId;
    }

    /**
     * Imposta il valore della proprietà contextSelectorId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContextSelectorId(String value) {
        this.contextSelectorId = value;
    }

    /**
     * Recupera il valore della proprietà path.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPath() {
        return path;
    }

    /**
     * Imposta il valore della proprietà path.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPath(String value) {
        this.path = value;
    }

    /**
     * Recupera il valore della proprietà dataType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Imposta il valore della proprietà dataType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataType(String value) {
        this.dataType = value;
    }

    /**
     * Recupera il valore della proprietà mustBePresent.
     * 
     */
    public boolean isMustBePresent() {
        return mustBePresent;
    }

    /**
     * Imposta il valore della proprietà mustBePresent.
     * 
     */
    public void setMustBePresent(boolean value) {
        this.mustBePresent = value;
    }

}

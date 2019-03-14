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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per StatusType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="StatusType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}StatusCode"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}StatusMessage" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}StatusDetail" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StatusType", propOrder = {
    "statusCode",
    "statusMessage",
    "statusDetail"
})
public class StatusType {

    @XmlElement(name = "StatusCode", required = true)
    protected StatusCodeType statusCode;
    @XmlElement(name = "StatusMessage")
    protected String statusMessage;
    @XmlElement(name = "StatusDetail")
    protected StatusDetailType statusDetail;

    /**
     * Recupera il valore della proprietà statusCode.
     * 
     * @return
     *     possible object is
     *     {@link StatusCodeType }
     *     
     */
    public StatusCodeType getStatusCode() {
        return statusCode;
    }

    /**
     * Imposta il valore della proprietà statusCode.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusCodeType }
     *     
     */
    public void setStatusCode(StatusCodeType value) {
        this.statusCode = value;
    }

    /**
     * Recupera il valore della proprietà statusMessage.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Imposta il valore della proprietà statusMessage.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusMessage(String value) {
        this.statusMessage = value;
    }

    /**
     * Recupera il valore della proprietà statusDetail.
     * 
     * @return
     *     possible object is
     *     {@link StatusDetailType }
     *     
     */
    public StatusDetailType getStatusDetail() {
        return statusDetail;
    }

    /**
     * Imposta il valore della proprietà statusDetail.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusDetailType }
     *     
     */
    public void setStatusDetail(StatusDetailType value) {
        this.statusDetail = value;
    }

}

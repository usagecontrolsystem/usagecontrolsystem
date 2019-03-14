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
package it.cnr.iit.xacmlutilities;

import org.json.JSONObject;

/**
 * This is the AttributeRetrieval class.
 * <p>
 * This class is used to pass from the PIPRetrieval to the remote context
 * handler the informations the CH needs to retrieve teh attribute in which the
 * PIPRetrieval is interested into. We have to pass from the PIP to the context
 * handler the attributeid, the category of the attribute, plus additional
 * informations if required from the attribute, e.g. an attribute related to the
 * subject may require the subject-id. The remote context handler has to put
 * sonly the value inside this class and the return it to the caller.
 * </p>
 * 
 * @author antonio
 *
 */
public class AttributeRetrieval {
    // the attrbitue id the PIPREtrieval is interested into
    String attributeId;
    // the category of the attribute
    Category category;
    // the datatype of the atttribute
    DataType dataType;
    // teh value of the attribute
    String value = "";
    // additional informations if required
    String additionalInformations = "";
    // ip of the host that is querying the attribute
    String host;

    public AttributeRetrieval() {

    }

    /**
     * Constructor for the AttributeRetrieval object with only the attribute id
     * and the additional information
     * 
     * @param attributeId
     *          the attribute id
     * @param additionalInformations
     *          the additional informations
     */
    public AttributeRetrieval( String attributeId, String additionalInformations ) {
        this.attributeId = attributeId;
        this.additionalInformations = additionalInformations;
    }

    /**
     * Constructor for teh AttributeRetrieval class
     * 
     * @param attributeId
     *          the attributeId
     * @param category
     *          the category
     * @param additionalInformations
     *          some addtional informations if required
     */
    public AttributeRetrieval( String attributeId, String category,
            String additionalInformations ) {
        this.attributeId = attributeId;
        this.category = Category.toCATEGORY( category );
        this.additionalInformations = additionalInformations;
    }

    /**
     * Constructor for the AttributeRetrieval class
     * 
     * @param attributeId
     *          the attributeId
     * @param category
     *          the category
     * @param additionalInformations
     *          some addtional informations if required
     * @param dataType
     *          the attribute data type
     */
    public AttributeRetrieval( String attributeId, String category,
            String additionalInformations, String dataType ) {
        this.attributeId = attributeId;
        this.category = Category.toCATEGORY( category );
        this.additionalInformations = additionalInformations;
        this.dataType = DataType.toDATATYPE( dataType );
    }

    public static AttributeRetrieval createAttributeRetrieval( String attributeId,
            String category, String dataType ) {
        AttributeRetrieval attributeRetrieval = new AttributeRetrieval();
        attributeRetrieval.attributeId = attributeId;
        attributeRetrieval.category = Category.toCATEGORY( category );
        attributeRetrieval.dataType = DataType.toDATATYPE( dataType );
        return attributeRetrieval;
    }

    /**
     * Retrieves the category
     * 
     * @return the category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Retrieves the additional informations
     * 
     * @return additional informations
     */
    public String getAdditionalInformations() {
        return additionalInformations;
    }

    /**
     * Retrieves the id of this attribute
     * 
     * @return the attribute id
     */
    public String getAttributeId() {
        return attributeId;
    }

    /**
     * Set the value of the attribute
     * 
     * @param value
     *          the value of the attribute
     */
    public void setValue( String value ) {
        this.value = value;
    }

    /**
     * Set the value of the categotry
     * 
     * @param category
     *          the category as string
     */
    public void setCategory( String category ) {
        this.category = Category.toCATEGORY( category );
    }

    // public void setAttributeId(String attributeId) {
    // this.attributeId = new AttributeId(attributeId);
    // }

    public void setAttributeId( String attributeId ) {
        this.attributeId = attributeId;
    }

    public String getHost() {
        return host;
    }

    public void setHost( String host ) {
        this.host = host;
    }

    public void setAdditionalInformations( String additionalInformations ) {
        this.additionalInformations = additionalInformations;
    }

    /**
     * 
     */
    public String getValue() {
        return value;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType( String dataType ) {
        this.dataType = DataType.toDATATYPE( dataType );
    }

    @Override
    public String toString() {
        return "ID: " + attributeId + "\nCategory: " + category + "\nDataType: "
                + dataType + "\nAI: "
                + ( ( additionalInformations == null )
                        || ( additionalInformations.equals( "" ) ) ? ""
                                : additionalInformations );
    }

    /**
     * Given the JSON structure of the item put inside the list from the PIP,
     * extract all the informations and put them inside an AttributeRetrieval
     * object. Doing so all the informations become more usable.
     * 
     * @param json
     *          the json inserted by the PIP
     * @return the AttributeRetrieval representation of the JSON
     */
    public static AttributeRetrieval extractFromJSON( JSONObject json ) {
        AttributeRetrieval attributeRetrieval = new AttributeRetrieval();
        JSONObject jsonObject = null;
        if( json.has( "object" ) ) {
            jsonObject = json.getJSONObject( "object" );
            attributeRetrieval.additionalInformations = jsonObject
                .getString( "additionalInfo" );
            attributeRetrieval.attributeId = jsonObject.getString( "objectId" );
        }
        if( json.has( "subject" ) ) {
            jsonObject = json.getJSONObject( "subject" );
            attributeRetrieval.additionalInformations = jsonObject
                .getString( "additionalInfo" );
            attributeRetrieval.attributeId = jsonObject.getString( "subjectId" );
        }
        if( json.has( "action" ) ) {
            jsonObject = json.getJSONObject( "action" );
            attributeRetrieval.additionalInformations = jsonObject
                .getString( "additionalInfo" );
            attributeRetrieval.attributeId = jsonObject.getString( "actionId" );
        }
        if( json.has( "environment" ) ) {
            jsonObject = json.getJSONObject( "environment" );
            attributeRetrieval.attributeId = jsonObject.getString( "environmentId" );
        }
        return attributeRetrieval;
    }
}

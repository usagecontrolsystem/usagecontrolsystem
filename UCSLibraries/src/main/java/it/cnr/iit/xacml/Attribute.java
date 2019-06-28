/*
 * CNR - IIT (2015-2016)
 *
 * @authors Fabio Bindi and Filippo Lauria and Antonio La Marra
 */
package it.cnr.iit.xacml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import it.cnr.iit.utility.errorhandling.Reject;

/**
 * Creates an Attribute object compliant with the XACML standard Embeds
 * AttributeDesignator and AttributeValue.
 *
 * @author Fabio Bindi, Filippo Lauria, Antonio La Marra, Alessandro Rosetti
 */
public final class Attribute implements Cloneable {
    private final Logger log = Logger.getLogger( Attribute.class.getName() );

    private String attributeId;

    private String issuer;

    private boolean includeInResult;

    private DataType dataType;

    private Category category;

    private final HashMap<String, List<String>> attributeValueMap;

    private String additionalInformations = "";

    public Attribute() {
        attributeValueMap = new HashMap<>();
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId( String attributeId ) {
        Reject.ifBlank( attributeId, "Can't set a blank attributeId" );
        this.attributeId = attributeId;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType( DataType dataType ) {
        Reject.ifNull( dataType, "Can't set a null dataType" );
        this.dataType = dataType;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer( String issuer ) {
        Reject.ifBlank( issuer, "Can't set a blank issuer" );
        this.issuer = issuer;
    }

    /**
     * Retrieves the value of the IncludeInResult element of the attribute
     *
     * @return Issuer element value (true or false)
     */
    public boolean isIncludeInResult() {
        return includeInResult;
    }

    /**
     * Creates the IncludeInResult element of the attribute
     *
     * @param includeInResult_
     *          Issuer element value (true or false)
     */
    public void setIncludeInResult( boolean includeInResult ) {
        this.includeInResult = includeInResult;
    }

    /**
     * Retrieves attribute values of a certain type
     *
     * @param dataType_
     *          a string representing the attribute type
     * @return a list of attribute values
     */
    public List<String> getAttributeValues( DataType dataType ) {
        Reject.ifNull( dataType );
        return attributeValueMap.get( dataType.toString() );
    }

    /**
     * Retrieves attribute values of a certain type
     *
     * @param dataType_
     *          a string representing the attribute type
     * @return a list of attribute values
     */
    public List<String> getAttributeValues( String dataType ) {
        checkValidDataType( dataType );
        return attributeValueMap.get( dataType );
    }

    /**
     * Retrieves the map of attributes of each type
     *
     * @return attribute values map
     */
    public Map<String, List<String>> getAttributeValueMap() {
        return attributeValueMap;
    }

    /**
     * Creates the values of an attribute given its type
     *
     * @param type
     *          a string representing the attribute type
     * @param value
     *          value to set
     */
    public void setAttributeValues( String type, String value ) {
        checkValidDataType( type );
        Reject.ifBlank( value );
        List<String> valueList = attributeValueMap.get( type );
        if( valueList == null ) {
            valueList = new LinkedList<>();
        }
        for( int i = 0; i < valueList.size(); i++ ) {
            if( valueList.get( i ).isEmpty() ) {
                valueList.set( i, value );
                attributeValueMap.put( type, valueList );
                return;
            }
        }
        valueList.add( value );
        attributeValueMap.put( type, valueList );
    }

    private void checkValidDataType( String type ) {
        Reject.ifBlank( type );
        DataType tmpDataType = DataType.toDATATYPE( type );
        Reject.ifNull( tmpDataType );
    }

    /**
     * Creates the values of an attribute given its type
     *
     * @param dataType_
     *          a DataType object representig the attribute type
     * @param value
     */
    public void setAttributeValues( DataType dataType, String value ) {
        List<String> valueList = attributeValueMap.get( dataType.toString() );
        if( valueList == null ) {
            valueList = new LinkedList<>();
        }
        for( int i = 0; i < valueList.size(); i++ ) {
            if( valueList.get( i ).isEmpty() ) {
                valueList.set( i, value );
                attributeValueMap.put( dataType.toString(), valueList );
                return;
            }
        }
        valueList.add( value );
        attributeValueMap.put( dataType.toString(), valueList );
    }

    /**
     * Retrieves the attribute with its elements
     *
     * @return a string representing the attribute compliant with the XACML
     *         standard
     */
    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append( "\nAttributeID=" + attributeId );
        resultBuilder.append( ",Issuer=" + issuer );
        resultBuilder.append( ",IncludeInResult=" + includeInResult + "\n" );

        for( Map.Entry<String, List<String>> entry : attributeValueMap.entrySet() ) {
            List<String> values = entry.getValue();
            resultBuilder.append( "DataType=" + entry.getKey() + ":" );

            if( values != null ) {
                resultBuilder.append( "Value=" + values.toString() );
            }
            resultBuilder.append( "\tAdditionalInfo: " + additionalInformations );
        }
        return resultBuilder.toString();
    }

    /**
     * Creating an Attribute node compliant with the XACML standard
     *
     * @param doc
     *          Document used to retrieve XACML elements to ser
     * @return a new Node containing the Attribute information
     */
    public Node createAttributeNode( Document doc ) {
        Element elem = doc.createElement( "Attribute" );
        if( issuer != null ) {
            elem.setAttribute( "Issuer", issuer );
        }
        elem.setAttribute( "IncludeInResult", String.valueOf( includeInResult ) );
        elem.setAttribute( "AttributeId", attributeId );
        for( Map.Entry<String, List<String>> entry : attributeValueMap.entrySet() ) {
            List<String> values = entry.getValue();
            if( values != null ) {
                for( String value : values ) {
                    Element attributeValue = doc.createElement( "AttributeValue" );
                    attributeValue.setAttribute( "DataType", entry.getKey() );
                    attributeValue.setAttribute( "Category", category.toString() );
                    attributeValue.setTextContent( value );
                    elem.appendChild( attributeValue );
                }
            }
        }
        return elem;
    }

    @Override
    public boolean equals( Object obj ) {
        if( this == obj ) {
            return true;
        } else if( obj == null || getClass() != obj.getClass() ) {
            return false;
        }
        final Attribute other = (Attribute) obj;
        return toString().equalsIgnoreCase( other.toString() );
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public Attribute clone() throws CloneNotSupportedException { // NOSONAR
        super.clone();
        Attribute clone = new Attribute();
        clone.setAttributeId( attributeId );
        for( Map.Entry<String, List<String>> entry : attributeValueMap.entrySet() ) {
            for( String s : entry.getValue() ) {
                clone.setAttributeValues( entry.getKey(), s );
            }
        }

        return clone;
    }

    public void setCategory( Category category ) {
        Reject.ifNull( category, "Can't set a null category" );
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public String getAdditionalInformations() {
        return additionalInformations;
    }

    public void setAdditionalInformations( String additionalInformations ) {
        this.additionalInformations = additionalInformations;
    }

    public void setValue( DataType dataType, String... values ) {
        Reject.ifNull( dataType );
        Reject.ifNullStringArray( values );

        ArrayList<String> list = new ArrayList<>( Arrays.asList( values ) );
        attributeValueMap.put( dataType.toString(), list );
    }

    public void setAttributevalueMap( Map<String, ArrayList<String>> map ) {
        for( Map.Entry<String, ArrayList<String>> entry : map.entrySet() ) {
            DataType datatype = DataType.toDATATYPE( entry.getKey() );
            if( datatype == null ) {
                log.severe( "ERROR in RETRIEVING DT" );
                return;
            }
            attributeValueMap.put( datatype.toString(), entry.getValue() );
        }
    }

}

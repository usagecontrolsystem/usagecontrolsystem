/*
 * CNR - IIT (2015-2016)
 * 
 * @authors Fabio Bindi and Filippo Lauria and Antonio La Marra
 */
package it.cnr.iit.xacmlutilities;

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

/**
 * Creates an Attribute object compliant with the XACML standard Embeds
 * AttributeDesignator and AttributeValue.
 * 
 * @author Fabio Bindi and Filippo Lauria and Antonio La Marra
 */
final public class Attribute implements Cloneable {
    private final Logger LOGGER = Logger.getLogger( Attribute.class.getName() );

    private String attributeId;

    private String issuer;

    private boolean includeInResult;

    private DataType dataType;

    private Category category;

    private final HashMap<String, List<String>> attributeValueMap;

    private String additionalInformations = "";

    /**
     * Constructor
     */
    public Attribute() {
        attributeValueMap = new HashMap<>();
    }

    /**
     * Retrieves the Attribute ID of the attribute
     * 
     * @return attribute ID
     */
    public String getAttributeId() {
        return attributeId;
    }

    /**
     * Creates the attribute ID for an attribute
     * 
     * @param attributeId_
     *          Attribute ID
     */
    public boolean createAttributeId( String attributeId ) {
        // BEGIN parameter checking
        if( attributeId == null || attributeId.isEmpty() ) {
            return false;
        }
        // END parameter checking
        this.attributeId = attributeId;
        return true;
    }

    /**
     * Retrieves the value of the Issuer element of the attribute
     * 
     * @return Issuer element value (true or false)
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Creates the Issuer element of the attribute
     * 
     * @param issuer_
     *          Issuer element value (true or false)
     */
    public void createIssuer( String issuer_ ) {
        issuer = issuer_;
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
    public void createIncludeInResult( boolean includeInResult_ ) {
        includeInResult = includeInResult_;
    }

    /**
     * Retrieves attibute values of a certain type
     * 
     * @param dataType_
     *          a string representing the attribute type
     * @return a list of attribute values
     */
    public List<String> getAttributeValues( DataType dataType_ ) {
        return attributeValueMap.get( dataType_.toString() );
    }

    /**
     * Retrieves attibute values of a certain type
     * 
     * @param dataType_
     *          a string representing the attribute type
     * @return a list of attribute values
     */
    public List<String> getAttributeValues( String dataType_ ) {
        return attributeValueMap.get( dataType_ );
    }

    /**
     * Retrieves the map of attributes of each type
     * 
     * @return attribute values map
     */
    public HashMap<String, List<String>> getAttributeValueMap() {
        return attributeValueMap;
    }

    /**
     * Creates the values of an attribute given its type
     * 
     * @param type
     *          a string representig the attribute type
     * @param value
     *          value to set
     */
    public void createAttributeValues( String type, String value ) {
        DataType data = DataType.toDATATYPE( type );
        if( data == null ) {
            LOGGER.severe( "DATATYPE is null" );
            return;
        }
        List<String> valueList = attributeValueMap.get( data );
        if( valueList == null ) {
            valueList = new LinkedList<>();
        }
        for( int i = 0; i < valueList.size(); i++ ) {
            if( valueList.get( i ).isEmpty() ) {
                valueList.set( i, value );
                attributeValueMap.put( data.toString(), valueList );
                return;
            }
        }
        valueList.add( value );
        attributeValueMap.put( data.toString(), valueList );
    }

    /**
     * Creates the values of an attribute given its type
     * 
     * @param dataType_
     *          a DataType object representig the attribute type
     * @param value
     */
    public void createAttributeValues( DataType dataType_, String value ) {
        // dataType = dataType_;
        List<String> valueList = attributeValueMap.get( dataType_ );
        if( valueList == null ) {
            valueList = new LinkedList<>();
        }
        for( int i = 0; i < valueList.size(); i++ ) {
            if( valueList.get( i ).isEmpty() ) {
                valueList.set( i, value );
                attributeValueMap.put( dataType_.toString(), valueList );
                return;
            }
        }
        valueList.add( value );
        attributeValueMap.put( dataType_.toString(), valueList );
    }

    /**
     * Retrieves the attribute type (i.e String, Integer, AnyURI, Date)
     * 
     * @return
     */
    public DataType getAttributeDataType() {
        return dataType;
    }

    public boolean setAttributeDataType( DataType dataType ) {
        if( dataType == null ) {
            return false;
        } else {
            this.dataType = dataType;
            return true;
        }
    }

    /**
     * Retrieves the attribute with its elements
     * 
     * @return a string representing the attribute compliant with the XACML
     *         standard
     */
    @Override
    public String toString() {
        String result = "\nAttributeID=" + attributeId;
        result += ",Issuer=" + issuer;
        result += ",IncludeInResult=" + String.valueOf( includeInResult ) + "\n";

        for( String dataType : attributeValueMap.keySet() ) {
            List<String> values = attributeValueMap.get( dataType );
            result += "DataType=" + dataType + ": ";

            if( values != null ) {
                for( int j = 0; j < values.size(); j++ ) {
                    result += "Value=" + values.get( j );
                    if( j != values.size() - 1 ) {
                        result += ",";
                    }

                }
            }
            result += "\tAdditionalInfo: " + additionalInformations;
        }
        return result;
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
        elem.setAttribute( "AttributeId", attributeId.toString() );
        for( String dataType : attributeValueMap.keySet() ) {
            List<String> values = attributeValueMap.get( dataType );
            if( values != null ) {
                for( String value : values ) {
                    Element attributeValue = doc.createElement( "AttributeValue" );
                    attributeValue.setAttribute( "DataType", dataType.toString() );
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
        }
        if( obj == null ) {
            return false;
        }
        if( getClass() != obj.getClass() ) {
            return false;
        }
        final Attribute other = (Attribute) obj;
        return toString().equalsIgnoreCase( other.toString() );
    }

    @Override
    public int hashCode() {
        String s = toString();
        int result = 0;
        for( int i = 0; i < s.length(); i++ ) {
            result += s.charAt( i );
        }
        return result;
    }

    @Override
    public Attribute clone() throws CloneNotSupportedException {
        Attribute clone = new Attribute();
        clone.createAttributeId( attributeId );
        for( Map.Entry<String, List<String>> entry : attributeValueMap.entrySet() ) {
            for( String s : entry.getValue() ) {
                clone.createAttributeValues( entry.getKey(), s );
            }
        }

        return clone;
    }

    public boolean setCategory( Category category ) {
        // BEGIN parameter checking
        if( category == null ) {
            return false;
        }
        // END parameter checking
        this.category = category;
        return true;
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
        // BEGIN parameter checking
        if( dataType == null || values == null ) {
            return;
        }
        // END parameter checking

        ArrayList<String> list = new ArrayList<>( Arrays.asList( values ) );
        attributeValueMap.put( dataType.toString(), list );
    }

    public void setAttributevalueMap( HashMap<String, ArrayList<String>> map ) {
        for( Map.Entry<String, ArrayList<String>> entry : map.entrySet() ) {
            DataType datatype = DataType.toDATATYPE( entry.getKey() );
            if( datatype == null ) {
                System.err.println( "ERROR in RETRIEVING DT" );
                return;
            }
            attributeValueMap.put( datatype.toString(), entry.getValue() );
        }
    }

    public HashMap<String, List<String>> getAttributeValuesMap() {
        return attributeValueMap;
    }

}

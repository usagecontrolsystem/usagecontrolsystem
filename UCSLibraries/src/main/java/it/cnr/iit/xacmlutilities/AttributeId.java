/*
 * CNR - IIT (2015-2016)
 *
 * @authors Fabio Bindi and Filippo Lauria
 */
package it.cnr.iit.xacmlutilities;

/**
 * Creates the Attribute ID of an attribute object
 *
 * @author Fabio Bindi and Filippo Lauria
 */
public final class AttributeId {

    private String attributeId = "";

    public AttributeId() {}

    public AttributeId( String attributeId ) {
        set( attributeId );
    }

    public void set( String attributeId ) {
        this.attributeId = attributeId;
    }

    @Override
    public String toString() {
        return attributeId;
    }

    @Override
    public boolean equals( Object o ) {
        if( o == this ) {
            return true;
        }
        if( !( o instanceof AttributeId ) ) {
            return false;
        }
        return attributeId.equalsIgnoreCase( ( (AttributeId) o ).toString() );
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    /**
     * Gets the name of the attribute from the whole attributeId
     *
     * @return the attribute name
     */
    public String getSplittedAttribute() {
        String[] tokens = attributeId.split( "#" );
        if( tokens.length > 1 ) {
            return tokens[1];
        }
        return attributeId;
    }

    @Override
    public AttributeId clone() throws CloneNotSupportedException {
        return new AttributeId( attributeId );
    }

}
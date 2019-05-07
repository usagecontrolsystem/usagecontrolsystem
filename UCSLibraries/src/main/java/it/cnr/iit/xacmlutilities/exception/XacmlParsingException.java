/*
 * CNR - IIT (2015-2016)
 * 
 * @authors Fabio Bindi and Filippo Lauria
 */
package it.cnr.iit.xacmlutilities.exception;

/**
 * This exception occurs when something goes wrong during the creation of an object
 * compliant to the XACML standard
 * @author Fabio Bindi and Filippo Lauria
 */
public class XacmlParsingException extends XacmlException {

    private static final long serialVersionUID = 9070763423584822711L;

    public XacmlParsingException( String s ) {
        super( ( s == null ) ? "XACML Parsing Exception" : s );
    }

}

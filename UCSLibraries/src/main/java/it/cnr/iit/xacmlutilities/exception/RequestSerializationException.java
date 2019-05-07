/*
 * CNR - IIT (2015-2016)
 * 
 * @authors Fabio Bindi and Filippo Lauria
 */
package it.cnr.iit.xacmlutilities.exception;

/**
 * This exception occurs when something goes wrong during XACML request serialization
 * @author Fabio Bindi and Filippo Lauria
 */
public class RequestSerializationException extends XacmlException {

    private static final long serialVersionUID = 6564857795935523938L;

    public RequestSerializationException( String s ) {
        super( ( s == null ) ? "Cannot serialize this XACML Request" : s );
    }

}

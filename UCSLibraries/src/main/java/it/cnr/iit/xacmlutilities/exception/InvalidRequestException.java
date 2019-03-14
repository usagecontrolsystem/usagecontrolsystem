/*
 * CNR - IIT (2015-2016)
 * @authors Fabio Bindi and Filippo Lauria
 */
package it.cnr.iit.xacmlutilities.exception;

/**
 * This exception occurs during the parsing of a XACML request if the node Request
 * is missing or one or more Request node attributes are missing
 * @author Fabio Bindi and Filippo Lauria
 */
public class InvalidRequestException extends XacmlException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 2136168191806418114L;

	public InvalidRequestException() {
        super("Invalid XACML Request");
    }
    
}

/*
 * CNR - IIT (2015-2016)
 * @authors Fabio Bindi and Filippo Lauria
 */
package it.cnr.iit.xacmlutilities.exception;

/**
 * A wrapper for exceptions related to the XACML operations (i.e. missing 
 * mandatory attributes in a XACML request, invalid XACML request, etc.)
 * @author Fabio Bindi and Filippo Lauria
 */
public class XacmlException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8724411296197260827L;

	public XacmlException(String s) {
        super(s);
    }

}

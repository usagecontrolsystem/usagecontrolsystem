/*
 * CNR - IIT (2015-2016)
 * @authors Fabio Bindi and Filippo Lauria
 */
package it.cnr.iit.xacmlutilities.exception;

/**
 * This exception occurs during the parsing of a XACML request if one or more
 * mandatory attributes ar missing (i.e. ReturnPolicyIdList or Category)
 * @author Fabio Bindi and Filippo Lauria
 */
public class MissingRequiredAttributeException extends XacmlException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 4467213839272976251L;

	public MissingRequiredAttributeException(String attributeName) {
        super("Required attribute " + attributeName + " is missing");
    }
    
    
}

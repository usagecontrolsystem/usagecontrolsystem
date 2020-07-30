package it.cnr.iit.ucs.contexthandler;

import it.cnr.iit.ucs.constants.STATUS;
import it.cnr.iit.utility.errorhandling.Reject;
import it.cnr.iit.xacml.Attribute;
import it.cnr.iit.xacml.Category;
import it.cnr.iit.xacml.DataType;
import it.cnr.iit.xacml.wrappers.RequestWrapper;

public class RequestStatusEnricher {

    private static final Category category = Category.UCS;
    private static final String attributeId = "urn:uxacml:decisionTime";

    public static RequestWrapper setAttributeForStatus(RequestWrapper requestWrapper, STATUS status) {
        Reject.ifNull(requestWrapper, status);
        Attribute attribute = buildDefaultAttribute();
        switch (status) {
            case TRY:
                requestWrapper.setAttribute(attribute, "pre");
                return requestWrapper;
            case REVOKE:
            case START:
                requestWrapper.setAttribute(attribute, "ongoing");
                return requestWrapper;
            case END:
            	requestWrapper.setAttribute(attribute, "post");
            	return requestWrapper;
        }
        throw new IllegalArgumentException("NO status found!!");
    }

    private static Attribute buildDefaultAttribute() {
        Attribute attribute = new Attribute();
        attribute.setCategory(category);
        attribute.setAttributeId(attributeId);
        attribute.setDataType(DataType.STRING);
        return attribute;
    }

}
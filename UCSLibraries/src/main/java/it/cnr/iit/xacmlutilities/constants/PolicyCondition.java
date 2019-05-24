package it.cnr.iit.xacmlutilities.constants;

import it.cnr.iit.ucs.constants.STATUS;

public class PolicyCondition {
    public static final String TRYACCESS = "pre";
    public static final String STARTACCESS = "ongoing";
    public static final String ENDACCESS = "post";

    private PolicyCondition() {}

    public static String fromStatus( STATUS status ) {
        switch( status ) {
            case TRYACCESS:
                return PolicyCondition.TRYACCESS;
            case STARTACCESS:
                return PolicyCondition.STARTACCESS;
            case ENDACCESS:
                return PolicyCondition.ENDACCESS;
            default:
                return "";
        }
    }
}

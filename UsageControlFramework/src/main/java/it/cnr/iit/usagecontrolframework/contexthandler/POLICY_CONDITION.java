package it.cnr.iit.usagecontrolframework.contexthandler;

import it.cnr.iit.ucs.constants.STATUS;

public class POLICY_CONDITION {
    public static final String TRYACCESS = "pre";
    public static final String STARTACCESS = "ongoing";
    public static final String ENDACCESS = "post";

    private POLICY_CONDITION() {}

    public static String fromStatus( STATUS status ) {
        switch( status ) {
            case TRYACCESS:
                return POLICY_CONDITION.TRYACCESS;
            case STARTACCESS:
                return POLICY_CONDITION.STARTACCESS;
            case ENDACCESS:
                return POLICY_CONDITION.ENDACCESS;
            default:
                return "";
        }
    }
}

package it.cnr.iit.ucs.constants;

public enum RestOperation {
    TRY_ACCESS( OperationNames.TRYACCESS_REST ),
    START_ACCESS( OperationNames.STARTACCESS_REST ),
    END_ACCESS( OperationNames.ENDACCESS_REST ),
    ON_GOING( OperationNames.ONGOING_REST ),
    ON_GOING_RESPONSE( OperationNames.ONGOINGRESPONSE_REST ),
    TRY_ACCESS_RESPONSE( OperationNames.TRYACCESSRESPONSE_REST ),
    START_ACCESS_RESPONSE( OperationNames.STARTACCESSRESPONSE_REST ),
    END_ACCESS_RESPONSE( OperationNames.ENDACCESSRESPONSE_REST ),
    START_EVALUATION( "/startEvaluation" ),
    FLOW_STATUS( "/flowStatus" ),
    FINISH( "/finish" ),
    IS_ALIVE( "/isAlive" ),
    SEND_SYNCHRONOUS( "/sendSynchronous" );

    private String operationUri;

    RestOperation( String operationUri ) {
        this.operationUri = operationUri;
    }

    public String getOperationUri() {
        return this.operationUri;
    }
}

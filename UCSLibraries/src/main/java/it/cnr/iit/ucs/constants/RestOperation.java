package it.cnr.iit.ucs.constants;

public enum RestOperation {
    TRY_ACCESS( OperationName.TRYACCESS_REST ),
    START_ACCESS( OperationName.STARTACCESS_REST ),
    END_ACCESS( OperationName.ENDACCESS_REST ),
    ON_GOING( OperationName.ONGOING_REST ),
    ON_GOING_RESPONSE( OperationName.ONGOINGRESPONSE_REST ),
    TRY_ACCESS_RESPONSE( OperationName.TRYACCESSRESPONSE_REST ),
    START_ACCESS_RESPONSE( OperationName.STARTACCESSRESPONSE_REST ),
    END_ACCESS_RESPONSE( OperationName.ENDACCESSRESPONSE_REST ),
    START( "/start" ),
    STATUS( "/status" ),
    FINISH( "/finish" );

    private String operationUri;

    RestOperation( String operationUri ) {
        this.operationUri = operationUri;
    }

    public String getOperationUri() {
        return this.operationUri;
    }
}

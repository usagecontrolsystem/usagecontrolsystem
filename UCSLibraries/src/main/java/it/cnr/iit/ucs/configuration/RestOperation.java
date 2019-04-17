package it.cnr.iit.ucs.configuration;

import static it.cnr.iit.ucsinterface.node.NodeInterface.ENDACCESSRESPONSE_REST;
import static it.cnr.iit.ucsinterface.node.NodeInterface.ENDACCESS_REST;
import static it.cnr.iit.ucsinterface.node.NodeInterface.ONGOINGRESPONSE_REST;
import static it.cnr.iit.ucsinterface.node.NodeInterface.STARTACCESSRESPONSE_REST;
import static it.cnr.iit.ucsinterface.node.NodeInterface.STARTACCESS_REST;
import static it.cnr.iit.ucsinterface.node.NodeInterface.TRYACCESSRESPONSE_REST;
import static it.cnr.iit.ucsinterface.node.NodeInterface.TRYACCESS_REST;

public enum RestOperation {

    TRY_ACCESS( TRYACCESS_REST ),
    START_ACCESS( STARTACCESS_REST ),
    END_ACCESS( ENDACCESS_REST ),
    ON_GOING_RESPONSE( ONGOINGRESPONSE_REST ),
    TRY_ACCESS_RESPONSE( TRYACCESSRESPONSE_REST ),
    START_ACCESS_RESPONSE( STARTACCESSRESPONSE_REST ),
    END_ACCESS_RESPONSE( ENDACCESSRESPONSE_REST ),
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

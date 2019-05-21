package it.cnr.iit.ucsinterface.message;

import it.cnr.iit.ucsinterface.pdp.PDPEvaluation;

public interface EvaluatedMessage {

    public PDPEvaluation getPDPEvaluation();

    public void setPDPEvaluation( PDPEvaluation evaluation );

}

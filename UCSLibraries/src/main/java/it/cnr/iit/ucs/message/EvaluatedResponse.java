package it.cnr.iit.ucs.message;

import it.cnr.iit.ucs.pdp.PDPEvaluation;

public interface EvaluatedResponse {

    public PDPEvaluation getPDPEvaluation();

    public void setPDPEvaluation( PDPEvaluation evaluation );

}

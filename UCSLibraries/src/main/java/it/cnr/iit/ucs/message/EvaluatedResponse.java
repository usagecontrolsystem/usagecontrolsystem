package it.cnr.iit.ucs.message;

import it.cnr.iit.ucs.pdp.PDPEvaluation;

public interface EvaluatedResponse {

    public PDPEvaluation getEvaluation();

    public void setEvaluation( PDPEvaluation evaluation );

}

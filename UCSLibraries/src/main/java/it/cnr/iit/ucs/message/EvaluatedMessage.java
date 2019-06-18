package it.cnr.iit.ucs.message;

import it.cnr.iit.ucs.pdp.PDPEvaluation;

/**
 * A message that has an evaluation
 *
 * @author Alessandro Rosetti
 */
public interface EvaluatedMessage {

    public PDPEvaluation getEvaluation();

    public void setEvaluation( PDPEvaluation evaluation );

}

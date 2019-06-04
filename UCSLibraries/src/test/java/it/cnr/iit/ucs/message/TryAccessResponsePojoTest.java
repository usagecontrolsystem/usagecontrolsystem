package it.cnr.iit.ucs.message;

import org.junit.Test;

import it.cnr.iit.ucs.AbstractPojoTest;
import it.cnr.iit.ucs.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucs.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucs.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucs.message.tryaccess.TryAccessResponse;

import pl.pojo.tester.api.FieldPredicate;

public class TryAccessResponsePojoTest extends AbstractPojoTest {

    @Test
    public void shouldPassAllTryAccessResponsePojoTests() {
        super.shouldPassAllPojoTests( TryAccessResponse.class, FieldPredicate.exclude() );
    }

    @Test
    public void shouldPassAllStartAccessMessagePojoTests() {
        super.shouldPassAllPojoTests( StartAccessResponse.class, FieldPredicate.exclude() );
    }

    @Test
    public void shouldPassAllEndAccessResponsePojoTests() {
        super.shouldPassAllPojoTests( EndAccessResponse.class, FieldPredicate.exclude() );
    }

    @Test
    public void shouldPassAllReevaluationResponsePojoTests() {
        super.shouldPassAllPojoTests( ReevaluationResponse.class, FieldPredicate.exclude() );
    }
}

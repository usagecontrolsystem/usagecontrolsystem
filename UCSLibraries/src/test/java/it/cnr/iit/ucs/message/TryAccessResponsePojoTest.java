package it.cnr.iit.ucs.message;

import org.junit.Test;

import it.cnr.iit.ucs.AbstractPojoTest;
import it.cnr.iit.ucs.message.endaccess.EndAccessResponseMessage;
import it.cnr.iit.ucs.message.reevaluation.ReevaluationResponseMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessResponseMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessResponseMessage;

import pl.pojo.tester.api.FieldPredicate;

public class TryAccessResponsePojoTest extends AbstractPojoTest {

    @Test
    public void shouldPassAllTryAccessResponsePojoTests() {
        super.shouldPassAllPojoTests( TryAccessResponseMessage.class, FieldPredicate.exclude() );
    }

    @Test
    public void shouldPassAllStartAccessMessagePojoTests() {
        super.shouldPassAllPojoTests( StartAccessResponseMessage.class, FieldPredicate.exclude() );
    }

    @Test
    public void shouldPassAllEndAccessResponsePojoTests() {
        super.shouldPassAllPojoTests( EndAccessResponseMessage.class, FieldPredicate.exclude() );
    }

    @Test
    public void shouldPassAllReevaluationResponsePojoTests() {
        super.shouldPassAllPojoTests( ReevaluationResponseMessage.class, FieldPredicate.exclude() );
    }
}

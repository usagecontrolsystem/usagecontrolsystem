package it.cnr.iit.ucs.message;

import org.junit.Test;

import it.cnr.iit.ucs.AbstractPojoTest;
import it.cnr.iit.ucs.message.attributechange.AttributeChangeMessage;
import it.cnr.iit.ucs.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucs.message.reevaluation.ReevaluationMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessMessage;

import pl.pojo.tester.api.FieldPredicate;

public class StartEndReevaluateAccessMessagesPojoTest extends AbstractPojoTest {

    @Test
    public void shouldPassAllStartAccessMessagePojoTests() {
        super.shouldPassAllPojoTests( StartAccessMessage.class, FieldPredicate.exclude() );
    }

    @Test
    public void shouldPassAllEndAccessMessagePojoTests() {
        super.shouldPassAllPojoTests( EndAccessMessage.class, FieldPredicate.exclude() );
    }

    @Test
    public void shouldPassAllReevaluationMessagePojoTests() {
        super.shouldPassAllPojoTests( ReevaluationMessage.class, FieldPredicate.exclude() );
    }

    @Test
    public void shouldPassAllAttributeChangeMessagePojoTests() {
        super.shouldPassAllPojoTests( AttributeChangeMessage.class, FieldPredicate.exclude() );
    }
}

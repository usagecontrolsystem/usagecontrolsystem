package it.cnr.iit.ucs.sessionmanager;

import org.junit.Test;

import it.cnr.iit.ucs.AbstractPojoTest;

import pl.pojo.tester.api.FieldPredicate;

public class SessionAttributesTest extends AbstractPojoTest {

    @Test
    public void shouldPassAllPojoTests() {
        super.shouldPassAllPojoTests( SessionAttributes.class, FieldPredicate.exclude() );
    }
}

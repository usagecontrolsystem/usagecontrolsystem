package it.cnr.iit.ucs.obligationmanager;

import org.junit.Test;

import it.cnr.iit.ucs.AbstractPojoTest;

import pl.pojo.tester.api.FieldPredicate;

public class ActionAttributesTest extends AbstractPojoTest {

    @Test
    public void shouldPassAllPojoTests() {
        super.shouldPassAllPojoTests( Action.class, FieldPredicate.exclude( "additionalProperties" ) );
    }
}

package it.cnr.iit.ucs.message.tryaccess;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

import java.util.function.Predicate;

import pl.pojo.tester.api.assertion.Method;

public class AbstractPojoTest {

    public void shouldPassAllPojoTests( Class<?> clazz, Predicate<String> predicate ) {
        assertPojoMethodsFor( clazz, predicate )
            .testing( Method.GETTER, Method.SETTER, Method.CONSTRUCTOR )
            .areWellImplemented();
    }
}

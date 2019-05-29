package it.cnr.iit;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsForAll;

import org.junit.Test;

import pl.pojo.tester.api.DefaultPackageFilter;
import pl.pojo.tester.api.assertion.Method;

public class AbstractPojoTest {

    @Test
    public void shouldPassAllPojoTests() {
        assertPojoMethodsForAll( DefaultPackageFilter.forPackage( "it.cnr.iit" ) ).testing( Method.GETTER, Method.SETTER );
    }
}

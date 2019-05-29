package it.cnr.iit;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsForAll;

import org.junit.Test;

import pl.pojo.tester.api.DefaultPackageFilter;
import pl.pojo.tester.api.assertion.Method;

public class AbstractPojoTest {

    @Test
    public void Should_Pass_All_Pojo_Tests() {
        assertPojoMethodsForAll( DefaultPackageFilter.forPackage( "it.cnr.iit" ) ).testing( Method.GETTER, Method.SETTER );
    }
}

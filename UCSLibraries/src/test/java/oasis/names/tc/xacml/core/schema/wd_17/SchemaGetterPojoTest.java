package oasis.names.tc.xacml.core.schema.wd_17;

import java.util.Set;

import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import it.cnr.iit.ucs.AbstractPojoTest;

import pl.pojo.tester.api.FieldPredicate;
import pl.pojo.tester.api.assertion.Method;

public class SchemaGetterPojoTest extends AbstractPojoTest {

    private static final String BASE_PACKAGE_OF_SCHEMAS = "oasis.names.tc.xacml.core.schema.wd_17";

    @Test
    public void shouldPassGetterPojoTests() {
        Reflections reflections = new Reflections( new ConfigurationBuilder()
            .setScanners( new SubTypesScanner( false /* don't exclude Object.class */ ), new ResourcesScanner() )
            .addUrls( ClasspathHelper.forJavaClassPath() )
            .filterInputsBy( new FilterBuilder()
                .include( FilterBuilder.prefix( BASE_PACKAGE_OF_SCHEMAS ) ) ) );

        Set<Class<?>> subTypesOf = reflections.getSubTypesOf( Object.class );
        for( Class<?> clazz : subTypesOf ) {
            if( clazz.getName().equals( BASE_PACKAGE_OF_SCHEMAS + ".ApplyType" ) ) {
                continue;
            }
            super.shouldPassSpecifiedMethodsPojoTests( clazz, FieldPredicate.exclude(), Method.GETTER );
        }
    }
}

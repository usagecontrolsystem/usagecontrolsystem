package it.cnr.iit.xacml;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import it.cnr.iit.ucs.AbstractPojoTest;
import it.cnr.iit.ucs.constants.STATUS;

import pl.pojo.tester.api.FieldPredicate;

public class XACMLpojoTest extends AbstractPojoTest {

    @Test
    public void classAttributeShouldPassAllPojoTests() {
        super.shouldPassAllPojoTests( Attribute.class, FieldPredicate.exclude( "log", "attributeValueMap" ) );
    }

    @Test
    public void classPolicyTagsShouldPassAllPojoTests() {
        assertEquals( PolicyTags.CONDITION_PRE, PolicyTags.getCondition( STATUS.TRY ) );

        assertEquals( PolicyTags.CONDITION_ONGOING, PolicyTags.getCondition( STATUS.REVOKE ) );
        assertEquals( PolicyTags.CONDITION_ONGOING, PolicyTags.getCondition( STATUS.START ) );

        assertEquals( PolicyTags.CONDITION_POST, PolicyTags.getCondition( STATUS.END ) );
    }

    @Test
    public void classDataTypeShouldPassAllPojoTests() {
        assertEquals( DataType.INTEGER, DataType.toDATATYPE( DataType.INTEGER.toString() ) );
        assertEquals( DataType.STRING, DataType.toDATATYPE( DataType.STRING.toString() ) );
        assertEquals( DataType.ANYURI, DataType.toDATATYPE( DataType.ANYURI.toString() ) );
        assertEquals( DataType.DATE, DataType.toDATATYPE( DataType.DATE.toString() ) );
        assertEquals( DataType.DOUBLE, DataType.toDATATYPE( DataType.DOUBLE.toString() ) );
    }

    @Test
    public void classCategoryShouldPassAllPojoTests() {
        assertEquals( Category.ACTION, Category.toCATEGORY( Category.ACTION.toString() ) );
        assertEquals( Category.ENVIRONMENT, Category.toCATEGORY( Category.ENVIRONMENT.toString() ) );
        assertEquals( Category.RESOURCE, Category.toCATEGORY( Category.RESOURCE.toString() ) );
        assertEquals( Category.SUBJECT, Category.toCATEGORY( Category.SUBJECT.toString() ) );
    }
}

package it.cnr.iit.ucs.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import it.cnr.iit.ucs.AbstractPojoTest;
import it.cnr.iit.ucs.message.tryaccess.TryAccessMessage;
import it.cnr.iit.utility.FileUtility;

import pl.pojo.tester.api.FieldPredicate;
import pl.pojo.tester.internal.GetOrSetValueException;

public class TryAccessMessagePojoTest extends AbstractPojoTest {

    @Test
    public void shouldPassAllPojoTests() {
        super.shouldPassAllPojoTests( TryAccessMessage.class, FieldPredicate.exclude( "policy", "request" ) );
    }

    @Test
    public void shouldPassSetGetPolicy() {
        try {
            // given a policy and a message object
            String policy = FileUtility.readFileAsString( "../res/xmls/policy_1.xml" );
            TryAccessMessage tryAccessMessage = new TryAccessMessage();

            // when the policy is set on message
            tryAccessMessage.setPolicy( policy );

            // then the policy is equal to the one in the message
            assertEquals( policy, tryAccessMessage.getPolicy() );
        } catch( Exception e ) {
            fail( "unable to load policy file " );
        }
    }

    @Test
    public void shouldPassSetGetRequest() {
        try {
            // given a request and a message object
            String request = FileUtility.readFileAsString( "../res/xmls/request.xml" );
            TryAccessMessage tryAccessMessage = new TryAccessMessage();

            // when the request is set on message
            tryAccessMessage.setRequest( request );

            // then the request is equal to the one in the message
            assertEquals( request, tryAccessMessage.getRequest() );
        } catch( Exception e ) {
            fail( "unable to load request file " );
        }
    }

    @Test( expected = GetOrSetValueException.class )
    public void shouldFailSetPolicy() {
        super.shouldPassAllPojoTests( TryAccessMessage.class, FieldPredicate.exclude( "request" ) );
        fail( "should have failed" );
    }

    @Test( expected = GetOrSetValueException.class )
    public void shouldFailSetRequest() {
        super.shouldPassAllPojoTests( TryAccessMessage.class, FieldPredicate.exclude( "policy" ) );
        fail( "should have failed" );
    }
}

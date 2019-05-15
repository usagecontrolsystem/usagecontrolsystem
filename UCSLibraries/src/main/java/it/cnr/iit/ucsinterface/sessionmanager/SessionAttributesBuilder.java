package it.cnr.iit.ucsinterface.sessionmanager;

import java.util.List;

public class SessionAttributesBuilder {

    private SessionAttributes sessionAttributes;

    public SessionAttributesBuilder() {
        sessionAttributes = new SessionAttributes();
    }

    public SessionAttributesBuilder setSessionId( String sessionId ) {
        sessionAttributes.setSessionId( sessionId );
        return this;
    }

    public SessionAttributesBuilder setPolicySet( String policySet ) {
        sessionAttributes.setPolicySet( policySet );
        return this;
    }

    public SessionAttributesBuilder setOriginalRequest( String originalRequest ) {
        sessionAttributes.setOriginalRequest( originalRequest );
        return this;
    }

    public SessionAttributesBuilder setOnGoingAttributesForSubject( List<String> onGoingAttributesForSubject ) {
        sessionAttributes.setOnGoingAttributesForSubject( onGoingAttributesForSubject );
        return this;
    }

    public SessionAttributesBuilder setOnGoingAttributesForResource( List<String> onGoingAttributesForResource ) {
        sessionAttributes.setOnGoingAttributesForResource( onGoingAttributesForResource );
        return this;
    }

    public SessionAttributesBuilder setOnGoingAttributesForAction( List<String> onGoingAttributesForAction ) {
        sessionAttributes.setOnGoingAttributesForAction( onGoingAttributesForAction );
        return this;
    }

    public SessionAttributesBuilder setOnGoingAttributesForEnvironment( List<String> onGoingAttributesForEnvironment ) {
        sessionAttributes.setOnGoingAttributesForEnvironment( onGoingAttributesForEnvironment );
        return this;
    }

    public SessionAttributesBuilder setStatus( String status ) {
        sessionAttributes.setStatus( status );
        return this;
    }

    public SessionAttributesBuilder setPepURI( String pepURI ) {
        sessionAttributes.setPepURI( pepURI );
        return this;
    }

    public SessionAttributesBuilder setMyIP( String myIP ) {
        sessionAttributes.setMyIP( myIP );
        return this;
    }

    public SessionAttributesBuilder setSubjectName( String subjectName ) {
        sessionAttributes.setSubjectName( subjectName );
        return this;
    }

    public SessionAttributesBuilder setResourceName( String resourceName ) {
        sessionAttributes.setResourceName( resourceName );
        return this;
    }

    public SessionAttributesBuilder setActionName( String actionName ) {
        sessionAttributes.setActionName( actionName );
        return this;
    }

    public SessionAttributes build() {
        return sessionAttributes;
    }

}

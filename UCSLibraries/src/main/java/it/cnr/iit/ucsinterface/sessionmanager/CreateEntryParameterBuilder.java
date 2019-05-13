package it.cnr.iit.ucsinterface.sessionmanager;

import java.util.List;

public class CreateEntryParameterBuilder {

    private CreateEntryParameter createEntryParameter;

    public CreateEntryParameterBuilder() {
        createEntryParameter = new CreateEntryParameter();
    }

    public CreateEntryParameterBuilder setSessionId( String sessionId ) {
        createEntryParameter.setSessionId( sessionId );
        return this;
    }

    public CreateEntryParameterBuilder setPolicySet( String policySet ) {
        createEntryParameter.setPolicySet( policySet );
        return this;
    }

    public CreateEntryParameterBuilder setOriginalRequest( String originalRequest ) {
        createEntryParameter.setOriginalRequest( originalRequest );
        return this;
    }

    public CreateEntryParameterBuilder setOnGoingAttributesForSubject( List<String> onGoingAttributesForSubject ) {
        createEntryParameter.setOnGoingAttributesForSubject( onGoingAttributesForSubject );
        return this;
    }

    public CreateEntryParameterBuilder setOnGoingAttributesForResource( List<String> onGoingAttributesForResource ) {
        createEntryParameter.setOnGoingAttributesForResource( onGoingAttributesForResource );
        return this;
    }

    public CreateEntryParameterBuilder setOnGoingAttributesForAction( List<String> onGoingAttributesForAction ) {
        createEntryParameter.setOnGoingAttributesForAction( onGoingAttributesForAction );
        return this;
    }

    public CreateEntryParameterBuilder setOnGoingAttributesForEnvironment( List<String> onGoingAttributesForEnvironment ) {
        createEntryParameter.setOnGoingAttributesForEnvironment( onGoingAttributesForEnvironment );
        return this;
    }

    public CreateEntryParameterBuilder setStatus( String status ) {
        createEntryParameter.setStatus( status );
        return this;
    }

    public CreateEntryParameterBuilder setPepURI( String pepURI ) {
        createEntryParameter.setPepURI( pepURI );
        return this;
    }

    public CreateEntryParameterBuilder setMyIP( String myIP ) {
        createEntryParameter.setMyIP( myIP );
        return this;
    }

    public CreateEntryParameterBuilder setSubjectName( String subjectName ) {
        createEntryParameter.setSubjectName( subjectName );
        return this;
    }

    public CreateEntryParameterBuilder setResourceName( String resourceName ) {
        createEntryParameter.setResourceName( resourceName );
        return this;
    }

    public CreateEntryParameterBuilder setActionName( String actionName ) {
        createEntryParameter.setActionName( actionName );
        return this;
    }

    public CreateEntryParameter build() {
        return createEntryParameter;
    }

}

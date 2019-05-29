package it.cnr.iit.ucs.sessionmanager;

import java.util.List;

public class SessionAttributes {
    private String sessionId;
    private String policySet;
    private String originalRequest;
    private List<String> onGoingAttributesForSubject;
    private List<String> onGoingAttributesForResource;
    private List<String> onGoingAttributesForAction;
    private List<String> onGoingAttributesForEnvironment;
    private String status;
    private String pepURI;
    private String myIP;
    private String subjectName;
    private String resourceName;
    private String actionName;

    public SessionAttributes() {}

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId( String sessionId ) {
        this.sessionId = sessionId;
    }

    public String getPolicySet() {
        return policySet;
    }

    public void setPolicySet( String policySet ) {
        this.policySet = policySet;
    }

    public String getOriginalRequest() {
        return originalRequest;
    }

    public void setOriginalRequest( String originalRequest ) {
        this.originalRequest = originalRequest;
    }

    public List<String> getOnGoingAttributesForSubject() {
        return onGoingAttributesForSubject;
    }

    public void setOnGoingAttributesForSubject( List<String> onGoingAttributesForSubject ) {
        this.onGoingAttributesForSubject = onGoingAttributesForSubject;
    }

    public List<String> getOnGoingAttributesForResource() {
        return onGoingAttributesForResource;
    }

    public void setOnGoingAttributesForResource( List<String> onGoingAttributesForResource ) {
        this.onGoingAttributesForResource = onGoingAttributesForResource;
    }

    public List<String> getOnGoingAttributesForAction() {
        return onGoingAttributesForAction;
    }

    public void setOnGoingAttributesForAction( List<String> onGoingAttributesForAction ) {
        this.onGoingAttributesForAction = onGoingAttributesForAction;
    }

    public List<String> getOnGoingAttributesForEnvironment() {
        return onGoingAttributesForEnvironment;
    }

    public void setOnGoingAttributesForEnvironment( List<String> onGoingAttributesForEnvironment ) {
        this.onGoingAttributesForEnvironment = onGoingAttributesForEnvironment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus( String status ) {
        this.status = status;
    }

    public String getPepURI() {
        return pepURI;
    }

    public void setPepURI( String pepURI ) {
        this.pepURI = pepURI;
    }

    public String getMyIP() {
        return myIP;
    }

    public void setMyIP( String myIP ) {
        this.myIP = myIP;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName( String subjectName ) {
        this.subjectName = subjectName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName( String resourceName ) {
        this.resourceName = resourceName;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName( String actionName ) {
        this.actionName = actionName;
    }

}
/*
 * CNR - IIT (2015-2016)
 *
 * @authors Fabio Bindi and Filippo Lauria
 */
package it.cnr.iit.sessionmanagerdesktop;

import java.util.UUID;

/**
 *
 * The class representing an on going attribute in the database.
 * <p>
 * We have kept the OrmLite annotations since this kind of object has to easily
 * represent an entry in a SQL database. <br>
 * Names of the fields have been changed a little. <br>
 * </p>
 *
 * @author Fabio Bindi and Filippo Lauria and Antonio La Marra
 */
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import it.cnr.iit.ucsinterface.sessionmanager.OnGoingAttributesInterface;
import it.cnr.iit.utility.errorhandling.Reject;

@DatabaseTable( tableName = "on_going_attributes" )
public class OnGoingAttribute implements OnGoingAttributesInterface {

    public enum COLUMN {
        SUBJECT, RESOURCE, ACTION, ENVIRONMENT;
    }

    public static final String ID_FIELD_NAME = "id";

    public static final String SESSION_ID_FIELD_NAME = "session_id";

    public static final String ATTRIBUTEID_FIELD = "attribute_id";
    public static final String SUBJECTNAME_FIELD = "subject_name";
    public static final String RESOURCENAME_FIELD = "resource_name";
    public static final String ACTIONNAME_FIELD = "action_name";

    @DatabaseField( id = true, columnName = ID_FIELD_NAME )
    private String id;

    @DatabaseField( canBeNull = false, columnName = ATTRIBUTEID_FIELD )
    private String attributeId;

    @DatabaseField( columnName = SUBJECTNAME_FIELD )
    private String subjectName;

    @DatabaseField( columnName = RESOURCENAME_FIELD )
    private String resourceName;

    @DatabaseField( columnName = ACTIONNAME_FIELD )
    private String actionName;

    @DatabaseField( foreign = true, foreignAutoRefresh = true, canBeNull = false,
        columnName = SESSION_ID_FIELD_NAME )
    private Session session;

    // @DatabaseField(columnName = SESSION_ID_FIELD_NAME)
    private String sessionId;

    /**
     * Empty constructor
     */
    public OnGoingAttribute() {
        attributeId = "";
        subjectName = resourceName = actionName = null;
    }

    /**
     * Constructor
     *
     * @param attributeId
     *          subject or object attribute name
     * @param subjectName
     *          subject ID (null if it's an object attribute)
     * @param resourceName
     *          object ID (null if it's a subject attribute)
     */
    private OnGoingAttribute( String attributeId, String subjectName, String resourceName,
            String actionName ) {
        setId( UUID.randomUUID().toString() );
        setAttributeId( attributeId );
        setSubjectName( subjectName );
        setResourceName( resourceName );
        setActionName( actionName );
    }

    public static OnGoingAttribute createOnGoingAttribute( String attributeId, String name, COLUMN column ) {
        Reject.ifBlank( attributeId );
        if( column != COLUMN.ENVIRONMENT ) {
            Reject.ifBlank( name );
        }
        OnGoingAttribute onGoingAttribute = new OnGoingAttribute();
        onGoingAttribute.setAttributeId( attributeId );
        onGoingAttribute.setId( UUID.randomUUID().toString() );
        switch( column ) {
            case SUBJECT:
                onGoingAttribute.setSubjectName( name );
                break;
            case ACTION:
                onGoingAttribute.setActionName( name );
                break;
            case RESOURCE:
                onGoingAttribute.setResourceName( name );
                break;
            default:
                break;
        }
        return onGoingAttribute;
    }

    public static OnGoingAttribute createOnGoingAttributeForAction( String attributeId, String actionName ) {
        Reject.ifBlank( attributeId );
        Reject.ifBlank( actionName );
        return new OnGoingAttribute( attributeId, null, null, actionName );
    }

    public static OnGoingAttribute createOnGoingAttributeForResource( String attributeId, String resourceName ) {
        Reject.ifBlank( attributeId );
        Reject.ifBlank( resourceName );
        return new OnGoingAttribute( attributeId, null, null, resourceName );
    }

    @Override
    public String getActionName() {
        return actionName;
    }

    public void setActionName( String actionName ) {
        this.actionName = actionName;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId( String sessionId ) {
        this.sessionId = sessionId;
    }

    /**
     * Gets the ID of the OnGoingAttribute raw in the database
     *
     * @return ID
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the OnGoingAttribute raw in the database
     *
     * @param id
     *          ID to be set
     */
    public void setId( String id ) {
        this.id = id;
    }

    /**
     * Gets the attribute name
     *
     * @return attribute name
     */
    @Override
    public String getAttributeId() {
        return attributeId;
    }

    /**
     * Sets the attribute name
     *
     * @param attributeId
     *          attribute name to be set
     */
    public void setAttributeId( String attributeId ) {
        this.attributeId = attributeId;
    }

    /**
     * Gets the subject ID
     *
     * @return subject ID
     */
    @Override
    public String getSubjectName() {
        return subjectName;
    }

    /**
     * Sets the subject ID
     *
     * @param subjectName
     *          subject ID to be set
     */
    public void setSubjectName( String subjectName ) {
        this.subjectName = subjectName;
    }

    /**
     * Gets the resource ID
     *
     * @return resource ID
     */
    @Override
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Sets the resource ID
     *
     * @param resourceName
     *          object ID to be set
     */
    public void setResourceName( String resourceName ) {
        this.resourceName = resourceName;
    }

    /**
     * Retrieves the Session of this attribute
     *
     * @return a Session object
     */
    public Session getSession() {
        return session;
    }

    /**
     * Sets the session for this attribute
     *
     * @param session
     *          attribute session to be set
     */
    public void setSession( Session session ) {
        this.session = session;
    }

    @Override
    public String toString() {
        String result = "( name = " + attributeId + ", ";

        if( subjectName != null && !subjectName.isEmpty() ) {
            result += "subject_id = " + subjectName + ", ";
        }

        if( resourceName != null && !resourceName.isEmpty() ) {
            result += "object_id = " + resourceName + ", ";
        }

        result += "id = " + id + " )";
        return result;
    }
}

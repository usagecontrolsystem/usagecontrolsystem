/*
 * CNR - IIT (2015-2016)
 *
 * @authors Fabio Bindi and Filippo Lauria
 */
package it.cnr.iit.sessionmanagerdesktop;

/**
 *
 * The class representing an on going attribute in the database.
 * <p>
 * We have kept the OrmLite annotations since this kind of object has to easily
 * represent an entry in a SQL database. <br>
 * Names of the fields have been changed a little. <br>
 * TODO: comment more
 * </p>
 *
 * @author Fabio Bindi and Filippo Lauria and Antonio La Marra
 */
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import it.cnr.iit.ucsinterface.sessionmanager.OnGoingAttributesInterface;

@DatabaseTable( tableName = "on_going_attributes" )
public class OnGoingAttribute implements OnGoingAttributesInterface {

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

    @Override
    public String getActionName() {
        return actionName;
    }

    public void setActionName( String actionId ) {
        this.actionName = actionId;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId( String sessionId ) {
        this.sessionId = sessionId;
    }

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
     * @param name_
     *          subject or object attribute name
     * @param subjectId_
     *          subject ID (null if it's an object attribute)
     * @param resourceName_
     *          object ID (null if it's a subject attribute)
     */
    public OnGoingAttribute( String name_, String subjectId_, String resourceName_,
            String actionName ) {
        this.attributeId = name_;
        this.subjectName = subjectId_;
        this.resourceName = resourceName_;
        this.actionName = actionName;
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
     * @param id_
     *          ID to be set
     */
    public void setId( String id_ ) {
        id = id_;
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
     * @param name_
     *          attribute name to be set
     */
    public void setAttributeId( String name_ ) {
        attributeId = name_;
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
     * @param subjectId_
     *          subject ID to be set
     */
    public void setSubjectName( String subjectId_ ) {
        subjectName = subjectId_;
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
     * @param resourceId_
     *          object ID to be set
     */
    public void setResourceName( String resourceId_ ) {
        resourceName = resourceId_;
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
     * @param session_
     *          attribute session to be set
     */
    public void setSession( Session session_ ) {
        session = session_;
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

/*
 * CNR - IIT (2015-2016)
 * 
 * @authors Fabio Bindi and Filippo Lauria
 */
package iit.cnr.it.ucsinterface.sessionmanager;

import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Creates Session objects. It configures OrmLite annotations to persist these
 * objects in the sessions table of the database.
 * 
 * @author Fabio Bindi, Filippo Lauria and Antonio La Marra
 */
@DatabaseTable(tableName = "sessions")
final public class Session implements SessionInterface {
	
	public static final String									ID_FIELD_NAME								= "id";
	public static final String									POLICYSET_FIELD_NAME				= "policy_set";
	public static final String									ORIGINALREQUEST_FIELD_NAME	= "original_request";
	public static final String									STATUS_FIELD_NAME						= "status";
	public static final String									PEPURI_FIELD_NAME						= "pep_uri";
	public static final String									MYIP_FIELD									= "myip";
	
	@DatabaseField(id = true, columnName = ID_FIELD_NAME)
	private String															id;
	
	@DatabaseField(columnName = POLICYSET_FIELD_NAME, columnDefinition = "TEXT")
	private String															policySet;
	
	@DatabaseField(columnName = ORIGINALREQUEST_FIELD_NAME,
	    columnDefinition = "TEXT")
	private String															originalRequest;
	
	@DatabaseField(columnName = STATUS_FIELD_NAME)
	private String															status;
	
	@DatabaseField(columnName = PEPURI_FIELD_NAME)
	private String															pepURI;
	
	@DatabaseField(columnName = MYIP_FIELD)
	private String															myIP;
	
	@ForeignCollectionField(eager = true,
	    columnName = OnGoingAttribute.ATTRIBUTEID_FIELD)
	private ForeignCollection<OnGoingAttribute>	onGoingAttributes;
	
	public Session() {
		
	}
	
	/**
	 * Constructor
	 * 
	 * @param id_
	 *          session ID
	 * @param policyOnGoing_
	 *          policy containing mutable attributes needed during the life-cycle
	 *          of the session
	 * @param policyPost_
	 *          policy containing mutable attributes needed during the end-session
	 *          operations
	 * @param originalRequest_
	 *          original XAML reqest containing informations about the
	 *          subject/object
	 * @param status_
	 *          status of the session
	 * @param pepURI_
	 *          URI of the PEP that started the session
	 */
	/*
	 * public Session(String id_, String policyOnGoing_, String policyPost_,
	 * String originalRequest_, String status_, String pepURI_) { id = id_;
	 * policyOnGoing = policyOnGoing_; policyPost = policyPost_; originalRequest =
	 * originalRequest_; status = status_; pepURI = pepURI_; }
	 */
	
	/**
	 * 
	 * @param id_
	 *          session ID
	 * @param policySet_
	 *          policy containing all the mutable attributes needed during the
	 *          session
	 * @param originalRequest_
	 *          original XAML reqest containing informations about the
	 *          subject/object
	 * @param status_
	 *          status of the session
	 * @param pepURI_
	 *          URI of the PEP that started the session
	 */
	public Session(String id_, String policySet_, String originalRequest_,
	    String status_, String pepURI_, String myIP) {
		id = id_;
		policySet = policySet_;
		originalRequest = originalRequest_;
		status = status_;
		pepURI = pepURI_;
		this.myIP = myIP;
	}
	
	/**
	 * Retrieves session ID
	 * 
	 * @return session ID
	 */
	@Override
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the session ID
	 * 
	 * @param id_
	 *          session ID to be set
	 */
	public void setId(String id_) {
		id = id_;
	}
	
	/**
	 * Retrieves the PolicySet
	 * 
	 * @return policySet
	 */
	@Override
	public String getPolicySet() {
		return policySet;
	}
	
	/**
	 * Sets the policySet
	 * 
	 * @param policySet_
	 *          policySet to set
	 */
	public void setPolicySet(String policySet_) {
		policySet = policySet_;
	}
	
	/**
	 * Retrieves the originalRequest
	 * 
	 * @return originalRequest
	 */
	@Override
	public String getOriginalRequest() {
		return originalRequest;
	}
	
	/**
	 * Sets the originalRequest
	 * 
	 * @param originalRequest_
	 *          originalRequest to be set
	 */
	public void setOriginalRequest(String originalRequest_) {
		originalRequest = originalRequest_;
	}
	
	/**
	 * Retrieves the status of the session
	 * 
	 * @return status
	 */
	@Override
	public String getStatus() {
		return status;
	}
	
	/**
	 * Sets the status of the session
	 * 
	 * @param status_
	 *          status to be set
	 */
	public void setStatus(String status_) {
		status = status_;
	}
	
	/**
	 * Sets the URI of the PEP
	 * 
	 * @param pepURI_
	 *          URI of the PEP to be set
	 */
	public void setPepURI(String pepURI_) {
		pepURI = pepURI_;
	}
	
	public void setMyIP(String ip) {
		this.myIP = ip;
	}
	
	/**
	 * Retrieves the list of mutable attributes of the session
	 * 
	 * @return list of mutable attributes
	 */
	@Override
	public List<OnGoingAttributesInterface> getOnGoingAttributes() {
		List<OnGoingAttributesInterface> list = new ArrayList<>();
		for (OnGoingAttribute onGoingAttribute : onGoingAttributes) {
			list.add(onGoingAttribute);
		}
		return list;
	}
	
	/**
	 * Retrieves the list of mutable attributes of the session
	 * 
	 * @return list of mutable attributes
	 */
	public List<OnGoingAttribute> getOnGoingAttribute() {
		List<OnGoingAttribute> list = new ArrayList<>();
		for (OnGoingAttribute onGoingAttribute : onGoingAttributes) {
			list.add(onGoingAttribute);
		}
		return list;
	}
	
	/**
	 * Retrieves the list of mutable attributes of the session
	 * 
	 * @return list of mutable attributes
	 */
	@Override
	public ForeignCollection<OnGoingAttribute> getOnGoingAttributesAsForeign() {
		return onGoingAttributes;
	}
	
	@Override
	public String toString() {
		String s = "SessionId = " + id + "\nPolicySet = " + policySet + "\n";
		s += "OriginalRequest = " + originalRequest + "\n";
		s += "Status = " + status + "\npepURI = " + pepURI + "\n";
		if (onGoingAttributes != null) {
			s += "OnGoingAttributes = ";
			try {
				for (OnGoingAttribute a : onGoingAttributes) {
					s += a.toString() + " ";
				}
			} catch (NullPointerException npe) {
				
			}
		}
		// System.err.println(s);
		return s;
	}
	
	@Override
	public String getPEPUri() {
		return pepURI;
	}
	
	@Override
	public String getIP() {
		return myIP;
	}
	
	@Override
	public void setRequest(String request) {
		this.originalRequest = request;
	}
}

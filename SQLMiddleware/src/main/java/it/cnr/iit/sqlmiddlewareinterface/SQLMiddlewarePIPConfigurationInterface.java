package it.cnr.iit.sqlmiddlewareinterface;

/**
 * THis is the interface exposed by the SQLMiddleware to the PIP
 * 
 * @author antonio
 *
 */
public interface SQLMiddlewarePIPConfigurationInterface {
	
	/**
	 * Retrieves the string representing the driver to be used
	 * 
	 * @return the driver to be used
	 */
	public String getDriver();
	
	/**
	 * Retrieves the username to be used to connect with the database
	 * 
	 * @return the usernamke to be used to connect with the database
	 */
	public String getUsername();
	
	/**
	 * Retrieves the password to be used to connect with the database
	 * 
	 * @return the password to be used to connect with the database
	 */
	public String getPassword();
	
	/**
	 * Retrieves the hibernate property named pool size
	 * 
	 * @return the pool size of connections to a certain db
	 */
	public String getConnections();
	
	/**
	 * Retrieves the url used by the database
	 * 
	 * @return the url used by the database
	 */
	public String getURL();
	
	/**
	 * Retrieve the class corresponding to a certain table
	 * 
	 * @return the class corresponding to a certain table
	 */
	public Class<?> getClassForTable();
	
	@Override
	public String toString();
	
}

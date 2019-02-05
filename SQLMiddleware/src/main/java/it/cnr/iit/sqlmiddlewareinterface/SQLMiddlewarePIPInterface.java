package it.cnr.iit.sqlmiddlewareinterface;

import java.util.List;

/**
 * This is the interface offered by the SQLMiddleware to the PIPSQL.
 * <p>
 * This interface is the one offered by the SQLMiddleware in order to let the
 * PIP query the database with a proper API.
 * </p>
 * 
 * @author antonio
 *
 */
public interface SQLMiddlewarePIPInterface {
	
	/**
	 * Performs a query that will return a single result
	 * 
	 * @param tableName
	 *          the name of the table
	 * @param condition
	 *          the condition string (with the where clause in it)
	 * @param returnedClass
	 *          the returned class
	 * @return the object the PIP was queying, null otherwise
	 */
	public <T> T performQuerySingleRecord(String tableName, String condition,
	    Class<T> returnedClass);
	
	/**
	 * Performs a query that will return a list result
	 * 
	 * @param tableName
	 *          the name of the table
	 * @param condition
	 *          the condition string
	 * @param returnedClass
	 *          the returned class
	 * @return the list of object satisfying the condition
	 */
	public <T> List<T> performQueryMultipleRecords(String tableName,
	    String condition, Class<T> returnedClass);
	
	/**
	 * Inserts the new object inside the table
	 * 
	 * @param tableName
	 *          the name of the table
	 * @param object
	 *          the object to be inserted
	 */
	public <T> void insert(String tableName, T object);
	
	@Override
	public String toString();
	
}

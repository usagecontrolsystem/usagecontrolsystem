/*******************************************************************************
 * Copyright 2018 IIT-CNR
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package iit.cnr.it.ucsinterface.contexthandler.pipregister;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.datastax.driver.core.utils.UUIDs;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import iit.cnr.it.apachecassandra.api.CassandraTable;
import iit.cnr.it.apachecassandra.api.structure.Field;
import iit.cnr.it.apachecassandra.api.structure.Table;
import it.cnr.iit.xacmlutilities.Category;

/**
 * This is the class that stores all the informations about the PIP that manages
 * the attribute we're interested in.
 * <p>
 * The only informations we need are the host (hence the ip) on which the PIP
 * is, the port on which the host is listening to, the attribute id of the pip
 * and its category
 * </p>
 * 
 * @author antonio
 *
 */
@DatabaseTable(tableName = "pipregister")
final public class PIPQueryTable extends Table implements CassandraTable {
	// the id fieldName
	public static final String		ID_FIELD_NAME						= "id";
	// the host field name
	public static final String		HOST_FIELD_NAME					= "host";
	// the port fiedl name
	public static final String		PORT_FIELD_NAME					= "port";
	// the attribute id field name
	public static final String		ATTRIBUTEID_FIELD_NAME	= "attributeId";
	// the category field name
	public static final String		CATEGORY_FIELD_NAME			= "category";
	
	// id field
	private static final Field		idField									= new Field(
	    ID_FIELD_NAME, "varchar", true);
	// name field
	private static final Field		attributeIdField				= new Field(
	    ATTRIBUTEID_FIELD_NAME, "varchar", false);
	// subject_id field
	private static final Field		categoryField						= new Field(
	    CATEGORY_FIELD_NAME, "varchar", false);
	// object_id field
	private static final Field		hostField								= new Field(
	    HOST_FIELD_NAME, "varchar", false);
	private static final Field		portField								= new Field(
	    PORT_FIELD_NAME, "varchar", false);
	// name of the table
	public static final String		tableName								= "pipregister";
	
	// the address of the host
	@DatabaseField(columnName = HOST_FIELD_NAME)
	private String								host;
	// the number of the port
	@DatabaseField(columnName = PORT_FIELD_NAME)
	private String								port;
	// the attribute id
	@DatabaseField(columnName = ATTRIBUTEID_FIELD_NAME)
	private String								attributeId;
	// the category
	@DatabaseField(columnName = CATEGORY_FIELD_NAME)
	private Category							category;
	// the id of the pip in the table
	@DatabaseField(id = true, columnName = ID_FIELD_NAME)
	private String								id;
	// states if this value has been initialized correctly
	boolean												initialized;
	
	private Logger								LOGGER									= Logger
	    .getLogger(PIPQueryTable.class.getName());
	
	private static PIPQueryTable	pipQuery;
	
	private PIPQueryTable() {
		super(tableName, fieldsAsList());
	}
	
	private static Field[] fieldsAsList() {
		Field[] list = new Field[5];
		list[0] = idField;
		list[1] = hostField;
		list[2] = portField;
		list[3] = attributeIdField;
		list[4] = categoryField;
		return list;
	}
	
	public static PIPQueryTable getInstance() {
		if (pipQuery == null) {
			pipQuery = new PIPQueryTable();
		}
		return pipQuery;
	}
	
	/**
	 * Constructor for an object to be stored inside the table of pipregister
	 * 
	 * @param host
	 *          the host on which we have the pip
	 * @param port
	 *          the port on which the host is listening to
	 * @param attribute
	 *          the attribute that is managed by this pip
	 * @param category
	 *          the category of the attribute
	 */
	public PIPQueryTable(String host, String port, String attribute,
	    Category category) {
		super(tableName, fieldsAsList());
		// BEGIN parameter checking
		if (host == null || host.isEmpty() || port == null || port.isEmpty()
		    || attribute == null || attribute.isEmpty() || category == null) {
			LOGGER.log(Level.SEVERE,
			    "MISSING or NULL parameter " + host + "\t" + port + "\t" + attribute);
			return;
		}
		// END parameter checking
		// id = UUIDs.timeBased() + "";
		this.host = host;
		this.port = port;
		this.attributeId = attribute;
		this.category = category;
		initialized = true;
	}
	
	// ---------------------------------------------------------------------------
	// CASSANDRA FUNCTIONS
	// ---------------------------------------------------------------------------
	@Override
	public String getTableName() {
		return tableName;
	}
	
	@Override
	public ArrayList<String> convertValues(Object... objects) {
		// BEGIN parameter checking
		if (objects == null || objects.length != 4) {
			LOGGER.log(Level.SEVERE, "Invalid objects passsed as parameter");
			return null;
		}
		// END parameter checking
		ArrayList<String> values = new ArrayList<>();
		values.add(convertToCommand(null, PIPQueryTableFields.ID));
		for (Object object : objects) {
			values.add(convertToCommand(object, PIPQueryTableFields.GENERIC));
		}
		return values;
	}
	
	@Override
	public <T> String convertToCommand(T value, Enum<?> enumerate) {
		// BEGIN parameter checking
		if (!enumerate.getClass().equals(PIPQueryTableFields.class)) {
			LOGGER.log(Level.SEVERE, "Invalid enumerate passed");
			return null;
		}
		// END parameter checking
		if (enumerate == PIPQueryTableFields.ID
		    && (value == null || value.equals("null")))
			return "'" + UUIDs.timeBased().toString() + "'";
		return (value == null || value.equals("null")) ? "'null'"
		    : "'" + value + "'";
	}
	
	@Override
	public String[] retrieveFields() {
		String[] fields = new String[5];
		fields[0] = idField.getName();
		fields[1] = attributeIdField.getName();
		fields[2] = categoryField.getName();
		fields[3] = hostField.getName();
		fields[4] = portField.getName();
		return fields;
	}
	
	@Override
	public String correspondence(Enum<?> enumerate) {
		// BEGIN parameter checking
		if (enumerate == null
		    || !enumerate.getClass().equals(PIPQueryTableFields.class)) {
			LOGGER.log(Level.SEVERE, "Invalid enumerate passed");
			return null;
		}
		// END parameter checking
		if (enumerate == PIPQueryTableFields.ID)
			return idField.getName();
		if (enumerate == PIPQueryTableFields.ATTRIBUTEID)
			return attributeIdField.getName();
		if (enumerate == PIPQueryTableFields.CATEGORY)
			return categoryField.getName();
		if (enumerate == PIPQueryTableFields.HOST)
			return hostField.getName();
		if (enumerate == PIPQueryTableFields.PORT)
			return portField.getName();
		return null;
	}
	
	@Override
	public Enum<?> correspondence(String string) {
		return null;
	}
	
	// ---------------------------------------------------------------------------
	// GETTERS and SETTERS
	// ---------------------------------------------------------------------------
	final public String getHost() {
		// BEGIN parameter checking
		if (!checkInitialized()) {
			return null;
		}
		// END parameter checking
		return host;
	}
	
	final public String getPort() {
		// BEGIN parameter checking
		if (!checkInitialized()) {
			return null;
		}
		// END parameter checking
		return port;
	}
	
	final public String getAttributeId() {
		// BEGIN parameter checking
		if (!checkInitialized()) {
			return null;
		}
		// END parameter checking
		return attributeId;
	}
	
	final public String getCategory() {
		// BEGIN parameter checking
		if (!checkInitialized()) {
			return null;
		}
		// END parameter checking
		return category.toString();
	}
	
	final private boolean checkInitialized() {
		if (!initialized) {
			LOGGER.log(Level.SEVERE, "Pojo not initialized properly");
			return false;
		}
		return true;
	}
	
}

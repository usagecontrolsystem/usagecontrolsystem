package it.cnr.iit.sqlmiddleware;

public enum HIBERNATE_CONFIGURATIONS {
	DRIVER_CLASS("hibernate.connection.driver_class"), URL(
	    "hibernate.connection.url"), USERNAME(
	        "hibernate.connection.username"), PASSWORD(
	            "hibernate.connection.password"), POOL_SIZE(
	                "hibernate.connection.pool_size"), DIALECT(
	                    "hibernate.dialect");
	
	private String configurationName;
	
	private HIBERNATE_CONFIGURATIONS(String string) {
		configurationName = string;
	}
	
	public String getString() {
		return configurationName;
	}
}

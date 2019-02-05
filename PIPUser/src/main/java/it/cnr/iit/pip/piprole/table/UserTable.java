package it.cnr.iit.pip.piprole.table;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class UserTable {
	@Id
	@Column(name = "id")
	private int			id;
	
	@Column(name = "username")
	private String	username;
	
	@Column(name = "role")
	private String	role;
	
	@Column(name = "company")
	private String	company;
	
	@Column(name = "location")
	private String	location;
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	public int getId() {
		return id;
	}
	
	public String getCompany() {
		return company;
	}
	
	public void setCompany(String company) {
		this.company = company;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
}

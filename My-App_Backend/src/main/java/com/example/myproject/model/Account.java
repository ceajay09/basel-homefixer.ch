package com.example.myproject.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Represents an account in the application, including personal and authentication details.
 */
@Document
public class Account {
	//TODO: Annotations (@Email, @NotNull, @Data, @NoArgsConstructor, @AllArgsConstructor, @NotBlank)

	@Id
	private String id;
	private String firstName;
	private String lastName;
	private String email;
	@com.fasterxml.jackson.annotation.JsonIgnore
	private String password;
	private String company;
	private String phoneNumber;
	private boolean admin;
	@com.fasterxml.jackson.annotation.JsonIgnore
	private String authRevision;
	public String getAuthRevision() { return authRevision; }
	public void setAuthRevision(String value) { authRevision = value; }

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

}

package database;

import java.sql.ResultSet;
import java.sql.Statement;

public class User {
	public String username;
	private String password;
	private String firstName;
	private String lastName;
	private String email;
	private boolean isVIP;
	private Connect connector;
	
	public User(String user, String pass, String first, String last, String emailAddress) {
		this.username = user;
		this.password = pass;
		this.firstName = first;
		this.lastName = last;
		this.email = emailAddress;
		this.isVIP = true;
	}
	
	//Overload method for non-vip implementation
	public User(String user, String pass, String first, String last) {
		this.username = user;
		this.password = pass;
		this.firstName = first;
		this.lastName = last;
		this.isVIP = true;
	}
	
}

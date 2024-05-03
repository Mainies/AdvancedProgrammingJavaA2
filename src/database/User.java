package database;

public abstract class User {
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private String email;
	protected boolean isVIP;
	
	public User(String user, String pass, String first, String last) {
		this.setUsername(user);
		this.setPassword(pass);
		this.setFirstName(first);
		this.setLastName(last);
	}
	
	public User(String user, String pass, String first, String last, String email) {
		this.setUsername(user);
		this.setPassword(pass);
		this.setFirstName(first);
		this.setLastName(last);
		this.setEmail(email);
	}
	
	public boolean isVIP() {
		return isVIP;
	}

	public String getPassword() {
		return password;
	}
	
	public void setUsername(String user) {
		this.username = user;
	}
	
	public String getUsername() {
		return this.username;
	}
	public void setPassword(String password) {
		this.password = password;
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
	
}
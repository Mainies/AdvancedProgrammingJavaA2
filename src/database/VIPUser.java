package database;

public class VIPUser extends User{
	public String username;
	private String password;
	private String firstName;
	private String lastName;
	private String email;
	private boolean isVIP;
	
	public VIPUser(String user, String pass, String first, String last, String email) {
		super(user, pass, first, last, email);
		this.isVIP = true;
	}

}

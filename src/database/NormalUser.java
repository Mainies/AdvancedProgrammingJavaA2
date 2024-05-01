package database;

public class NormalUser extends User{
	public String username;
	private String password;
	private String firstName;
	private String lastName;
	private String email;
	private boolean isVIP;
	
	public NormalUser(String user, String pass, String first, String last) {
		super(user, pass, first, last);
		this.isVIP = false;
	}
	
}
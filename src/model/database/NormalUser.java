package model.database;

public class NormalUser extends User{
	
	public NormalUser(String user, String pass, String first, String last) {
		super(user, pass, first, last);
		this.isVIP = false;
	}
	
}
package database;

public class VIPUser extends User{
	public String username;
	private String password;
	private String firstName;
	private String lastName;
	private String email;
	private boolean isVIP;
	private int loyaltyPoints;
	
	public VIPUser(String user, String pass, String first, String last, String email, int points) {
		super(user, pass, first, last, email);
		this.isVIP = true;
		this.loyaltyPoints = points;
	}

	public int getLoyaltyPoints() {
		return loyaltyPoints;
	}

	public void setLoyaltyPoints(int loyaltyPoints) {
		this.loyaltyPoints = loyaltyPoints;
	}
	
	

}

package database;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConnectTest {
	private Connect connector = new Connect();

	@Test
	public void checkConnectionDrivers() {
		connector.connect();
		if (connector != null) {
            connector.closeConnection();
			System.out.println("Connection closed.");
        }
	}
	
	@Test
	public void insertNewUser() {
		connector.connect();
		boolean didconnect = false;
		boolean diddelete = false;
		try {
		didconnect = connector.createUser("username", "password", "user", "lastname");
		} catch (Exception e) {
			fail("User not successfully created.");
		}
		assertEquals("user not added successfully", didconnect, true);
		try {
		diddelete = connector.deleteUser("username");
		} catch (Exception e) {
			fail("User not successfully deleted.");
		}
		assertEquals("user not deleted successfully", diddelete, true);
		connector.closeConnection();
	}
	
	@Test
	public void pickUpOrder() {
		fail("not yet implemented");
	}
	
	@Test
	public void addNewOrder() {
		fail("not yet implemented");
	}

}

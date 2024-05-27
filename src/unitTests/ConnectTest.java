package unitTests;

import static org.junit.Assert.*;

import java.sql.Connection;
import org.junit.Test;

import model.database.Connect;
import model.database.DBConnectTestMethods;

public class ConnectTest {
	private DBConnectTestMethods connector = new DBConnectTestMethods();

	@Test
	public void checkConnectionDrivers() {
		Connection connection = connector.make_connect();
		if (connection != null) {
            connector.closeConnection();
			System.out.println("Connection closed.");
        }
	}
	
	@Test
	public void insertNewUser() {
		Connect connect = new Connect();
		boolean didconnect = false;
		boolean diddelete = false;
		try {
		didconnect = connect.createUser("username", "password", "user", "lastname");
		} catch (Exception e) {
			fail("User not successfully created.");
		}
		assertEquals("user not added successfully", didconnect, true);
		try {
		diddelete = connect.deleteUser("username");
		} catch (Exception e) {
			fail("User not successfully deleted.");
		}
		assertEquals("user not deleted successfully", diddelete, true);
	}

}

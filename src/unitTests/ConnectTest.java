package unitTests;

import static org.junit.Assert.*;

import java.sql.Connection;
import org.junit.Test;
import model.database.ConnectMediator;
import model.database.DBConnectTestMethods;

public class ConnectTest {
	/*Class created for unit testing of the connection to the database.
	 * Tests connection url and connection method.
	 */
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
		ConnectMediator connect = new ConnectMediator();
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

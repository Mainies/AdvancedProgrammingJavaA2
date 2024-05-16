package unitTests;

import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import restaurant.*;

import org.junit.Test;

import database.Connect;

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
	/*
	 * Old Tests Prior to refactoring with more complex orders
	 * 
	@Test
	public void addNewOrder() {
		connector.connect();
		Order order = new Order(1, 1, 1, 1);
		PointOfService pos = new PointOfService();
		String user = "Mainies";
		try{
			connector.newOrder(order, pos, user);
			System.out.println("Orders added succesfully");
		} catch (Exception e) {
			fail("failed to connect and add order successfully");
		}
		int delete = connector.getOrderNumber();
		String removeTestOrder = "DELETE FROM Orders WHERE orderNumber = ?";
		String removeFromUserOrders = "DELETE FROM UserOrders WHERE orderNumber = ?";
		connector.deleteOrders(delete);
		connector.closeConnection();
	}
	
	@Test
	public void pickUpOrder() {
		connector.connect();
		Order order = new Order(1, 1, 1, 1);
		PointOfService pos = new PointOfService();
		String user = "Mainies";
		try{
			connector.newOrder(order, pos, user);
			System.out.println("Order added succesfully.");
		} catch (Exception e) {
			fail("failed to connect and add order successfully");
		}

		int delete = connector.getOrderNumber();
		try{
			connector.pickUpOrder(delete);
			System.out.println("Order picked up succesfully");
		} catch (Exception e){
			e.printStackTrace();
		}
		String removeTestOrder = "DELETE FROM Orders WHERE orderNumber = ?";
		String removeFromUserOrders = "DELETE FROM UserOrders WHERE orderNumber = ?";
		connector.deleteOrders(delete);
		connector.closeConnection();
	}
	*/
	


}

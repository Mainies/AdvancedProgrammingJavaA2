package unitTests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import controller.OrderPaneController;
import exceptions.InvalidNegativeNumber;
import exceptions.NotANumberException;
import exceptions.NotWholeNumber;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import model.database.Connect;
import model.database.NormalUser;
import model.database.User;
import model.database.VIPUser;
import model.restaurant.Order;
import model.restaurant.PointOfService;
import model.service.KitchenService;
import model.service.OrderService;
import model.service.POSService;
import model.service.UserService;

public class UnitTestsForImplementation {
	NormalUser user;
	VIPUser vipUser;
	UserService userService;
	POSService posService;
	KitchenService kitchenService;
	OrderService orderService;
	Connect connection;
	Order order;
	
	String burrito;
	
	@Before
	public void setUp() {
		user = new NormalUser("testUsername", "TestPassword", "TestFirstName", "TestLastName"); 
		vipUser = new VIPUser("testUsername", "TestPassword", "TestFirstName", "TestLastName", "email@test", 100);
		userService = UserService.getInstance();
		posService = POSService.getInstance();
		kitchenService = KitchenService.getInstance();
		orderService = OrderService.getInstance();
		Connect connection = new Connect();
	}

	@Test
	public void logOut() {
		userService.setObject(user);
		userService.clearObject();
		assertNull(userService.getObject());
	}

	@Test
	public void updateDetails() {
		connection.createUser("testUsername", "TestPassword", "TestFirstName", "TestLastName");
		User user = connection.getUserFromDatabase("testUsername");
		if (user instanceof VIPUser){
			fail("User should not be vip due to no email");//run a test to determine that details are updated correctly
		}
		connection.deleteUser("testUsername");
		connection.createVIPUser("testUsername", "TestPassword", "TestFirstName", "TestLastName", "email@test", 100);
		user = connection.getUserFromDatabase("testUsername");
		if (!(user instanceof VIPUser)) {
			fail("VIPUser should be returned as a VIP Object");
		}
		connection.deleteUser("testUsername");
	}
	
	@Test
	public void testGetWaitTime() {
		//make an order and test that it cooks correctly
		//test method (getWaitTime();
		fail("Not yet implemented");
		
	}
	
	@Test
	public void cardNumberValidator() {
		//test the card fields, should return booleans for values
		//isValidCardNumber
		//is valid expiry format
		//is valid expirty date
		// is valid csv
		fail("Not yet implemented");		
	}
	
	@Test
	public void checkPointsDiscount() {
		fail("Not yet implemented");
		//check that the discount is being applied correctly for vip and non-vip
	}
	
	@Test
	public void checkUserCreation() {
		//check that the constructors instantiate the correct type of user
		fail("Not yet implemented");
	}
	
	@Test
	public void testAppService() {
		//test the right user gets brought in/out or other class
		fail("not yet implemented");
	}
	
	@Test (expected = NotANumberException.class)
	public void validateNumberInput() throws Exception{
		burrito = "three";
		PointOfService pos = POSService.getInstance().getObject();
		pos.validateNumber(burrito);
	}
	
	@Test (expected = NotWholeNumber.class)
	public void validateWholeNumberInput() throws Exception{
		burrito = "1.2";
		PointOfService pos = POSService.getInstance().getObject();
		pos.wholeNumber(Double.parseDouble(burrito));
	}
	
	@Test (expected = InvalidNegativeNumber.class)
	public void InvalidNegativeNumber() throws Exception{
		burrito = "-1";
		PointOfService pos = POSService.getInstance().getObject();
		pos.negativeInput(Integer.parseInt(burrito));
	}

}

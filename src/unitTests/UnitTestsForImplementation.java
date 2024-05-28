package unitTests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import exceptions.InvalidNegativeNumber;
import exceptions.NotANumberException;
import exceptions.NotWholeNumber;
import model.database.ConnectMediator;
import model.database.DBConnectTestMethods;
import model.database.NormalUser;
import model.database.User;
import model.database.VIPUser;
import model.restaurant.BaseOrder;
import model.restaurant.Kitchen;
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
	DBConnectTestMethods connectionTester;
	ConnectMediator connection = new ConnectMediator();
	BaseOrder order;
	String burrito;
	
	@Before
	public void setUp() {
		user = new NormalUser("testUsername", "TestPassword", "TestFirstName", "TestLastName"); 
		vipUser = new VIPUser("testUsername", "TestPassword", "TestFirstName", "TestLastName", "email@test", 100);
		userService = UserService.getInstance();
		posService = POSService.getInstance();
		kitchenService = KitchenService.getInstance();
		orderService = OrderService.getInstance();
		DBConnectTestMethods connection = new DBConnectTestMethods();
	}

	@Test
	public void logOut() {
		userService.setObject(user);
		userService.clearObject();
		assertNull(userService.getObject());
	}

	@Test
	public void updateDetails() {
		//Issues pertaining to inability to use DB at the moment hmm
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
		System.out.println("made it here");
		Kitchen kitchen = kitchenService.getObject();
		if(kitchen == null) {
			fail("Failure to load kitchen");
		}
		BaseOrder order = new BaseOrder(3, 1, 0);
		//Burritos cook in 9 minutes and 2 can be cooked at a time. Expected 18
		//In the kitchen there should be 4 fries left over
		int waittime = kitchen.cookTime(order);
		assertEquals(waittime, 18);
		
		order = new BaseOrder(0, 4, 0);
		//expecting 0
		waittime = kitchen.cookTime(order);
		assertEquals(waittime, 0);
		//same order, expecting fries cook time which is 8
		waittime = kitchen.cookTime(order);
		assertEquals(waittime, 8);		
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

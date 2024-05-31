package unitTests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import controller.OrderPaneController;
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


public class UnitTestsForA2Model {
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
	public void testGetWaitTimeTest() {
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
	
	/*Card testing pretty simple due to validating strings
	 * no other checks for correct numbers and date-time conversions
	 */
	
	@Test
	public void cardNumberValidatorTest() {
		OrderPaneController pane = new OrderPaneController();
		//not enough numbers
		boolean TestCondition = pane.isValidCardNumber("1234");
		assertFalse(TestCondition);
		//Not digits
		TestCondition = pane.isValidCardNumber("thiswordhas16dig");
		assertFalse(TestCondition);
		//Correct
		TestCondition = pane.isValidCardNumber("1234123412341234");
		assertTrue(TestCondition);
	}
	
	@Test
	public void monthValidatorTest() {
		OrderPaneController pane = new OrderPaneController();
		//not right format
		boolean TestCondition = pane.isValidExpiryFormat("1234");
		assertFalse(TestCondition);
		//right format
		TestCondition = pane.isValidExpiryFormat("12/21");
		assertTrue(TestCondition);
		//but expired card
		TestCondition = pane.isValidExpiryDate("12/21");
		assertFalse(TestCondition);
		//Wrong format
		TestCondition = pane.isValidExpiryFormat("03/01/29");
		assertFalse(TestCondition);
		//Wrong Format
		TestCondition = pane.isValidExpiryFormat("06 Mar");
		assertFalse(TestCondition);
		//Correct and in the future
		TestCondition = pane.isValidExpiryFormat("12/99");
		assertTrue(TestCondition);
	}
	
	@Test
	public void csvValidatorTest() {
		OrderPaneController pane = new OrderPaneController();
		boolean TestCondition = pane.isValidCSV("abc");
		assertFalse(TestCondition);
		TestCondition = pane.isValidCSV("1234");
		assertFalse(TestCondition);
		TestCondition = pane.isValidCSV("123");
		assertTrue(TestCondition);
	}
	
	
	@Test
	public void checkPointsDiscount() {
		NormalUser user = new NormalUser("Test", "Test", "Test", "Test");
		VIPUser vipuser = new VIPUser("Test", "Test", "Test", "Test", "Test", 0);
		//ordermeal
		BaseOrder order = new BaseOrder(1, 1, 1, 1);
		PointOfService pos = POSService.getInstance().getObject();
		double price = pos.checkout(order, user.isVIP());
		assertEquals(price, 15, 0.0000001);
		price = pos.checkout(order, vipuser.isVIP());
	}
	
	@Test
	public void testAppService() {
		POSService service = POSService.getInstance();
		//AssertTheSame. 2 pointer should reference same object
		PointOfService pos1 = service.getObject();
		PointOfService pos2 = service.getObject();
		assertEquals(pos1, pos2);
		//AssertEmptiesObject
		service.clearObject();
		PointOfService pos3 = service.getObject();
		assertNull(pos3);
		//adding it back to use for other testing
		service.setObject(pos1);
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

package model.database;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javafx.collections.ObservableList;
import model.restaurant.*;


public class ConnectMediator implements IConnect{
	/*Mediator class that contains a child class for all DB connect classes
	 * Calls their methods where relevant and shares information.
	 * All retrieving and sending information to the Controller classes is handled by the connect class
	 */
	
	//Private DBConnectors
	private OrderInput orderInput;
	private UserInput userInput;
	private UserOutput userOutput;
	private OrderOutput orderOutput;
	private VIPUserDB vipDB;
	
	/* This class is intended to be a facade to make connection with program easier to understand*/
	public ConnectMediator(){
		orderInput = new OrderInput();
		userInput = new UserInput();
		userOutput = new UserOutput();
		orderOutput = new OrderOutput();
		vipDB = new VIPUserDB();
	}
	
	@Override
	public void updateFirstName(String newName, String currentUser) {
		// database connection to update first name for user. Performed as programming is running for stability
		userInput.updateDetails("FirstName", newName, currentUser); 
	}
	
	@Override
	public void updateLastName(String newName, String currentUser) {
		// database connection to update last name for user. Performed as programming is running for stability
		userInput.updateDetails("LastName", newName, currentUser); 
	}
	
	@Override
	public void updatePassword(String newPass, String currentUser) {
		// database connection to update password for user. Performed as programming is running for stability
		userInput.updateDetails("Password", newPass, currentUser);
	}
	
	@Override
	public void updateEmail(String email, String currentUser) {
		// database connection to update email for user. Performed as programming is running for stability. Called for when creating a VIP user
		userInput.updateDetails("Email", email, currentUser);
	}
	
	@Override
    public void checkIfPopulated(ArrayList<Integer> orderNumbersList) {
    	//Get list of VIP user orders
        ArrayList<Integer> vipOrders = fetchVipOrders();
        //Compare VIP orders to all orders
        ArrayList<Integer> numsToPopulate = getMissingVipOrders(orderNumbersList, vipOrders);
        //Populate VIP list with orders that are not in VIPUser Table
        insertMissingVipOrders(numsToPopulate);
    } 
    
	@Override
    public void updatePointsFull(VIPUser vipUser) {
		//Full method to implement a points update for the user
		// only method that doesn't update DB in place for the user points due to updating the user valid orders
		// vipUser only method
    	String username = vipUser.getUsername();
    	ArrayList<Integer> userOrderNumbers = getListOfUserOrderNumbers(username);
    	checkIfPopulated(userOrderNumbers);
    	updatePointsInDB(vipUser);
    }
    
	@Override
	public void processOrder(String userName, Order order) {
		//uses Orderinput to add order to DB
		// then adds the order to the list of user's orders
		orderInput.newOrder(userName, order);
		userInput.addUserOrder(userName, orderInput.getOrderNumber());
	}
		   
	
	@Override
	public boolean checkValidOrder(int orderNumber, String username) {
		return orderInput.checkValidOrder(orderNumber, username);
	}

	@Override
	public void deleteOrders(int OrderNumber) {
		orderInput.deleteOrders(OrderNumber);
		
	}

	@Override
	public void newOrder(String userName, Order order) {
		orderInput.newOrder(userName, order);
	}

	@Override
	public ArrayList<Integer> fetchVipOrders() {
		return orderOutput.fetchVipOrders();
	}

	@Override
	public boolean checkIfReadyForPickUp(int orderNumber) {
		return orderOutput.checkIfReadyForPickUp(orderNumber);
	}

	@Override
	public ArrayList<Integer> getMissingVipOrders(ArrayList<Integer> orderNumbersList, ArrayList<Integer> vipOrders) {
		return orderOutput.getMissingVipOrders(orderNumbersList, vipOrders);
	}

	@Override
	public ArrayList<Integer> getListOfUserOrderNumbers(String userName) {
		return orderOutput.getListOfUserOrderNumbers(userName);
	}

	@Override
	public ObservableList<Order> getActiveOrders(String username) {
		return orderOutput.getActiveOrders(username);
	}

	@Override
	public ObservableList<Order> getOrdersForExport(String username) {
		return orderOutput.getOrdersForExport(username);
	}

	@Override
	public void MaxValue() {
		orderInput.MaxValue();
	}

	@Override
	public void logoutPoints(String username, int points) {
		userInput.logoutPoints(username, points);
	}

	@Override
	public boolean createUser(String userName, String password, String firstName, String lastName) {
		return userInput.createUser(userName, password, firstName, lastName);
	}

	@Override
	public boolean createVIPUser(String userName, String password, String firstName, String lastName, String email,
			int points) {
		return userInput.createVIPUser(userName, password, firstName, lastName, email, points);
	}

	@Override
	public boolean deleteUser(String userName) {
		return userInput.deleteUser(userName);
	}

	@Override
	public void addUserOrder(String userName, int orderNumber) {
		userInput.addUserOrder(userName, orderNumber);		
	}

	@Override
	public void updateDetails(String condition, String newValue, String currentUser) {
		userInput.updateDetails(condition, newValue, currentUser);
		
	}

	@Override
	public void updatePointsInDB(VIPUser vipUser) {
		vipDB.updatePointsInDB(vipUser);		
	}

	@Override
	public void insertMissingVipOrders(ArrayList<Integer> numsToPopulate) {
		vipDB.insertMissingVipOrders(numsToPopulate);
		
	}

	@Override
	public void updatePoints(String currentUser) {
		vipDB.updatePoints(currentUser);
		
	}

	@Override
	public void collectPoints(int orderNumber) {
		vipDB.collectPoints(orderNumber);		
	}

	@Override
	public boolean isVIP(String userName) {
		return userOutput.isVIP(userName);
	}

	@Override
	public boolean checkPassword(String user, String inputPassword) {
		return userOutput.checkPassword(user, inputPassword);
	}

	@Override
	public boolean isUser(String user) {
		return userOutput.isUser(user);
	}

	@Override
	public User getUserFromDatabase(String username) {
		return userOutput.getUserFromDatabase(username);
	}
	
	@Override
	public void pickUpOrder(int orderNumber, String formattedDate) {
		orderInput.pickUpOrder(orderNumber, formattedDate);
		
	}

	@Override
	public void cancelOrder(int orderNumber, String formattedDate) {
		orderInput.cancelOrder(orderNumber, formattedDate);
		
	}

	@Override
	public void collectOrder(int orderNumber) {
		GetDate clock = new GetDate();
		String formattedDate = clock.getStringNow();
		pickUpOrder(orderNumber, formattedDate);
		
	}

	@Override
	public void makeCancelOrder(int orderNumber) {
		GetDate clock = new GetDate();
		String formattedDate = clock.getStringNow();
		cancelOrder(orderNumber, formattedDate);
		
	}
}

interface IConnect extends IUserInput, IUserOutput, IOrderInput, IOrderOutput, IVIPUser{
	/*Interface methods for the mediation of input data to child classes of DB Connect
	 * Implemented by Connect which is instantiated by all controllers
	 */
	void updateFirstName(String newName, String currentUser);
	
	void collectOrder(int orderNumber);
	
	void makeCancelOrder(int orderNumber);

	void updateLastName(String newName, String currentUser);
	
	void updatePassword(String newPass, String currentUser);
	
	void updateEmail(String email, String currentUser);
	
	void checkIfPopulated(ArrayList<Integer> orderNumbersList);
	
	void updatePointsFull(VIPUser vipUser);
	
	void processOrder(String userName, Order order);
	
}


//Done
interface DateTime{
	public String getStringNow();
}

class GetDate implements DateTime {
	public String getStringNow() {
    	//Method for returning the current date and time to put into the database
    	LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        String formattedDate = currentDateTime.format(formatter);
        return formattedDate;
    }
	
}


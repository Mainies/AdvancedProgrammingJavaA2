package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.database.User;
import model.database.VIPUser;
import model.restaurant.Kitchen;
import model.restaurant.Order;
import model.restaurant.PointOfService;
import model.service.*;
import exceptions.*;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class OrderPaneController extends AppController{
	//Order Panel to handle orders. Linked to ConfirmOrder.fxml and Orderer.fxml;
		
	//FXML labels for intaking user order values for Orderer.fxml
	@FXML private TextField burrito;
	@FXML private TextField fries;
	@FXML private TextField sodas;
	@FXML private TextField meals;
	@FXML private Label errorMessage;
	
	//Further labels for ConfirmOrder.fxml
	@FXML private Button make_order;
	@FXML private Label currentUser;
	@FXML private Label numBurritos;
	@FXML private Label numSodas;
	@FXML private Label numFries;
	@FXML private Label mealDeals;
	@FXML private Label totalPrice;
	@FXML private Label waitTime;
	@FXML private TextField usedPoints;
	@FXML private CheckBox usePointsButon;

    @FXML
    @Override
    public void initialize() {
    	//Initalise loading of FXML page, if order available then update wait times
        updateUserName();
        //Orderservice should only have object on ConfirmOrder.fxml
        if (orderService.getObject() != null) {
        	updateOrders();
        	updateWaitTime();
        }
        //Points should only work if on ConfirmOrder.fxml
        try{pointsVisibility();}
        catch(Exception e) {
        }
    }
    
    public void pointsVisibility() {
    	//Show points if points is a VIP user and option to use points for VIP user in ConfirmOrder.fxml
        User user = userService.getObject();
        if (!(user instanceof VIPUser)) {  
            usedPoints.setVisible(false); 
            usePointsButton.setVisible(false); 
        } else {
            usedPoints.setVisible(true); 
            usePointsButton.setVisible(true);
        }
    }
    
    private void updateWaitTime() {
    	//Usage to update for View
    	//Use kitchen instance to calculate waittime. Use java inbuilt ios to add that to wait time.
        int waitMinutes = getWaitTime(); 
        updateWaitLabel(waitMinutes);
    }

    private int getWaitTime(){
		Order order = orderService.getObject();
	    Kitchen kitchen = kitchenService.getObject();
	    int waitMinutes = kitchen.cookTime(order); 
	return waitMinutes;
    }

    private void updateWaitLabel(int waitMinutes){
	//get local time
        LocalTime now = LocalTime.now();
        LocalTime readyTime = now.plusMinutes(waitMinutes);      
        //Format to hh:mm
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = readyTime.format(timeFormatter);
        //set time and show expected pickup time with minutes till pick up time.
        waitTime.setText("Expected ready time: " + formattedTime + " (" + waitMinutes + " minutes.)");
    }
    
    private String getPickUpTime() {
    	//Similar to update wait time. Usage to return value for updaing 
        int waitMinutes = getWaitTime();
        LocalDateTime now = LocalDateTime.now(); 
        LocalDateTime readyTime = now.plusMinutes(waitMinutes);      
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"); 
        String formattedTime = readyTime.format(timeFormatter);
        return formattedTime;
    }
    
    private void updateUserName() {
    	//returns value to update username for view
        User user = userService.getObject(); 
        if (user != null && currentUser != null) {
            currentUser.setText(user.getUsername()); 
        } else {
            if (currentUser != null) {
                currentUser.setText("No user logged in");
            }
        }
    }
    
    private void updateOrders() {
    	//Updates the order for the ConfirmOrder view.
    	//Get order from service instance
    	Order order = orderService.getObject();
        if (order == null) {
            System.err.println("No order available to update.");
            return;
        }
        //get pos and user from service instances
        PointOfService pos = posService.getObject();
        User user = userService.getObject();
        boolean vipStatus = user.isVIP();
        //pos to return price
        order.setPrice(pos.calculateSale(order, vipStatus));
        updateOrderLabels(order, order.getPrice());
        //Checks for meals. Allows normal users to order meals but no discounts
        if (vipStatus && mealDeals != null) {
        	//reminds user of discounts if they are vip
            mealDeals.setText("Inclusive of " + order.getMeals() + " VIP meal deal discounts.");
        } else if (mealDeals != null) {
            mealDeals.setText(""); 
        }
    }

    private void updateOrderLabels(Order order, double price){
	updateLabel(numBurritos, Integer.toString(order.getBurritos()));
        updateLabel(numFries, Integer.toString(order.getFries()));
        updateLabel(numSodas, Integer.toString(order.getSodas()));
        updateLabel(totalPrice, "Total Price: $" + String.format("%.2f", price));
    }
    
    private void updateLabel(Label label, String text) {
    	//general method to handle labelling update for readibility within methods
        if (label != null) {
            label.setText(text);
        }
    }

    public void orderFood(ActionEvent event) throws Exception {
    	/*
    	 * Validates order input, constructs a new order and assigns it to the orderService instance for further handling in the confirm order class
    	 */
        try {
            validateOrderInput();
        } catch (Exception e) {
            errorMessage.setText("Error: " + e.getMessage());
            return;
        }
        //Initially just handle orders value quantities. Send to confirmorder view for further order manipulation
        int numBurritos = Integer.parseInt(burrito.getText());
        int numFries = Integer.parseInt(fries.getText());
        int numSodas = Integer.parseInt(sodas.getText());
        int numMeals = Integer.parseInt(meals.getText());
        numBurritos += numMeals;
        numFries += numMeals;
        numSodas += numMeals;
        Order order = new Order(numBurritos, numFries, numSodas, numMeals);
        //Set orderservice singleton to current order
        orderService.setObject(order); 
        //Change view
        changeToCheckout(event);
    }

	public void validateOrderInput() throws Exception{
		//exception handling for order class. Implements various exceptions to ensure order quantities are whole numbers >= 0
		PointOfService pos = POSService.getInstance().getObject();
		try{
			//Check actually a number
			pos.validateNumber(burrito.getText());
			pos.validateNumber(fries.getText());
			pos.validateNumber(sodas.getText());
			pos.validateNumber(meals.getText());
			}
		catch (NotANumberException e) {
				throw e;
			}
		try {
			//check for integer not double or float
			pos.wholeNumber(Double.parseDouble(burrito.getText()));
	        pos.wholeNumber(Double.parseDouble(fries.getText()));
	        pos.wholeNumber(Double.parseDouble(meals.getText()));	
		} 
		catch(NotWholeNumber e) {
			throw e;
		}	
		try {
			//check that number is not negative
			pos.negativeInput(Integer.parseInt(burrito.getText()));
			pos.negativeInput(Integer.parseInt(fries.getText()));
			pos.negativeInput(Integer.parseInt(sodas.getText()));
			pos.negativeInput(Integer.parseInt(meals.getText()));
		}
		catch(InvalidNegativeNumber e) {
			throw e;
		}
	}
   
    public void changeToCheckout(ActionEvent event) {
    	SceneChanger.changeScene(event, "ConfirmOrder.fxml");
    }
    
    public void backToOrder(ActionEvent event) {
    	//allows user to make amendments to order by clearing order and creating a new one prior to checking out
    	
    	//clear singleton instance
    	orderService.clearObject();
    	SceneChanger.changeScene(event, "Orderer.fxml");
    }
	    
    //Further validation for confirm order
        
    //Credit card fields
	@FXML private TextField csv;
	@FXML private TextField expiry;
	@FXML private TextField cardNumber;
	@FXML private Label cardError;
	
	
	//check that the lenth matches 16 digits
	public boolean isValidCardNumber(String cardNumber) {
	    return cardNumber.matches("\\d{16}");
	}
	
	//check that the input field is in MM/YY
	public boolean isValidExpiryFormat(String expiry) {
	    if (!expiry.matches("\\d{2}/\\d{2}")) {
	        return false; 
	    }
	    String[] parts = expiry.split("/");
	    //only month needs to be checked. Other method checks for future
	    int month = Integer.parseInt(parts[0]);
	    // Check if the month is valid (1 to 12)
	    if (month < 1 || month > 12) {
	        return false;
	    }
	    //if all conditions met true is return
		return true;
	}
	
	public boolean isValidExpiryDate(String expiry) {
		//put string into a date and ensure it is later than now and not expired
	    try {
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
	        YearMonth expiryDate = YearMonth.parse(expiry, formatter);
	        YearMonth currentMonth = YearMonth.now();
	        return expiryDate.isAfter(currentMonth);
	    } catch (DateTimeParseException e) {
	        return false; 
	    }
	}
	
	public boolean isValidCSV(String csv) {
		//check csv is 3 digits
	    return csv.matches("\\d{3}");
	}
	
	public boolean validatePaymentInfo() {
		/*Validates card number, expiry date and csv using previous methods
		 * changes error messages to update to reflect errors
		 */
	    String card = cardNumber.getText();
	    String exp = expiry.getText();
	    String csvnumber = csv.getText();

	    if (!isValidCardNumber(card)) {
	    	//card number error message
	        cardError.setText("Invalid card number. Must be 16 digits.");
	        return false;
	    } else if (!isValidExpiryFormat(exp)) {
	    	//checks for a valid format for numbers
	        cardError.setText("Invalid expiry date. Must be in the format mm/yy");
	        return false;
	    } else if (!isValidExpiryDate(exp)) {
	    	//if valid format, check that it is not expired
	        cardError.setText("Expiry date must be in the future.");
	        return false;
	    } else if (!isValidCSV(csvnumber)) {
	    	//csv checker
	    	cardError.setText("CSV must be 3 numbers");
	    	return false;
	    } else {
	    	//if all valid then the program can continue
	    	return true;}
	}
	
	public void checkOut(ActionEvent e) {
		/* format that takes multiple elements to ensure a order is valid, payment details pass and updates order with the pick up time*/
		
		//valid payment info
		if (!validatePaymentInfo()) {//validate payment stops this code block if false
		} else {
		//get order and user
		Order thisOrder = orderService.getObject();
		
		/*
		 * Can this next part be broken down into a separate method
		 */
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
		String date = currentDateTime.format(formatter);
				
		//set order placement date
		thisOrder.setDateCreated(date);
		String pickupTime = getPickUpTime();
		//set pick up time based on cooking time
		thisOrder.setDatePickedUp(pickupTime);
	
		//send this order to newOrder method to be added to database
		sendOrderToDB(thisOrder);
		orderService.clearObject();
		//clear the order object so further objects can be made
		SceneChanger.popUp(e);
		backToLanding(e);
		}
	}
	
	
    public void sendOrderToDB(Order order) {
    	//get values from order
    	double price = order.getPrice();
    	double discount = pointsDiscount();
    	price = price - discount;
    	order.setPrice(price);
    	String userName = userService.getObject().getUsername();
    	connection.processOrder(userName, order);
    }
    
    public void backToLanding(ActionEvent event) {
    	SceneChanger.changeScene(event, "LandingPage.fxml");
    }
    
    @FXML private CheckBox usePointsButton;
    
    public double pointsDiscount() {
    	//Method to handle user points per assignment specification
    	//User has the option to select the points button
    	if(usePointsButton == null) {
    		return 0.00;
    	}
        if (usePointsButton.isSelected()) {
        	//if selected the user can specify how many points they want to use
            try {
            	VIPUser user = (VIPUser) userService.getObject();
                int points = Integer.parseInt(usedPoints.getText());
                //check that the points are less than the total points a user has. If too many, use all user points
                if (points > user.getLoyaltyPoints()) {
                	points = user.getLoyaltyPoints();
                }
                //update loyalty points
                user.setLoyaltyPoints(user.getLoyaltyPoints() - points);
                  
                //discount for actual price. Prevents infinite points
                double discount = Math.round((points / 100.0) * 100.0) / 100.0; 
                return discount;
            } catch (NumberFormatException e) {
            	
                System.err.println("Invalid number format in points field.");
                return 0.00;
            }
        } else {
            return 0.00;
        }
    }
}


	


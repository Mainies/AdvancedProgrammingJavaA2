package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import exceptions.*;
import database.Connect;
import database.User;
import database.VIPUser;
import restaurant.Kitchen;
import restaurant.Order;
import restaurant.PointOfService;
import service.*; 

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalTime;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class OrderPaneController {
	
	
	@FXML private TextField burrito;
	@FXML private TextField fries;
	@FXML private TextField sodas;
	@FXML private TextField meals;
	@FXML private Label errorMessage;
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

    private UserService userService = UserService.getInstance();
    private POSService posService = POSService.getInstance();
    private KitchenService kitchenService = KitchenService.getInstance();
    private OrderService orderService = OrderService.getInstance();

    @FXML
    public void initialize() {
        updateUserName();
        if (orderService.getObject() != null) {
        	updateOrders();
        	updateWaitTime();
        }
        try{pointsVisibility();}
        catch(Exception e) {
        }
    }
    
    public void pointsVisibility() {
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
        Order order = orderService.getObject();
        Kitchen kitchen = kitchenService.getObject();
        int waitMinutes = kitchen.cookTime(order); 
        LocalTime now = LocalTime.now();
        LocalTime readyTime = now.plusMinutes(waitMinutes);      
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = readyTime.format(timeFormatter);
        waitTime.setText("Expected ready time: " + formattedTime + " (" + waitMinutes + " minutes.)");
    }
    
    private String getPickUpTime() {
    	Order order = orderService.getObject();
        Kitchen kitchen = kitchenService.getObject();
        int waitMinutes = kitchen.cookTime(order); 
        LocalDateTime now = LocalDateTime.now(); 
        LocalDateTime readyTime = now.plusMinutes(waitMinutes);      
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"); 
        String formattedTime = readyTime.format(timeFormatter);
        return formattedTime;
    }
    
    private void updateUserName() {
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
        Order order = orderService.getObject();
        if (order == null) {
            System.err.println("No order available to update.");
            return;
        }
        PointOfService pos = posService.getObject();
        User user = userService.getObject();
        boolean vipStatus = user.isVIP();
        double price = pos.calculateSale(order, vipStatus);
        System.out.println(price);
        updateLabel(numBurritos, Integer.toString(order.getBurritos()));
        updateLabel(numFries, Integer.toString(order.getFries()));
        updateLabel(numSodas, Integer.toString(order.getSodas()));
        updateLabel(totalPrice, "Total Price: $" + String.format("%.2f", price));
        if (vipStatus && mealDeals != null) {
            mealDeals.setText("Inclusive of " + order.getMeals() + " VIP meal deal discounts.");
        } else if (mealDeals != null) {
            mealDeals.setText(""); 
        }
    }
    
    private void updateLabel(Label label, String text) {
        if (label != null) {
            label.setText(text);
        }
    }

    public void orderFood(ActionEvent event) throws Exception {
        try {
            validateOrderInput();
        } catch (Exception e) {
            errorMessage.setText("Error: " + e.getMessage());
            return;
        }
        int numBurritos = Integer.parseInt(burrito.getText());
        int numFries = Integer.parseInt(fries.getText());
        int numSodas = Integer.parseInt(sodas.getText());
        int numMeals = Integer.parseInt(meals.getText());
        numBurritos += numMeals;
        numFries += numMeals;
        numSodas += numMeals;
        Order order = new Order(numBurritos, numFries, numSodas, numMeals);
        orderService.setObject(order); 
        this.changeToCheckout(event);
    }

	@SuppressWarnings("static-access")
	public void validateOrderInput() throws Exception{
		PointOfService pos = new PointOfService();
		try{
			pos.validateNumber(burrito.getText());
			pos.validateNumber(fries.getText());
			pos.validateNumber(sodas.getText());
			pos.validateNumber(meals.getText());
			}
		catch (NotANumberException e) {
				throw e;
			}
		try {
			pos.wholeNumber(Double.parseDouble(burrito.getText()));
	        pos.wholeNumber(Double.parseDouble(fries.getText()));
	        pos.wholeNumber(Double.parseDouble(meals.getText()));	
		} 
		catch(NotWholeNumber e) {
			throw e;
		}	
		try {
			pos.negativeInput(Integer.parseInt(burrito.getText()));
			pos.negativeInput(Integer.parseInt(fries.getText()));
			pos.negativeInput(Integer.parseInt(sodas.getText()));
			pos.negativeInput(Integer.parseInt(meals.getText()));
		}
		catch(InvalidNegativeNumber e) {
			throw e;
		}
	}

    @FXML
    public void initializeListeners() {
        burrito.textProperty().addListener((observable) -> validateInput());
        fries.textProperty().addListener((observable) -> validateInput());
        sodas.textProperty().addListener((observable) -> validateInput());
        meals.textProperty().addListener((observable) -> validateInput());
    }

    private void validateInput() {
        try {
            validateOrderInput();
        } catch (Exception e) {
            errorMessage.setText("Input Error: " + e.getMessage());
        }
    }
    
    public void changeToCheckout(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ConfirmOrder.fxml")); 
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
    
    public void backToOrder(ActionEvent event) {
    	orderService.clearObject();
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Orderer.fxml")); 
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
	

	@FXML private TextField csv;
	@FXML private TextField expiry;
	@FXML private TextField cardNumber;
	@FXML private Label cardError;
	
	public boolean isValidCardNumber(String cardNumber) {
	    return cardNumber.matches("\\d{16}");
	}
	
	public boolean isValidExpiryFormat(String expiry) {
	    if (!expiry.matches("\\d{2}/\\d{2}")) {
	        return false; 
	    }
		return true;
	}
	
	public boolean isValidExpiryDate(String expiry) {
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
	    return csv.matches("\\d{3}");
	}
	
	public boolean validatePaymentInfo() {
	    String card = cardNumber.getText();
	    String exp = expiry.getText();
	    String csvnumber = csv.getText();

	    if (!isValidCardNumber(card)) {
	        cardError.setText("Invalid card number. Must be 16 digits.");
	        return false;
	    } else if (!isValidExpiryFormat(exp)) {
	        cardError.setText("Invalid expiry date. Must be in the format mm/yy");
	        return false;
	    } else if (!isValidExpiryDate(exp)) {
	        cardError.setText("Expiry date must be in the future.");
	        return false;
	    } else if (!isValidCSV(csvnumber)) {
	    	cardError.setText("CSV must be 3 numbers");
	    	return false;
	    } else {
	    	return true;}
	}
	
	public void checkOut(ActionEvent e) {
		if (!validatePaymentInfo()) {
		} else {
		Order thisOrder = orderService.getObject();
		User thisuser = userService.getObject();
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
		String date = currentDateTime.format(formatter);
		thisOrder.setDateCreated(date);
		String pickupTime = getPickUpTime();
		thisOrder.setDatePickedUp(pickupTime);
		boolean vip = thisuser.isVIP();
		double price = posService.getObject().calculateSale(thisOrder, vip);
		thisOrder.setPrice(price);
		newOrder(thisOrder);
		orderService.clearObject();
		backToLanding(e);
		}
	}
	
    public void newOrder(Order order) {
    	Connect connector = new Connect();
    	int burritos = order.getBurritos();
    	int fries = order.getFries();
    	int sodas = order.getSodas(); 
    	int meals = order.getMeals();
    	double price = order.getPrice();
    	double discount = pointsDiscount();
    	price = price - discount;
    	connector.MaxValue();
    	int orderNumber = connector.getOrderNumber();
    	orderNumber++;
    	order.setOrderNum(orderNumber);
    	String pickuptime = getPickUpTime();
    	order.setDatePickedUp(pickuptime);
    	String query = "INSERT INTO Orders(dateCreated, OrderNumber, Burritos, Fries, Sodas, Meals, Status, Price, dateCollected) VALUES(?, ?, ?, ?, ?, ?, ?, ?,?)";
    	try (Connection conn = connector.make_connect();
    		PreparedStatement pstmt = conn.prepareStatement(query)) {
    		pstmt.setString(1, order.getDateCreated());
            pstmt.setInt(2, orderNumber);
            pstmt.setInt(3, burritos);
            pstmt.setInt(4, fries);
            pstmt.setInt(5, sodas);
            pstmt.setInt(6, meals);
            String collection = "await for collection";
            pstmt.setString(7, collection);
            pstmt.setDouble(8, price);
            pstmt.setString(9, order.getDatePickedUp());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Order added successfully.");
            } else {
                System.out.println("No rows affected.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } 
    	String userName = userService.getObject().getUsername();
    	addUserOrder(userName, orderNumber);
    }
    
    public void addUserOrder(String userName, int orderNumber) {
    	Connect connector = new Connect();
    	String query = "INSERT INTO UserOrders(Username, OrderNumber) VALUES (?, ?)";
    	try (Connection connect = connector.make_connect();
    		PreparedStatement pstmt = connect.prepareStatement(query)) {
            pstmt.setString(1, userName);
            pstmt.setInt(2, orderNumber);
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("Order successfully added to UserOrders.");
            } else {
                System.out.println("No rows affected.");
            }
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    public void backToLanding(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LandingPage.fxml")); 
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
    
    @FXML private CheckBox usePointsButton;
    
    public double pointsDiscount() {
    	if(usePointsButton == null) {
    		return 0.00;
    	}
        if (usePointsButton.isSelected()) {
            try {
            	VIPUser user = (VIPUser) userService.getObject();
                int points = Integer.parseInt(usedPoints.getText());
                if (points > user.getLoyaltyPoints()) {
                	points = user.getLoyaltyPoints();
                }
                user.setLoyaltyPoints(user.getLoyaltyPoints() - points);
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


	


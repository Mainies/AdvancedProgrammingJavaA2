package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import exceptions.*;
import database.Connect;
import database.User;
import restaurant.Kitchen;
import restaurant.Order;
import restaurant.PointOfService;
import service.ApplicationService; 

public class OrderPaneController {
	
	@FXML
	private TextField burrito;
	
	@FXML
	private TextField fries;
	
	@FXML
	private TextField sodas;
	
	@FXML
	private TextField meals;
	
	@FXML
	private Label errorMessage;
	
	@FXML
	private Button make_order;
	
	@FXML
	private Label currentUser;
	
	@FXML
	private Label numBurritos;
	
	@FXML
	private Label numSodas;
	
	@FXML
	private Label numFries;
	
	@FXML
	private Label mealDeals;
	
	@FXML
	private Label totalPrice;

    private ApplicationService appService = ApplicationService.getInstance(); 

    @FXML
    public void initialize() {
        updateUserName();
        if (appService.getOrder() != null) {
        	updateOrders();
        };
    }
     

    private void updateUserName() {
        User user = appService.getUser(); 
        if (user != null && currentUser != null) {
            currentUser.setText(user.username); 
        } else {
            if (currentUser != null) {
                currentUser.setText("No user logged in");
            }
        }
    }
    
    private void updateOrders() {
        Order order = appService.getOrder();
        PointOfService pos = appService.getPos();
        if (order != null) {
            double price = pos.calculateSale(order);
            if (numBurritos != null) {
                numBurritos.setText(Integer.toString(order.getBurritos()));
            } 
            if (numFries != null) numFries.setText(Integer.toString(order.getFries()));
            if (numSodas != null) numSodas.setText(Integer.toString(order.getSodas()));
            if (mealDeals != null) mealDeals.setText("Inclusive of " + Integer.toString(order.getMeals()) + " meal deals.");
            if (totalPrice != null) totalPrice.setText("Total Price: $" + Double.toString(price));
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
        order.printOrder();
        appService.setOrder(order); 
        this.changeToCheckout(event);
    }

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

	
	public void buildAndConfirmOrder(ActionEvent event) throws Exception {
		Kitchen kitchen = appService.getKitchen();
		Order order = appService.getOrder();
		PointOfService pos = appService.getPos();
		String userName = appService.getUser().username;
		int cookingTime = kitchen.cookTime(order);
		/* need to implement this so it sends the food to the kitchen, returns the cooking time and adds a new order to the db and a 
		 * new order to userOrders
		 */
		//pos.
		
	}
	
	
}


	


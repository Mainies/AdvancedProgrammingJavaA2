package application;

import javafx.event.ActionEvent;
import restaurant.*;
import restaurant.*;
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
	
	public Order orderFood(ActionEvent event) throws Exception {
		try{
			validateOrderInput();
		} catch (Exception e) {
			errorMessage.setText("Error: " + e.getMessage());
			return null; //had to add this so an order wasn't created in the event of a bad input.
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
		return order;
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
	        pos.wholeNumber(Double.parseDouble(sodas.getText()));
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
        burrito.textProperty().addListener((observable) -> {});
        fries.textProperty().addListener((observable) -> {});
        sodas.textProperty().addListener((observable) -> {});
        meals.textProperty().addListener((observable) -> {});
	}
	/*
	public void buildAndConfirmOrder(ActionEvent event) throws Exception {
		Kitchen kitchen = new Kitchen();
		Order order = orderFood(event);
		// Load order details. 
		String orderDetails = order.getOrder();
		int cookingTime = kitchen.cookTime(order);
		
	}
	*/
	
	
}

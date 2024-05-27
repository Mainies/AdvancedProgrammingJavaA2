package controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import model.database.Connect;
import model.restaurant.Order;
import model.service.*;

import java.sql.*;

public class OrderManagerController {
	/*Controller for managing the ability to collect or cancel orders, Linked to CollectOrder.fxml"
	 * 
	 */
	private UserService userService = UserService.getInstance();
	private Connect connector = new Connect();
	
	//Column attributes for table view
    @FXML private TableView<Order> orders;
    @FXML private TableColumn<Order, String> date;
    @FXML private TableColumn<Order, Number> orderNum;
    @FXML private TableColumn<Order, Number> burritos;
    @FXML private TableColumn<Order, Number> fries;
    @FXML private TableColumn<Order, Number> sodas;
    @FXML private TableColumn<Order, Number> price;
    @FXML private TextField orderNo;
    @FXML private Label warningMsg;
    
    
    @FXML
    public void initialize() {
        //user Orders initialise for uncollected orders for tableview
        //Technically factory design pattern but documentation taken from
        //https://docs.oracle.com/javafx/2/ui_controls/table-view.htm
    	date.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
        orderNum.setCellValueFactory(new PropertyValueFactory<>("orderNum"));
        burritos.setCellValueFactory(new PropertyValueFactory<>("burritos"));
        fries.setCellValueFactory(new PropertyValueFactory<>("fries"));
        sodas.setCellValueFactory(new PropertyValueFactory<>("sodas"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));  
        orders.setItems(fetchDataForUser());
    }
    

    private ObservableList<Order> fetchDataForUser() {
    	String username = userService.getObject().getUsername();
    	//Observable list for setCellValueFacotry and Property Value factory
        ObservableList<Order> ordersList = connector.getActiveOrders(username);
        return ordersList;
    } 
    
    public void backToLanding(ActionEvent event) {
    	SceneChanger.changeScene(event, "LandingPage.fxml");
    }
    
    /*
     * This needs to be moved to the connector database
     */
    public boolean checkIfValidOrder() {
    	//Checks if the order number is correct for orders to be collected
        int orderNumber = Integer.parseInt(orderNo.getText());
        String userName = userService.getObject().getUsername();
        boolean validOrder = connector.checkValidOrder(orderNumber, userName);
        if(validOrder) {
        	return true;
        } else {
            warningMsg.setText("Entered order number is not valid");
            return false;
        }
    }
    
    public void pickUpOrder(ActionEvent event) {
    	/*Action event for picking up an order
    	 * checks if order is ready for pick up using time and date with the mininum pick up time being time + cooking time
    	 * Changes order to collected if collected and updates view table
    	 */
        try {
            int orderNum = Integer.parseInt(orderNo.getText().trim());
            //check order number
            if (checkIfValidOrder()) {
            	//checking ready to pick up
                if (connector.checkIfReadyForPickUp(orderNum)) {
                	// change status to collected
                    connector.collectOrder(orderNum);
                    backToLanding(event);
                } else {
                    warningMsg.setText("Your meal is still being cooked. Please be patient.");
                }
            } else {
                warningMsg.setText("Invalid order number. Please check and try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelOrder(ActionEvent event) {
    	//option to also cancel number if collected. Important to change to cancelled so that the user cannot claim VIP points
    	if (checkIfValidOrder()) {
    		connector.makeCancelOrder(Integer.parseInt(orderNo.getText()));
    		backToLanding(event);
    	}
    }
    

    
}

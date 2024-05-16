package application;

import database.Connect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import restaurant.Order;
import service.*; 
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;

public class OrderManagerController {
	/*Controller for managing the ability to collect or cancel orders, Linked to CollectOrder.fxml"
	 * 
	 */
	private UserService userService = UserService.getInstance();
	
	
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
    	Connect connector = new Connect();
    	
    	//Observable list for setCellValueFacotry and Property Value factory
        ObservableList<Order> ordersList = FXCollections.observableArrayList();
        
        //Query that returns all orders in awaiting for collected ordered by most recent first
        String query = "SELECT dateCreated, OrderNumber, Burritos, Fries, Sodas, Price FROM Orders WHERE Status = 'await for collection' AND OrderNumber IN (SELECT OrderNumber FROM UserOrders WHERE Username = ?) ORDER BY dateCreated DESC";

        try (Connection conn = connector.make_connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            
            //Execute Query
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
            	int orderNum = rs.getInt("OrderNumber");
                int burritos = rs.getInt("Burritos");
                int fries = rs.getInt("Fries");

                int sodas = rs.getInt("Sodas");

                double price = rs.getDouble("Price");
                //use order constructor
                Order order = new Order(
                    burritos,
                    fries,
                    sodas);
                //Add missing details to order
                order.setDateCreated(rs.getString("dateCreated"));
                order.setOrderNum(orderNum);
                order.setPrice(price); 
                ordersList.add(order);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching orders: " + e.getMessage());
        }
        return ordersList;
    } 
    
    public void backToLanding(ActionEvent event) {
    	SceneChanger.changeScene(event, "LandingPage.fxml");
    }
    
    public boolean checkIfValidOrder() {
    	//Checks if the order number is correct for orders to be collected
        int orderNumber;
        try {
            orderNumber = Integer.parseInt(orderNo.getText()); 
        } catch (NumberFormatException e) {
            warningMsg.setText("Entered order number is not valid");
            return false;
        }
        //Query to return order numbers
        
        //Current design maintains stability by updating database instead of immediate state
        String query = "SELECT OrderNumber FROM Orders WHERE Status = 'await for collection' AND OrderNumber = ? AND OrderNumber IN (SELECT OrderNumber FROM UserOrders WHERE Username = ?)";
        Connect connector = new Connect();
        try (Connection conn = connector.make_connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, orderNumber);
            pstmt.setString(2, userService.getObject().getUsername());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return true;
            } else {
                warningMsg.setText("Please enter an active order");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            warningMsg.setText("Error checking order status");
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
            Connect connection = new Connect(); 
            //check order number
            if (checkIfValidOrder()) {
            	//checking ready to pick up
                if (connection.checkIfReadyForPickUp(orderNum)) {
                	// change status to collected
                    connection.pickUpOrder(orderNum);
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
    		Connect connection = new Connect();
    		connection.cancelOrder(Integer.parseInt(orderNo.getText()));
    		backToLanding(event);
    	}
    }
    

    
}

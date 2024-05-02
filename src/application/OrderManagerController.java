package application;

import database.Connect;
import database.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import restaurant.Order;
import service.*; 
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;

public class OrderManagerController {
	private UserService userService = UserService.getInstance();
    private POSService posService = POSService.getInstance();
    private OrderService orderService = OrderService.getInstance();
    private Stage stage;
    private Scene scene;

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
        ObservableList<Order> ordersList = FXCollections.observableArrayList();
        String query = "SELECT dateCreated, OrderNumber, Burritos, Fries, Sodas, Price FROM Orders WHERE Status = 'await for collection' AND OrderNumber IN (SELECT OrderNumber FROM UserOrders WHERE Username = ?) ORDER BY dateCreated DESC";

        try (Connection conn = connector.make_connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
            	int orderNum = rs.getInt("OrderNumber");
                int burritos = rs.getInt("Burritos");
                int fries = rs.getInt("Fries");

                int sodas = rs.getInt("Sodas");

                double price = rs.getDouble("Price");

                burritos = rs.wasNull() ? 0 : burritos;
                fries = rs.wasNull() ? 0 : fries;
                sodas = rs.wasNull() ? 0 : sodas;
                price = rs.wasNull() ? 0.0 : price;

                Order order = new Order(
                    burritos,
                    fries,
                    sodas);
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
    
    public boolean checkIfValidOrder() {
        int orderNumber;
        try {
            orderNumber = Integer.parseInt(orderNo.getText()); 
        } catch (NumberFormatException e) {
            warningMsg.setText("Entered order number is not valid");
            return false;
        }

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
    	if (checkIfValidOrder()) {
    		Connect connection = new Connect();
    		connection.pickUpOrder(Integer.parseInt(orderNo.getText()));
    		backToLanding(event);
    	}
    }
    
    public void cancelOrder(ActionEvent event) {
    	if (checkIfValidOrder()) {
    		Connect connection = new Connect();
    		connection.cancelOrder(Integer.parseInt(orderNo.getText()));
    		backToLanding(event);
    	}
    }
    

    
}

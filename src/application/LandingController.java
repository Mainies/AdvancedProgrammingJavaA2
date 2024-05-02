package application;

import database.Connect;
import database.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import restaurant.Order;
import service.*; 


import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;

public class LandingController {
    private Stage stage;
    private Scene scene;
    
    @FXML
    Label userName; 
        
    private UserService userService = UserService.getInstance();
    private POSService posService = POSService.getInstance();
    
    
    @FXML private TableView<Order> orders;
    @FXML private TableColumn<Order, String> date;
    @FXML private TableColumn<Order, Number> orderNum;
    @FXML private TableColumn<Order, Number> burritos;
    @FXML private TableColumn<Order, Number> fries;
    @FXML private TableColumn<Order, Number> sodas;
    @FXML private TableColumn<Order, Number> price;
    
    
    @FXML
    public void initialize() {
        updateUserName();
        
        //user Orders initialise for uncollected orders for tableview
        date.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
        orderNum.setCellValueFactory(new PropertyValueFactory<>("orderNum"));
        burritos.setCellValueFactory(new PropertyValueFactory<>("burritos"));
        fries.setCellValueFactory(new PropertyValueFactory<>("fries"));
        sodas.setCellValueFactory(new PropertyValueFactory<>("sodas"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));  
        orders.setItems(fetchDataForUser());
    }

    private void updateUserName() {
        User user = userService.getObject(); 
        if (user != null && userName != null) {
            userName.setText(user.getUsername()); 
        } else {
            if (userName != null) {
                userName.setText("No user logged in");
            }
        }
    }

    public void openOrderPane(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Orderer.fxml")); 
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
    
    
    public void managerLogin(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AccessControl.fxml"));
            Parent root = loader.load();
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    private ObservableList<Order> fetchDataForUser() {
    	String username = userService.getObject().getUsername();
    	Connect connector = new Connect();
        ObservableList<Order> ordersList = FXCollections.observableArrayList();
        String query = "SELECT dateCreated, OrderNumber, Burritos, Fries, Sodas, Price FROM Orders WHERE Collected = FALSE AND dateCollected IS NULL AND OrderNumber IN (SELECT OrderNumber FROM UserOrders WHERE Username = ?)";

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
    
    
  
}

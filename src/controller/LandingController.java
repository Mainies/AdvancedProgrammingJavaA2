package controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import model.database.Connect;
import model.database.NormalUser;
import model.database.User;
import model.database.VIPUser;
import model.restaurant.Order;
import model.service.*;

import java.sql.*;
import java.util.ArrayList;

public class LandingController {
    /* Landing Class Controller. 
     * Central Controller that provides access to all other parts of the program
     */
	
	//View of User Details
    @FXML private Label userName; 
    @FXML private Label fullName;
    //Vip Details that are Hidden if Not VIP
    @FXML private Label vipLabel;
    @FXML private Label vipPoints;
    @FXML private Label joinVIPmessage;
        
    private UserService userService = UserService.getInstance();
    private Connect connection = new Connect();
    
    
    //Table Attributes to See Current ORders
    @FXML private TableView<Order> orders;
    @FXML private TableColumn<Order, String> date;
    @FXML private TableColumn<Order, Number> orderNum;
    @FXML private TableColumn<Order, Number> burritos;
    @FXML private TableColumn<Order, Number> fries;
    @FXML private TableColumn<Order, Number> sodas;
    @FXML private TableColumn<Order, Number> price;
    
    @FXML
    private Button updatePointsButton;
    
    @FXML
    public void initialize() {
        updateUserName();
        updateFullName();
        updateVipPointsLabels();
        updateButtonVisibility();
        updateVIPmessageVisibility();        
        
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
    
    private void updateButtonVisibility() {
    	//hides vip points if not a VIP user
        User user = userService.getObject();
        updatePointsButton.setVisible(user instanceof VIPUser);  
    }
    
    private void updateVIPmessageVisibility() {
    	//Shows vip message if not a vip user
        User user = userService.getObject();
        joinVIPmessage.setVisible(user instanceof NormalUser);  
    }
    
    
    private void updateVipPointsLabels() {
    	// shows VIP points on view
        User user = userService.getObject();
        if (user instanceof VIPUser) {  
            VIPUser vipUser = (VIPUser) user;  
            vipLabel.setText("VIP Points:");
            vipPoints.setText(Integer.toString(vipUser.getLoyaltyPoints())); 
        }
    }

    private void updateUserName() {
    	
    	//updates username on view
        User user = userService.getObject(); 
        if (user != null && userName != null) {
            userName.setText(user.getUsername()); 
        } else {
            if (userName != null) {
                userName.setText("No user logged in");
            }
        }
    }
    
    private void updateFullName() {
    	
    	//updates first and last name to show user details on view
    	User user = userService.getObject(); 
        if (user != null && userName != null) {
            fullName.setText(user.getFirstName() + " " + user.getLastName()); 
        } else {
            if (userName != null) {
                userName.setText("No user logged in");
            }
        }
    }

    public void openOrderPane(ActionEvent event) {
    	SceneChanger.changeScene(event, "Orderer.fxml");
    }
    
    
    public void managerLogin(ActionEvent event) {
    	SceneChanger.changeScene(event, "AccessControl.fxml");
    }
    
    public void goToPickup(ActionEvent event) {
    	SceneChanger.changeScene(event, "CollectOrder.fxml");
    }
    

    public void goToPast(ActionEvent event) {
    	SceneChanger.changeScene(event, "PastOrders.fxml");
    }
    
    private ObservableList<Order> fetchDataForUser() {
    	//creating an observable list as per https://docs.oracle.com/javafx/2/ui_controls/table-view.htm
    	//Interacts with connector to return a list of all order data that the user in the current UserSerivice has active
    	//Facade pattern keeping databse connectivity in database
    	String username = userService.getObject().getUsername();
    	ObservableList<Order> ordersList = connection.getActiveOrders(username);
        return ordersList;
    }
    
    public void goToUpdateDetails(ActionEvent event) {
    	//Change to userController for details changeing
    	SceneChanger.changeScene(event, "UpdateDetails.fxml");
    }
    
    public void toVIPPortal(ActionEvent event) {
    	//Change to userController for VIP status change
    	SceneChanger.changeScene(event, "BecomeVIP.fxml");
    }
    
    public void logOut(ActionEvent event) {
    	//Update points. Handling of normal user in method
    	vipUserLogoutPoints();
    	//Clear user from Userservice and return to login page
    	userService.clearObject();
    	SceneChanger.changeScene(event, "Login.fxml");
    }
    
    //UPDATING VIP POINTS   
    
    public void updatePoints() {
        User user = userService.getObject(); 
        if (!(user instanceof VIPUser)) {
            System.err.println("User is not a VIPUser");
            return;
        }
        VIPUser vipUser = (VIPUser) user; //Casting user from DB to VIP user
        connection.updatePointsFull(vipUser); 
    }
    
    public void executePointsUpdate(ActionEvent e) {
    	//method that listens for action event to update points
        //collect all points from unclaimed orders and update VIP points
        updatePoints();
        //update view
        updateVipPointsLabels();
    } 
    
    public void vipUserLogoutPoints() {
        User user = userService.getObject();
        String username = user.getUsername();
        int points = 0; // Initialize points to 0.
        // Check if VIPUserand get loyalty points if VIP
        if (user instanceof VIPUser) {
            points = ((VIPUser) user).getLoyaltyPoints();
        }
        connection.logoutPoints(username, points);
    }

}

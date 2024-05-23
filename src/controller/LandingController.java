package controller;

import javafx.collections.FXCollections;
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
    
    
    //Table Attributes to See Current ORders
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
    
    @FXML
    private Button updatePointsButton;
    
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
    	Connect connector = new Connect();
    	ObservableList<Order> ordersList = connector.getActiveOrders(username);
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
    public ArrayList<Integer> getListOfUserOrderNumbers() {
    	//returns a list of order numbers that a user has ordered to be checked if they have already been claimed
        Connect conn = new Connect();
        String currUser = userService.getObject().getUsername();
        //Query to return all ordernumbers by username
        String query = "SELECT OrderNumber FROM UserOrders WHERE UserName = ?";
        ArrayList<Integer> orderNumbers = new ArrayList<>();
        try (Connection connector = conn.make_connect();
             PreparedStatement pstmt = connector.prepareStatement(query)) {
            pstmt.setString(1, currUser);
            //executing query
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                orderNumbers.add(rs.getInt("OrderNumber"));
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
        return orderNumbers;
    }

    public void checkIfPopulated(ArrayList<Integer> orderNumbersList) {
    	//Get list of VIP user orders
        ArrayList<Integer> vipOrders = fetchVipOrders();
        //Compare VIP orders to all orders
        ArrayList<Integer> numsToPopulate = getMissingVipOrders(orderNumbersList, vipOrders);
        //Populate VIP list with orders that are not in VIPUser Table
        insertMissingVipOrders(numsToPopulate);
    }

    private ArrayList<Integer> fetchVipOrders() {
    	//Query database and return list of orders
    	Connect conn = new Connect();
        String query = "SELECT OrderNumber FROM VIPPoints";
        ArrayList<Integer> vipOrders = new ArrayList<>();
        try (Connection connector = conn.make_connect();
             PreparedStatement pstmt = connector.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                vipOrders.add(rs.getInt("OrderNumber"));
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
        return vipOrders;
    }

    private ArrayList<Integer> getMissingVipOrders(ArrayList<Integer> orderNumbersList, ArrayList<Integer> vipOrders) {
    	//Compare user orders with vip list of orders. Return those that havent' been added to VIP points table
        ArrayList<Integer> numsToPopulate = new ArrayList<>();
        for (Integer orderNum : orderNumbersList) {
            if (!vipOrders.contains(orderNum)) {
                numsToPopulate.add(orderNum);
            }
        }
        return numsToPopulate;
    }

    private void insertMissingVipOrders(ArrayList<Integer> numsToPopulate) {
    	Connect conn = new Connect();
    	
    	//Put missing orders into VIP orders and set their collected value to false
        String query = "INSERT INTO VIPPoints (OrderNumber, Collected) VALUES (?, false)";
        for (Integer missingOrder : numsToPopulate) {
            try (Connection connector = conn.make_connect();
                 PreparedStatement pstmt = connector.prepareStatement(query)) {
                pstmt.setInt(1, missingOrder);
                //execute query
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("SQL Error: " + e.getMessage());
            }
        }
    }
    
    public void updatePoints(ActionEvent event) {
        Connect conn = new Connect();
        User user = userService.getObject(); 
        if (!(user instanceof VIPUser)) {
            System.err.println("User is not a VIPUser");
            return;
        }
        VIPUser vipUser = (VIPUser) user; //Casting user from DB to VIP user
        
        //Query that returns all the orders in VIP orders that haven't been collected yet for the current user
        String query = "SELECT ord.OrderNumber, ord.Price, v.Collected " +
                "FROM Orders ord " +
                "JOIN UserOrders uo ON ord.OrderNumber = uo.OrderNumber " +
                "JOIN VipPoints v ON ord.OrderNumber = v.OrderNumber " +
                "WHERE ord.Status = 'collected' AND v.Collected = false " +
                "AND uo.UserName = ?";
        
        //Connect to DB and execute query
        try (Connection connector = conn.make_connect();
            PreparedStatement pstmt = connector.prepareStatement(query)) {
            pstmt.setString(1, vipUser.getUsername());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
            	//Getting variables for interaction with program
                double price = rs.getDouble("Price");
                boolean collected = rs.getBoolean("Collected");
                int orderNumber = rs.getInt("OrderNumber");

                if (!collected) {
                	//Floor per dollar spent
                    int pointsToAdd = (int) Math.floor(price);
                    //update current loyalty points
                    vipUser.setLoyaltyPoints(vipUser.getLoyaltyPoints() + pointsToAdd);
                    //change collected to true in VIP points so it cannot be redeemed again
                    String updateQuery = "UPDATE VipPoints SET Collected = true WHERE OrderNumber = ?";
                    try (PreparedStatement updateStmt = connector.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, orderNumber);
                        updateStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
    }
    
    public void executePointsUpdate(ActionEvent e) {
    	//method that listens for action event to update points
    	
    	//get order numbers
        ArrayList<Integer> userOrderNumbers = getListOfUserOrderNumbers();
        
        //get unpopulated and add to table
        checkIfPopulated(userOrderNumbers);
        
        //collect all points from unclaimed orders and update VIP points
        updatePoints(e);
        
        //update view
        updateVipPointsLabels();
    }
    
    
    
    public void vipUserLogoutPoints() {
        Connect connect = new Connect();
        User user = userService.getObject();
        String username = user.getUsername();
        int points = 0; // Initialize points to 0.

        // Check if VIPUserand get loyalty points if VIP
        if (user instanceof VIPUser) {
            points = ((VIPUser) user).getLoyaltyPoints();
        }

        // query to update user points.
        String updateQuery = "UPDATE Users SET Points = ? WHERE username = ?";
        
        // update points in database for next log in time
        try (Connection conn = connect.make_connect();
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
            updateStmt.setInt(1, points);
            updateStmt.setString(2, username);
            updateStmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
    }

}

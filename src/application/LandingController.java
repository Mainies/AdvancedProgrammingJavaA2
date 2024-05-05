package application;

import database.Connect;
import database.User;
import database.VIPUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import restaurant.Order;
import service.*; 


import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import java.util.ArrayList;

public class LandingController {
    private Stage stage;
    private Scene scene;
    
    @FXML private Label userName; 
    @FXML private Label fullName;
        
    private UserService userService = UserService.getInstance();
    @FXML private TableView<Order> orders;
    @FXML private TableColumn<Order, String> date;
    @FXML private TableColumn<Order, Number> orderNum;
    @FXML private TableColumn<Order, Number> burritos;
    @FXML private TableColumn<Order, Number> fries;
    @FXML private TableColumn<Order, Number> sodas;
    @FXML private TableColumn<Order, Number> price;
    
    @FXML private Label vipLabel;
    @FXML private Label vipPoints;
    @FXML private Label joinVIPmessage;
    
    @FXML
    public void initialize() {
        updateUserName();
        updateFullName();
        updateVipPointsLabels();
        updateButtonVisibility();
        updateVIPmessageVisibility();        
        
        //user Orders initialise for uncollected orders for tableview
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
        User user = userService.getObject();
        updatePointsButton.setVisible(user instanceof VIPUser);  
    }
    
    private void updateVIPmessageVisibility() {
        User user = userService.getObject();
        joinVIPmessage.setVisible(user instanceof VIPUser);  
    }
    
    
    private void updateVipPointsLabels() {
        User user = userService.getObject();
        if (user instanceof VIPUser) {  
            VIPUser vipUser = (VIPUser) user;  
            vipLabel.setText("VIP Points:");
            vipPoints.setText(Integer.toString(vipUser.getLoyaltyPoints())); 
        }
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
    
    private void updateFullName() {
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
    
    public void goToPickup(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CollectOrder.fxml"));
            Parent root = loader.load();
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public void goToPast(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PastOrders.fxml"));
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
    
    public void goToUpdateDetails(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateDetails.fxml"));
            Parent root = loader.load();
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void toVIPPortal(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BecomeVIP.fxml"));
            Parent root = loader.load();
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void logOut(ActionEvent event) {
    	userService.clearObject();
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public ArrayList<Integer> getListOfUserOrderNumbers() {
        Connect conn = new Connect();
        String currUser = userService.getObject().getUsername();
        String query = "SELECT OrderNumber FROM UserOrders WHERE UserName = ?";
        ArrayList<Integer> orderNumbers = new ArrayList<>();
        try (Connection connector = conn.make_connect();
             PreparedStatement pstmt = connector.prepareStatement(query)) {
            pstmt.setString(1, currUser);
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

        ArrayList<Integer> numsToPopulate = new ArrayList<>();
        for (Integer orderNum : orderNumbersList) {
            if (!vipOrders.contains(orderNum)) {
                numsToPopulate.add(orderNum);
            }
        }
        for (Integer missingOrder : numsToPopulate) {
            String query2 = "INSERT INTO VIPPoints (OrderNumber, Collected) VALUES (?, false)";
            try (Connection connector = conn.make_connect();
                 PreparedStatement pstmt2 = connector.prepareStatement(query2)) {
                pstmt2.setInt(1, missingOrder);
                pstmt2.executeUpdate();
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
        VIPUser vipUser = (VIPUser) user;
        String query = "SELECT ord.OrderNumber, ord.Price, v.Collected " +
                "FROM Orders ord " +
                "JOIN UserOrders uo ON ord.OrderNumber = uo.OrderNumber " +
                "JOIN VipPoints v ON ord.OrderNumber = v.OrderNumber " +
                "WHERE ord.Status = 'collected' AND v.Collected = false " +
                "AND uo.UserName = ?";
        try (Connection connector = conn.make_connect();
             PreparedStatement pstmt = connector.prepareStatement(query)) {
            pstmt.setString(1, vipUser.getUsername());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                double price = rs.getDouble("Price");
                boolean collected = rs.getBoolean("Collected");
                int orderNumber = rs.getInt("OrderNumber");

                if (!collected) {
                    int pointsToAdd = (int) Math.floor(price);
                    vipUser.setLoyaltyPoints(vipUser.getLoyaltyPoints() + pointsToAdd);
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
        ArrayList<Integer> userOrderNumbers = getListOfUserOrderNumbers();
        checkIfPopulated(userOrderNumbers);
        updatePoints(e);
        updateVipPointsLabels();
    }
    

}

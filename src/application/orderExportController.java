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
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class orderExportController {
    
    @FXML
    Label userName; 
    @FXML
    Label fullName;
        
    private UserService userService = UserService.getInstance();
    
    @FXML private TableView<Order> orders;
    @FXML private TableColumn<Order, String> date;
    @FXML private TableColumn<Order, Number> orderNum;
    @FXML private TableColumn<Order, Number> burritos;
    @FXML private TableColumn<Order, Number> fries;
    @FXML private TableColumn<Order, Number> sodas;
    @FXML private TableColumn<Order, Number> price;
    @FXML private TableColumn<Order, String> status;
    @FXML private TableColumn<Order, Boolean> selected;
    
    @FXML
    public void initialize() {
    	orders.setEditable(true);
    	selected.setEditable(true);
    	
    	
    	//https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TableColumn.html#:~:text=T%3E%3E%20value
        orderNum.setCellValueFactory(new PropertyValueFactory<>("orderNum"));
        burritos.setCellValueFactory(new PropertyValueFactory<>("burritos"));
        fries.setCellValueFactory(new PropertyValueFactory<>("fries"));
        sodas.setCellValueFactory(new PropertyValueFactory<>("sodas"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));  
        selected.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selected.setCellFactory(CheckBoxTableCell.forTableColumn(selected));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        orders.setItems(fetchDataForUser());
    }
    
    @FXML
    TextField csvFileLocation;
    
    @FXML
    Label warningMsg;

    private ObservableList<Order> fetchDataForUser() {
    	String username = userService.getObject().getUsername();
    	Connect connector = new Connect();
        ObservableList<Order> ordersList = FXCollections.observableArrayList();
        String query = "SELECT dateCreated, OrderNumber, Burritos, Fries, Sodas, Price, status FROM Orders WHERE OrderNumber IN (SELECT OrderNumber FROM UserOrders WHERE Username = ?) ORDER BY dateCreated DESC";

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
                String status = rs.getString("Status");

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
                order.setStatus(status);
                ordersList.add(order);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching orders: " + e.getMessage());
        }
        return ordersList;
    }
    
    public void exportToCSV() {
        File csvFile = new File(csvFileLocation.getText());
        try (PrintWriter writer = new PrintWriter(csvFile)) {
            writer.println("DateCreated,OrderNumber,Burritos,Fries,Sodas,Price");

            for (Order order : orders.getItems()) {
                if (order.getSelected()) {
                    writer.printf("%s,%d,%d,%d,%d,%.2f%n",
                        order.getDateCreated(),
                        order.getOrderNum(),
                        order.getBurritos(),
                        order.getFries(),
                        order.getSodas(),
                        order.getPrice());
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }
    
    public void checkCSVfield(ActionEvent e) {
    	String filePath = csvFileLocation.getText(); 
        if (filePath.isEmpty()) {
            warningMsg.setText("File path cannot be empty."); 
            return;
        }
        File file = new File(filePath);
        if (!filePath.toLowerCase().endsWith(".csv")) {
            warningMsg.setText("File must be a CSV file.");
            return;
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
                file.delete(); 
                warningMsg.setText("File path is valid. You can proceed to export.");
            } catch (IOException ex) {
                warningMsg.setText("Invalid file path. Please check the permissions and path validity.");
                System.err.println("IO Error: " + ex.getMessage());
            }
        } else {
            warningMsg.setText("File path is valid and file exists.");
        }
        exportToCSV();
    }
    
    public void goBack(ActionEvent event) {
    	SceneChanger.changeScene(event, "LandingPage.fxml");
    }
}

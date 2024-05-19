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
    
    
    //Tableview columns to show orders
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
    	//Editable to change selected to be true or false
    	orders.setEditable(true);
    	selected.setEditable(true);
    	
    	//Used factory method from Tableview and Property Value Factory to get values from the ObservableList<Order>
    	//https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TableColumn.html#:~:text=T%3E%3E%20value
    	date.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
        orderNum.setCellValueFactory(new PropertyValueFactory<>("orderNum"));
        burritos.setCellValueFactory(new PropertyValueFactory<>("burritos"));
        fries.setCellValueFactory(new PropertyValueFactory<>("fries"));
        sodas.setCellValueFactory(new PropertyValueFactory<>("sodas"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));  
        selected.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selected.setCellFactory(CheckBoxTableCell.forTableColumn(selected));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        orders.setItems(fetchOrdersForUser());
    }
    
    @FXML
    TextField csvFileLocation;
    
    @FXML
    Label warningMsg;

    private ObservableList<Order> fetchOrdersForUser() {
    	//Facade Pattern and separating database connectivity
    	String username = userService.getObject().getUsername();
    	Connect connector = new Connect();
    	ObservableList<Order> ordersList = connector.getOrdersForExport(username);
        return ordersList;
    }
    
    public void exportToCSV() {
    	//Function to write to csv
        File csvFile = new File(csvFileLocation.getText());
        try (PrintWriter writer = new PrintWriter(csvFile)) {
            writer.println("DateCreated,OrderNumber,Burritos,Fries,Sodas,Price");

            for (Order order : orders.getItems()) {
                if (order.getSelected()) {
                	//if the order has been selected using the CheckBox then it is added to the csv
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
    	
    	//checks that the CSV field is valid, creates a new file if not and overwrites old file if exists
    	String filePath = csvFileLocation.getText(); 
        if (filePath.isEmpty()) {
        	//warns user of empty file path
            warningMsg.setText("File path cannot be empty."); 
            return;
        }
        File file = new File(filePath);
        if (!filePath.toLowerCase().endsWith(".csv")) {
        	//checks that file is of CSV type for safety
            warningMsg.setText("File must be a CSV file.");
            return;
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
                file.delete(); 
                //checks if the file path is safe to create and removes it if new file
                warningMsg.setText("File path is valid. You can proceed to export.");
            } catch (IOException ex) {
            	//if error, warn user of invalid file path
                warningMsg.setText("Invalid file path. Please check the permissions and path validity.");
                System.err.println("IO Error: " + ex.getMessage());
            }
        } else {
        	//If all checks pass then user can proceed to export
            warningMsg.setText("File path is valid and file exists.");
        }
        // call export method
        exportToCSV();
    }
    
    public void goBack(ActionEvent event) {
    	SceneChanger.changeScene(event, "LandingPage.fxml");
    }
}

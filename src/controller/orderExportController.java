package controller;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import model.database.Connect;
import model.restaurant.Order;
import model.service.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class OrderExportController {
    /*missing for submission
     * need to implement ability to select different parts the order for export
     * planning to implement via more editable buttons
     */
	
    @FXML private Label userName; 
    @FXML private Label fullName;
    @FXML private TextField csvFileLocation;
    @FXML private Label warningMsg;
    
    
    @FXML private CheckBox dateSelect;
    @FXML private CheckBox foodSelect;
    @FXML private CheckBox priceSelect;
    
        
    private UserService userService = UserService.getInstance();
    private Connect connector = new Connect();
    
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
    	
    	//Used factory method from Tableview and Property Value Factory to get values from the ObservableList<Order> documentation
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
    
    private ObservableList<Order> fetchOrdersForUser() {
    	//Facade Pattern and separating database connectivity
    	String username = userService.getObject().getUsername();
    	ObservableList<Order> ordersList = connector.getOrdersForExport(username);
        return ordersList;
    }
    
    public void exportToCSV(ArrayList<String> ordersList, String header) {
    	//Function to write to csv
        File csvFile = new File(csvFileLocation.getText());
        try (PrintWriter writer = new PrintWriter(csvFile)) {
            writer.println(header);

            for (String order : ordersList) {
                  writer.printf(order + "\n");
                }
        } catch (FileNotFoundException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }
    
    private ArrayList<String> trimOrderSelection(){
    	ArrayList<String> orderString = new ArrayList<String>();
   
    	for (Order order: orders.getItems()) {
    		if (order.getSelected()) {
	    		String newString = "";
	    		if(dateSelect.isSelected()) {
	        		newString = newString + order.getDateCreated() + ",";
	        	}
	    		newString = newString + Integer.toString(order.getOrderNum()) + ",";
	        	if(foodSelect.isSelected()) {
	        		newString = newString + Integer.toString(order.getBurritos()) + ",";
	        		newString = newString + Integer.toString(order.getFries()) + ",";
	        		newString = newString + Integer.toString(order.getSodas()) + ",";
	        	}
	        	if(priceSelect.isSelected()) {
	        		newString = newString + Double.toString(order.getPrice()) + ",";
	        	}
	        	orderString.add(newString);
    		}
    	}
    	
    	return orderString;
    }
    
    private String getHeaderString() {
    	String headerString = "";
    	if(dateSelect.isSelected()) {
    		headerString = headerString + "Date,";
    	}
    	headerString = headerString + "OrderNumber,";
    	if(foodSelect.isSelected()) {
    		headerString = headerString + "Burritos,Fries,Sodas,";
    	}
    	if(priceSelect.isSelected()) {
    		headerString = headerString + "Price,";
    	}
    	
    	return headerString;
    }
    
        
    public boolean checkCSVfield() {
    	//checks that the CSV field is valid, creates a new file if not and overwrites old file if exists
    	String filePath = csvFileLocation.getText(); 
        if (filePath.isEmpty()) {
        	//warns user of empty file path
            warningMsg.setText("File path cannot be empty."); 
            return false;
        }
        if (!filePath.toLowerCase().endsWith(".csv")) {
        	//checks that file is of CSV type for safety
            warningMsg.setText("File must be a CSV file.");
            return false;
        }
        return true;
        
    }
    
    public void export(ActionEvent e) {
    	if (checkCSVfield()) {
	    	ArrayList<String> orders = trimOrderSelection();
	        String header = getHeaderString();
	        exportToCSV(orders, header);
	        warningMsg.setText("File exported!");
    	}
    }
    
    public void goBack(ActionEvent event) {
    	SceneChanger.changeScene(event, "LandingPage.fxml");
    }
}

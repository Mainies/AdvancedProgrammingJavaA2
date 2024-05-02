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

public class userController {
    private UserService userService = UserService.getInstance();
    private POSService posService = POSService.getInstance();
    private OrderService orderService = OrderService.getInstance();
    private Stage stage;
    private Scene scene;
    
    @FXML
    TextField inputField;
    
    @FXML
    Label updateMessage;
    
    @FXML
    TextField emailTextInput;
    
    @FXML
    ChoiceBox choices;
    
    @FXML
    CheckBox tickBox;
    
    
    @FXML
    public void intialize() {
    	choices.getItems().addAll("First Name", "Last Name", "Password");
    	choices.setValue("First Name");
    	choices.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            System.out.println("Selected item: " + newValue);
        }
        });
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
    
       
    public void becomeVIP(ActionEvent e) {
    	if (tickBox.isSelected()) {
    	String email = emailTextInput.getText();
    	String user = userService.getObject().getUsername();
    	Connect connector = new Connect();
    	connector.updateEmail(email, user);
    	updateMessage.setText("Update Successful, please log in again for VIP benefits.");
    	} else {
    	updateMessage.setText("Please agree to terms and conditions to continue.");
    	}
    }
    
    public void updateDetails(ActionEvent e) {
        User user = userService.getObject();
        String username = user.getUsername();
        String option = (String) choices.getValue(); 
        String text = inputField.getText();          
        Connect connector = new Connect();

        switch (option) {
            case "First Name":
                connector.updateFirstName(text, username);
                user.setFirstName(text);
                updateMessage.setText("First Name Updated Successfully");
                break;
            case "Last Name":
                connector.updateLastName(text, username);
                user.setLastName(text);
                updateMessage.setText("Last Name Updated Successfully");
                break;
            case "Password":
                connector.updatePassword(text, username);
                user.setPassword(text);
                updateMessage.setText("Password Updated Successfully");
                break;
            default:
                System.out.println("Invalid option selected");
                break;
        }
    }
    
}

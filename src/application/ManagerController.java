package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

//import service.POSService;
//import service.UserService; 

public class ManagerController {
    //private UserService userservice = UserService.getInstance();
    //private POSService posservice = POSService.getInstance();
    private Stage stage;
    private Scene scene;
    private String passkey = "1234";
    
    @FXML 
    PasswordField passWord;
    
    @FXML
    Label errorMessage;
    
    
    //Interface and Password Section
    
    public void goBack(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LandingPage.fxml"));
            Parent root = loader.load();
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void managerLogin(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ManagerOptions.fxml"));
            Parent root = loader.load();
            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void checkPassword(ActionEvent event) {
    	if (passWord.getText().equals(this.passkey)) {
    		managerLogin(event);
    	}
    	else {
    		errorMessage.setText("Incorrect password. Please try again.");
    	}
    }
    
    
    
}	

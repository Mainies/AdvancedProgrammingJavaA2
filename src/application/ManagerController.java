package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

//import service.POSService;
//import service.UserService; 

public class ManagerController {
    //private UserService userservice = UserService.getInstance();
    //private POSService posservice = POSService.getInstance();
    private String passkey = "1234";
    
    @FXML 
    PasswordField passWord;
    
    @FXML
    Label errorMessage;
    
    
    //Interface and Password Section
    
    public void goBack(ActionEvent event) {
    	SceneChanger.changeScene(event, "LandingPage.fxml");
    }
    
    public void managerLogin(ActionEvent event) {
    	SceneChanger.changeScene(event, "ManagerOptions");
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

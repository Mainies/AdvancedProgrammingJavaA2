package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;


public class ManagerController extends SecureAppController{
	/* Further functionality that is not required from assignment 2
	 * Had a password portal.
	 * Linked to AccessControl.fxml and ManagerOptions.fxml
	 * Plans beyond assignment to implement functionality to change items
	 */
	
	private String passkey = "1234";
    
    private @FXML PasswordField passWord;
    private @FXML Label errorMessage;
    
    
    //Interface and Password Section
    
    public void managerLogin(ActionEvent event) {
    	//login to manager
    	SceneChanger.changeScene(event, "ManagerOptions.fxml");
    }
    
    public void checkPassword(ActionEvent event) {
    	//check if the entered password is the same as passkey
    	if (passWord.getText().equals(this.passkey)) {
    		managerLogin(event);
    	}
    	else {
    		errorMessage.setText("Incorrect password. Please try again.");
    	}
    }
        
}	

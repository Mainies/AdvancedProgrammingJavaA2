package application;

import database.Connect;
import database.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import service.*; 


import javafx.scene.control.TextField;

public class userController {
    private UserService userService = UserService.getInstance();
    
    @FXML private TextField inputField;
    @FXML private Label updateMessage;
    @FXML private TextField emailTextInput;
    @FXML private ChoiceBox<String> choices;
    @FXML private CheckBox tickBox;
    
    @FXML
    public void initialize() {
    	try {
        choices.getItems().addAll("First Name", "Last Name", "Password");
        choices.setValue("First Name");  // Set the default value
        choices.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Selected item: " + newValue);});}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public void backToLanding(ActionEvent event) {
    	SceneChanger.changeScene(event, "LandingPage.fxml");
    }
       
    public void becomeVIP(ActionEvent e) {
    	if (tickBox.isSelected()) {
    	String email = emailTextInput.getText();
    	String user = userService.getObject().getUsername();
    	Connect connector = new Connect();
    	connector.updateEmail(email, user);
    	connector.updatePoints(user);
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

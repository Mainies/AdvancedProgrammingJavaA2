package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.database.Connect;
import model.database.User;
import model.service.*;

public class userController {
	/* controller that manages BecomeVIP and UpdateDetails
	 * Allows users to become VIP, update user details
	 */
    
	private UserService userService = UserService.getInstance();
    
	//Fxml fields
    @FXML private TextField inputField;
    @FXML private Label updateMessage;
    @FXML private TextField emailTextInput;
    
    //choicebox for showing user details options 
    @FXML private ChoiceBox<String> choices;
   
    // tickbox to consent to VIP marketing
    @FXML private CheckBox tickBox;
    
    @FXML
    public void initialize() {
    	//Implements choice box for options to control user input
    	try {
        choices.getItems().addAll("First Name", "Last Name", "Password");
        choices.setValue("First Name");  // Set the default value
        choices.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Selected item: " + newValue);});}
    	catch (Exception e) {
    	}
    }
    
    public void backToLanding(ActionEvent event) {
    	SceneChanger.changeScene(event, "LandingPage.fxml");
    }
       
    //Method in BecomeVIP.fxml
    public void becomeVIP(ActionEvent e) {
    	//requires tickbox be selected
    	if (tickBox.isSelected()) {
    	String email = emailTextInput.getText();
    	String user = userService.getObject().getUsername();
    	
    	//use Connect object to update database
    	Connect connector = new Connect();
    	
    	//puts user email in database to be able to be user
    	connector.updateEmail(email, user);
    	connector.updatePoints(user);
    	
    	updateMessage.setText("Update Successful, please log in again for VIP benefits.");
    	} else {
    		//lets user know error
    	updateMessage.setText("Please agree to terms and conditions to continue.");
    	}
    }
    
    //Method for UpdateDetails.fxml
    public void updateDetails(ActionEvent e) {
    	/* changes the relevant detail of current user to new information based on choicebox*/
    	//get current object
        User user = userService.getObject();
        String username = user.getUsername();
        //get detail to be changed
        String option = (String) choices.getValue(); 
        String text = inputField.getText();          
        Connect connector = new Connect();
        //switch logic for updating detils
        switch (option) {
            case "First Name":
            	
            	//use connector to update user first name
                connector.updateFirstName(text, username);
                user.setFirstName(text);
                updateMessage.setText("First Name Updated Successfully");
                break;
            case "Last Name":

            	//use connector to update user last name
                connector.updateLastName(text, username);
                user.setLastName(text);
                updateMessage.setText("Last Name Updated Successfully");
                break;
            case "Password":

            	//use connector to update user password
                connector.updatePassword(text, username);
                user.setPassword(text);
                updateMessage.setText("Password Updated Successfully");
                break;
            default:
            	//default for programming safety
                System.out.println("Invalid option selected");
                break;
        	}
    	}
	
    /*Attempted test creating a non-public class as a separate controller for the VIP landing page which was notsuccesful.
     *  Seems to be that the class needs to be a public to be recognised as a controller to be recognised/usable in Scenebuilder*/
   
}



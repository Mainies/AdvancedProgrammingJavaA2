package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.database.NormalUser;
import model.database.User;
import model.database.VIPUser; 


interface ILogin{
	User login();
	void attemptLogin(ActionEvent event);
	void createNewUser(ActionEvent event);
}

public class LoginController extends AppController implements ILogin{
	/*Login controller linked to Login.fxml and CreateNewUser.fxml
	 * Purpose is to authenticate username and login details to get a correct user from Connect
	 * Also provides the ability to create a new user and password
	 */
	
    // User Login Interface
    private @FXML TextField userNameText;
    private @FXML PasswordField passwordText;
    private @FXML Label errorMessage;
    // New User Interface
    private @FXML TextField newUserName;
    private @FXML TextField newPassword;
    private @FXML TextField newFirstName;
    private @FXML TextField newLastName;
    private @FXML TextField newEmail;
    private @FXML Label errorMessageNewUser;
 
    //Login Page ActionEvents
    public User login() {
        boolean userExists = connection.isUser(userNameText.getText());
        //check if user is a valid user
        if (!userExists) {
        	//Invalid User
        	errorMessage.setText("Error: User Does Not Exist");
            return null;
        } else {
        	//Very poor cybersecurity but it does check if the two strings are the same
            boolean correctPassword = connection.checkPassword(userNameText.getText(), passwordText.getText());
            if (correctPassword) {
            	//If correct return correct user from database
                return connection.getUserFromDatabase(userNameText.getText());
            } else {
                errorMessage.setText("Error: Wrong Password");
                return null;
            }
        }
    }

    public void attemptLogin(ActionEvent event) {
        User currentUser = login();
        //use of User as abstract has methods to return a normal or VIP user
        if (currentUser != null) {
            userService.setObject(currentUser);
            //Update singleton Userservice
            SceneChanger.changeScene(event, "LandingPage.fxml");
            //Change view
        } else {
        	return;
        }
    }
    

	public void switchToNewUserPage(ActionEvent event) {
		SceneChanger.changeScene(event, "CreateNewUser.fxml");
	}

    //Create new user action event
    public void createNewUser(ActionEvent event) {
    	//Connect to db
        boolean userExists = connection.isUser(newUserName.getText());
        //check if user exists before trying to insert the same primary key
        if (!userExists) {
        	User user = null;
            String username = newUserName.getText();
            String pass = newPassword.getText();
            String first = newFirstName.getText();
            String last = newLastName.getText();
            String email = newEmail.getText();
            //current implentation handles both regular and VIP users
            if (email.isEmpty()) {
            	/*me-to-me: change to factory if you have time
            	 * 
            	 */
                user = new NormalUser(username, pass, first, last);
                connection.createUser(username, pass, first, last);
            } else {
                user = new VIPUser(username, pass, first, last, email, 0);
                connection.createVIPUser(username, pass, first, last, email, 0);
            }
            userService.setObject(user); 
            // update message to inform user created succesfully
            errorMessageNewUser.setText("User Added Successfully. Return to Login Page");
        } else {
        	//error message to inform user if the username is avaiable
            errorMessageNewUser.setText("Username is already in use. Try again");
        }
    }
    
	public void backtoLogin(ActionEvent event) {
		SceneChanger.changeScene(event, "Login.fxml");
	}	
}


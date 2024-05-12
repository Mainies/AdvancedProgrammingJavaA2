package application;

import database.Connect;
import database.NormalUser;
import database.User;
import database.VIPUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import service.*; 

public class LoginController {
    private Connect connector = new Connect();
    private UserService userService = UserService.getInstance(); 

    // User Login Interface
    @FXML
    TextField userNameText;

    @FXML
    PasswordField passwordText;

    @FXML
    Label errorMessage;
 
    
    public LoginController() {
        connector.connect(); 
    }

    private User login() {
        boolean userExists = connector.isUser(userNameText.getText());
        if (!userExists) {
            return null;
        } else {
            boolean correctPassword = connector.checkPassword(userNameText.getText(), passwordText.getText());
            if (correctPassword) {
                return connector.getUserFromDatabase(userNameText.getText());
            } else {
                errorMessage.setText("Error: Wrong Password or User Doesn't Exist");
                return null;
            }
        }
    }

    public void attemptLogin(ActionEvent event) {
        User currentUser = login();
        if (currentUser != null) {
            userService.setObject(currentUser);
            SceneChanger.changeScene(event, "LandingPage.fxml");
        } else {
            errorMessage.setText("Error: Wrong Password or User Doesn't Exist");
        }
    }

    // New User Interface
    @FXML
    TextField newUserName;

    @FXML
    TextField newPassword;

    @FXML
    TextField newFirstName;

    @FXML
    TextField newLastName;

    @FXML
    TextField newEmail;

    @FXML
    Label errorMessageNewUser;

    public void createNewUser(ActionEvent event) {
    	connector.connect();
        boolean userExists = connector.isUser(newUserName.getText());
        if (!userExists) {
        	User user = null;
            String username = newUserName.getText();
            String pass = newPassword.getText();
            String first = newFirstName.getText();
            String last = newLastName.getText();
            String email = newEmail.getText();
            if (email.isEmpty()) {
                user = new NormalUser(username, pass, first, last);
                connector.createUser(username, pass, first, last);
            } else {
                user = new VIPUser(username, pass, first, last, email, 0);
                connector.createVIPUser(username, pass, first, last, email, 0);
            }
            userService.setObject(user); 
        } else {
            errorMessageNewUser.setText("Username is already in use. Try again");
        }
    }
    
	public void backtoLogin(ActionEvent event) {
		SceneChanger.changeScene(event, "Login.fxml");
	}
	
	public void switchToNewUserPage(ActionEvent event) {
		SceneChanger.changeScene(event, "CreateNewUser.fxml");
	}
	
}

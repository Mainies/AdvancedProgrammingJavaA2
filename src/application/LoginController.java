package application;

import database.Connect;
import database.NormalUser;
import database.User;
import database.VIPUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.*; 

public class LoginController {
    private Stage stage;
    private Scene scene;
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
            User newUser = null;
            String username = newUserName.getText();
            String pass = newPassword.getText();
            String first = newFirstName.getText();
            String last = newLastName.getText();
            String email = newEmail.getText();
            if (email.isEmpty()) {
                newUser = new NormalUser(username, pass, first, last);
                connector.createUser(username, pass, first, last);
            } else {
                newUser = new VIPUser(username, pass, first, last, email, 0);
                connector.createVIPUser(username, pass, first, last, email, 0);
            }
            userService.setObject(newUser); 
        } else {
            errorMessageNewUser.setText("Username is already in use. Try again");
        }
    }
    
	public void backtoLogin(ActionEvent event) {
		try {
		Parent root = (Parent) FXMLLoader.load(getClass().getResource("Login.fxml"));
		stage = (Stage) ((Node)event.getSource()).getScene( ).getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void switchToNewUserPage(ActionEvent event) {
		try {
		Parent root = (Parent) FXMLLoader.load(getClass().getResource("CreateNewUser.fxml"));
		stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

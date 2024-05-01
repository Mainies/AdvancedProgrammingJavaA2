package application;

import database.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import service.*; 

public class LandingController {
    private Stage stage;
    private Scene scene;
    
    @FXML
    Label userName; 
        
    private UserService userService = UserService.getInstance();
    private POSService posService = POSService.getInstance();
    
    @FXML
    public void initialize() {
        updateUserName();
    }

    private void updateUserName() {
        User user = userService.getObject(); 
        if (user != null && userName != null) {
            userName.setText(user.getUsername()); 
        } else {
            if (userName != null) {
                userName.setText("No user logged in");
            }
        }
    }

    public void openOrderPane(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Orderer.fxml")); 
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
    
    
    public void managerLogin(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AccessControl.fxml"));
            Parent root = loader.load();
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}

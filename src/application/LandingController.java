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
import service.ApplicationService; 

public class LandingController {
    private Stage stage;
    private Scene scene;
    
    @FXML
    Label userName; 
    
    private ApplicationService appService = ApplicationService.getInstance();
    
    @FXML
    public void initialize() {
        updateUserName();
    }

    private void updateUserName() {
        User user = appService.getUser(); 
        if (user != null && userName != null) {
            userName.setText(user.username); 
        } else {
            if (userName != null) {
                userName.setText("No user logged in");
            }
        }
    }

    @FXML
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
}

package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneChanger {
	/*Static object that handles view changing. static method for readability*/
	
    public static void changeScene(ActionEvent event, String fxmlFile) {
        try {
        	//get fxml resource
            FXMLLoader loader = new FXMLLoader(SceneChanger.class.getResource(fxmlFile));
            //instantiate gui components
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            // only 1 css file for this application for simplicity
            String css = SceneChanger.class.getResource("application.css").toExternalForm();
            //update view
            scene.getStylesheets().add(css);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

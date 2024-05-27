package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SceneChanger {
	/*Static object that handles view changing. static method(s) for readability and Facade pattern*/
	
    public static void changeScene(ActionEvent event, String fxmlFile) {
        try {
        	//get fxml resource
            FXMLLoader loader = new FXMLLoader(SceneChanger.class.getResource("/view/" + fxmlFile));
            //instantiate gui components
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            // only 1 css file for this application for simplicity
            String css = SceneChanger.class.getResource("/view/application.css").toExternalForm();
            //update view
            scene.getStylesheets().add(css);
            stage.setScene(scene);
            stage.setTitle("Burrito King");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void popUp(ActionEvent event) {
    	/*popup method is implemented for the use of a popup notification when a newOrder is succesfully placed
    	 * implemented as some practice to see how to add a popup window/second window for scene changing 
    	 */
			try {
				FXMLLoader loader = new FXMLLoader(SceneChanger.class.getResource("/view/Popup.fxml"));
				Parent root = loader.load();
				Stage stage = new Stage(); //doesnt retrieve scene source and instead creates a new stage
				String css = SceneChanger.class.getResource("/view/application.css").toExternalForm();
	            //update view
				Scene scene = new Scene(root);
	            scene.getStylesheets().add(css);
				stage.setScene(scene);
				stage.show();
			} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    @FXML
    private Button okay;
    public void close(ActionEvent event) {
    	//Close pop up to return to main scene
    	Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
    	stage.close();
    }
}
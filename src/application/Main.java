package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = (Parent) FXMLLoader.load(getClass().getResource("Login.fxml"));
			Scene scene = new Scene(root);
			//String css = this.getClass().getResource("application.css").toExternalForm();
			//scene.getStylesheets().add(css);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		launch(args);
	}
}

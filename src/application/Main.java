package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;


public class Main extends Application {
	/*main class to load application. 
	 application loads to login page
	 */
	@Override
	public void start(Stage primaryStage) {
		//Scene Changer not used for first instatiation due to no action event object.
		try {
			Parent root = (Parent) FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
			primaryStage.getIcons().add(new Image("/view/bkicon.png"));
			primaryStage.setTitle("BURRITO KING");
			Scene scene = new Scene(root);
			String css = this.getClass().getResource("/view/application.css").toExternalForm();
			scene.getStylesheets().add(css);
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

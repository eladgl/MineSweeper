package mines;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MineSweeper extends Application{
	//start scene for the application
	
		@Override //start application method
		public void start(Stage stage) throws Exception {
			BorderPane root;
			GameController controller; 
			Image cursorImage = new Image("/img/shovel.png");
			Cursor cursor = new ImageCursor(cursorImage);
			
			try {
				FXMLLoader loader = new FXMLLoader();	//load fxml
				loader.setLocation(getClass().getResource("MinesFX.fxml"));
				root = loader.load();
				controller = loader.getController();	//load controller
				controller.setStage(stage);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			root.setCursor(cursor);
			Scene scene = new Scene(root);				//create scene
			stage.getIcons().add(new Image(MineSweeper.class.getResourceAsStream("/img/mineIcon.png")));
			stage.setScene(scene);						//set scene
			stage.setTitle("The Amazing Mine Sweeper"); 		
			stage.show(); 								//show app
		}
		//launch app
		public static void main(String[] args) {
			launch(args);
		}
}

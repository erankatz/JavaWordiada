/**
 * Created by eran on 20/05/2017.
 */

import javafx.application.Application;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.net.URL;


public class Main extends Application  {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("gameUI.fxml");
        fxmlLoader.setLocation(url);
        try {
            Parent root = fxmlLoader.load(url.openStream());
            Initializable welcomeController = fxmlLoader.getController();
            //welcomeController.setModel(model);
            Scene scene = new Scene(root, 1400  , 700);
            Utils.setStyleSheet(scene,"mainStyle.css");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(java.io.IOException ex){
            Utils.showExceptionMessage(ex);
        }


        //board.getColumnConstraints().add(new ColumnConstraints());
    }
}

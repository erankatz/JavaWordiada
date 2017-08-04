

/**
 * Created by eran on 25/07/2017.
 */
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @web http://zoranpavlovic.blogspot.com/
 */
public class Login extends Application implements MessageBoxInterface {


    String checkUser;
    GameModel model = new GameModel();

    public static void main(String[] args) {
        launch(args);
    }
    public void showExceptionMessage(String message){

        Platform.runLater(()-> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login Error Occured");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });

    }
    @Override
    public void start(Stage primaryStage) {
        Utils.setMessageBoxConsumer(this);
        primaryStage.setTitle("JavaFX 2 Login");
        model.setExceptionMessageConsumer((message)->Utils.showExceptionMessage(message));

        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(10,50,50,50));

        //Adding HBox
        HBox hb = new HBox();
        hb.setPadding(new Insets(20,20,20,30));

        //Adding GridPane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20,20,20,20));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        //Implementing Nodes for GridPane
        Label lblUserName = new Label("Username");
        final TextField txtUserName = new TextField();
        CheckBox CheckBoxisComputer = new CheckBox("Computer");
        Button btnLogin = new Button("Login");
        Button btnStart = new Button("Start");
        btnStart.setVisible(false);
        final Label lblMessage = new Label();
        ComboBox<String> comboBoxGames = new ComboBox<>();
        comboBoxGames.setVisible(false);
        //Adding Nodes to GridPane layout
        gridPane.add(lblUserName, 0, 0);
        gridPane.add(txtUserName, 1, 0);
        gridPane.add(CheckBoxisComputer, 0, 1);
        gridPane.add(btnLogin, 2, 1);
        gridPane.add(btnStart, 2, 1);
        gridPane.add(lblMessage, 1, 2);
        gridPane.add(comboBoxGames,1,0);
        comboBoxGames.setMinSize(200,20);
        //Reflection for gridPane
        Reflection r = new Reflection();
        r.setFraction(0.7f);
        gridPane.setEffect(r);

        //DropShadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(5);
        dropShadow.setOffsetY(5);

        //Adding text and DropShadow effect to it
        Text text = new Text("Wordiada Login");
        text.setFont(Font.font("Courier New", FontWeight.BOLD, 28));
        text.setEffect(dropShadow);

        //Adding text to HBox
        hb.getChildren().add(text);

        //Add ID's to Nodes
        bp.setId("bp");
        gridPane.setId("root");
        btnLogin.setId("btnLogin");
        btnStart.setId("btnStart");
        text.setId("text");

        //Action for btnLogin
        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                checkUser = txtUserName.getText().toString();
                if(model.login(checkUser,CheckBoxisComputer.isSelected())){
                    lblMessage.setText("Login Succeed redirecting to lobby room");
                    lblMessage.setTextFill(Color.GREEN);
                    comboBoxGames.getItems().clear();
                    model.getGamesList().forEach(s->comboBoxGames.getItems().add(s));
                    txtUserName.setVisible(false);
                    comboBoxGames.setVisible(true);
                    CheckBoxisComputer.setVisible(false);
                    lblUserName.setText("Game");
                    btnLogin.setVisible(false);
                    btnStart.setVisible(true);
                }
                else{
                    lblMessage.setText("Incorrect user or pw.");
                    lblMessage.setTextFill(Color.RED);
                }
            }
        });

        btnStart.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (model.joinGame(comboBoxGames.getSelectionModel().getSelectedItem())) {
                    ((Node)event.getSource()).getScene().getWindow().hide();
                    loadGameUI();

                }
            }
        });

            //Add HBox and GridPane layout to BorderPane Layout
        bp.setTop(hb);
        bp.setCenter(gridPane);

        //Adding BorderPane to the scene and loading CSS
        Scene scene = new Scene(bp);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("login.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.titleProperty().bind(
                scene.widthProperty().asString().
                        concat(" : ").
                        concat(scene.heightProperty().asString()));
        //primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void loadGameUI(){
        try {
            URL url = getClass().getResource("gameUI.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(url);
            Parent root =  fxmlLoader.load();
            GameUIController gameUIController = fxmlLoader.getController();
            gameUIController.setModel(model);
            model.loadGameDetails();
            gameUIController.setModel(model);
        /*
         * if "fx:controller" is not set in fxml
         * fxmlLoader.setController(NewWindowController);
         */
            Scene scene = new Scene(root, 1400  , 700);
            Utils.setStyleSheet(scene,"mainStyle.css");
            Stage stage = new Stage();
            stage.setTitle("game Room");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }
}

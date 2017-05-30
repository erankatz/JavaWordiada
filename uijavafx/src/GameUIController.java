/**
 * Created by eran on 20/05/2017.
 */

import engine.Card;
import engine.GameManager;
import engine.exception.EngineException;
import engine.exception.dice.DiceException;
import engine.listener.DisableAllCardsListener;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.xpath.XPathExpressionException;

public class GameUIController implements Initializable  {

    @FXML BorderPane boarderPane;
    @FXML Button buttonStart;
    @FXML Button buttonExit;
    @FXML Button buttonLoadXml;
    @FXML Button buttonRollDice;
    GameModel model = new GameModel();
    BoardButtonController boardButtonController;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setConsumers(model);
        FXMLLoader fxmlLoader = new FXMLLoader();
        buttonStart.setOnMouseClicked((Event e) -> {
                try{
                    model.newGame();
                    boardButtonController.setModel(model);
                } catch (DiceException ex){
                    Utils.showExceptionMessage(ex);
                } catch (IOException ex){
                    Utils.showExceptionMessage(ex);
                }});
        buttonStart.setDisable(true);

        buttonLoadXml.setOnMouseClicked((Event e)->{
            Stage newStage = new Stage();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            File file = fileChooser.showOpenDialog(newStage);
            if (file != null){
                try{
                    model.readXmlFile(file);
                    buttonStart.setDisable(false);
                } catch (IOException ex){
                    Utils.showExceptionMessage(ex);
                } catch (EngineException ex)
                {
                    Utils.showExceptionMessage(ex);
                } catch (XPathExpressionException ex){
                    Utils.showExceptionMessage(ex);
                }
            }
        });

        this.buttonRollDice.setOnMouseClicked((Event e) -> {
            model.rollDice();
        });
        url = getClass().getResource("BoardButton.fxml");
        fxmlLoader.setLocation(url);
        try {
            Node boardButton = fxmlLoader.load();
            boardButtonController = fxmlLoader.getController();
            boarderPane.setCenter(boardButton);
            boardButton.prefWidth(500);
            boardButton.prefHeight(500);
        } catch (java.io.IOException ex){

        }
    }

    private void setConsumers(GameModel model) {
        model.setDisableAllCardsConsumer((Boolean flag)->
                Platform.runLater(
                        ()-> boardButtonController.setDisable(flag)
                )
        );
        model.setCardConsumer((Card c)->
            Platform.runLater(
                    ()->boardButtonController.updateCharCardLetter(c.getRow(),c.getCol(),c.getLetter())
            ));
    }
}

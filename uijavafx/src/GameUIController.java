/**
 * Created by eran on 20/05/2017.
 */

import engine.Card;
import engine.GameManager;
import engine.Player;
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
import java.util.Map;
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
    @FXML Button buttonRevealCard;
    @FXML Button buttonRevealWord;
    @FXML Label labelScoreMode;
    @FXML Label labelIsGoldfishMode;
    @FXML Label labelPlayerTurn;
    @FXML TextArea textBoxLowestFrequencyDictionaryWords;
    @FXML TextArea textBoxLetterFrequencyInDeck;

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
        buttonRevealCard.setOnMouseClicked((Event e) ->{
            model.revealCards();
        }) ;
        buttonRevealWord.setOnMouseClicked((Event e) ->{
            model.revealWord();
        });
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
                    labelPlayerTurn.setText("PlayerTurn: 0");
                    labelIsGoldfishMode.setText("Gold Fish Mode:");
                    if (model.getIsGoldFish())
                            labelIsGoldfishMode.setText("Gold Fish Mode: True");
                    else
                            labelIsGoldfishMode.setText("Gold Fish Mode: False");

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
        model.setRolledDicesConsumer((Integer result)->{
            Utils.printMessage(String.format("Pick %d Cards in the board\n", result));
        });
        model.setCardRemovedConsumer((e)->
                Platform.runLater(()->
                    boardButtonController.removeCard(e.getKey(),e.getValue())
                )
        );
        model.setCardSelectedConsumer((e)->
                Platform.runLater(()->
                    boardButtonController.selectCard(e.getKey(),e.getValue())
                )
        );
        model.setLetterFrequencyInDeckConsumer((frequency) ->
            Platform.runLater(()->
                    updateLetterFrequencyInDeckTextBox(frequency)
            )
        );
        model.setPlayerTurnConsumer((playerId)->
            Platform.runLater(()->
                    labelPlayerTurn.setText("Player Turn: " + playerId)
            )
        );

    }

    private void updateLetterFrequencyInDeckTextBox(Map<Character,Long> frequency){
        textBoxLetterFrequencyInDeck.clear();
            frequency
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e1->printCharFrequencyToTextBox(e1,model.getNumOfCardInDeck()));
    }

    private void printCharFrequencyToTextBox(Map.Entry<Character,Long> ch2Freq,int NumOfCardInDeck){
        textBoxLetterFrequencyInDeck.appendText(
                String.format("%c - %d/%d\n",ch2Freq.getKey(),ch2Freq.getValue(), NumOfCardInDeck)
        );;
    }
}

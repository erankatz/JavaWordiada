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
import java.util.*;
import java.util.stream.Collectors;

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
    @FXML Button buttonClearCardSelection;

    GameModel model = new GameModel();
    BoardButtonController boardButtonController;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setConsumers(model);
        FXMLLoader fxmlLoader = new FXMLLoader();
        buttonRevealWord.setDisable(true);
        buttonRevealCard.setDisable(true);
        buttonRollDice.setDisable(true);
        buttonStart.setOnMouseClicked((Event e) ->
            model.startGame());
        buttonClearCardSelection.setOnMouseClicked((Event e)->model.clearCardSelection());
        buttonClearCardSelection.disableProperty().bind(((buttonRevealCard.disabledProperty().not()).or(buttonRevealWord.disabledProperty().not())).not());
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
                    model.newGame();
                    boardButtonController.setModel(model);
                    textBoxLowestFrequencyDictionaryWords.setText(model.getLowestFrequencyDictionaryWords());
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
                    ()->boardButtonController.updateCharCard(c)
            ));
        model.setRolledDicesConsumer((result)->{
            Utils.printMessage(String.format("Pick %d Cards in the board\n", result.getKey(),result.getValue()));
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
        model.setWordRevealedWord2Score((e)->
            Platform.runLater(()->
                   Utils.printMessage("You got " + e.getValue() + " for trying to composing word " + e.getKey() +
                           " you have "+ model.getCurrentPlayerRetriesLeft() + " more chances " )
            )
        );
        model.setIsRealedWordPendingConsumer((isPending)->
            Platform.runLater(()->
                    buttonRevealWord.setDisable(!isPending)
            )
        );
        model.setIsRevealedCardPendingConsumer((isPending)->
                Platform.runLater(()->
                        buttonRevealCard.setDisable(!isPending)
                )
        );
        model.setIsRolledDicesPendingConsumer((isPending)->
            Platform.runLater(()->
                    buttonRollDice.setDisable(!isPending)
            )
        );
        model.setGameOverConsumer((id)->
        Platform.runLater(()->
            Utils.printMessage("The winner is player id :" + id)));
    }

    private void updateLetterFrequencyInDeckTextBox(Map<Character,Long> frequency) {
        String charFrequencyToTextBoxText = frequency
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e1 -> printCharFrequencyToTextBox(e1, model.getNumOfCardInDeck()))
                .collect(Collectors.joining());
        Platform.runLater(() ->
                textBoxLetterFrequencyInDeck.setText(charFrequencyToTextBoxText)
        );
    }

    private String printCharFrequencyToTextBox(Map.Entry<Character,Long> ch2Freq,int NumOfCardInDeck){
        return String.format("%c - %d/%d\n",ch2Freq.getKey(),ch2Freq.getValue(), NumOfCardInDeck);
    }
}

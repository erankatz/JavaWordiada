/**
 * Created by eran on 20/05/2017.
 */

import engine.Card;
import engine.GameManager;
import engine.Player;
import engine.PlayerData;
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
import javafx.scene.control.cell.PropertyValueFactory;
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
    @FXML TableColumn nameCol;
    @FXML TableColumn typeCol;
    @FXML TableColumn idCol;
    @FXML TableColumn scoreCol;
    @FXML TableView<PlayerData> playersTable;
    @FXML Button buttonQuitGame;
    @FXML Label labelStatus;
    @FXML Button buttonPrev;
    @FXML Button buttonNext;
    @FXML Button buttonGetCurrentPlayerStatus;
    GameModel model = new GameModel();
    BoardButtonController boardButtonController;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        playersTable.setDisable(true);
        buttonPrev.setVisible(false);
        buttonNext.setVisible(false);
        buttonQuitGame.setDisable(true);
        buttonGetCurrentPlayerStatus.setVisible(false);
        labelStatus.setText("");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));

        setConsumers(model);
        FXMLLoader fxmlLoader = new FXMLLoader();
        buttonRevealWord.setDisable(true);
        buttonRevealCard.setDisable(true);
        buttonRollDice.setDisable(true);
        buttonQuitGame.setOnMouseClicked(e->model.quitGame());
        buttonStart.setOnMouseClicked((Event e) ->{
                    buttonQuitGame.setDisable(false);
                    model.startGame();
                }
        );
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
                    model.getPlayersData().forEach(pl->playersTable.getItems().add(pl));
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
        this.buttonPrev.setOnMouseClicked((Event e)-> model.playPrevMove());
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
        model.setRolledDicesConsumer((result)->
            Platform.runLater(()->{
                    if (!model.isComputerPlayerPlays())
                        labelStatus.setText(String.format("Pick %d Cards in the board\n", result.getKey(),result.getValue()));
                    else
                        labelStatus.setText(String.format("Computer Player got %d Cards on dices, \ncurrently reveling cards in the board\n", result.getKey(),result.getValue()));
            }));
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
        model.setPlayerTurnConsumer((playerIndex)->
            Platform.runLater(()-> {
                playersTable.getSelectionModel().select(playerIndex);
                //labelPlayerTurn.setText("Player Turn: " + playerId)
            })
        );
        model.setWordRevealedWord2Score((e)->
            Platform.runLater(()->{
                    if (!model.isComputerPlayerPlays())
                        labelStatus.setText("You got " + e.getValue() + " for trying to composing word " + e.getKey() +
                               " you have "+ model.getCurrentPlayerRetriesLeft() + " more chances " );
                    else
                        labelStatus.setText("Computer Player got " + e.getValue() + " for trying to composing word " + e.getKey());
            })
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
        Platform.runLater(
                ()-> {
                    labelStatus.setText("The winner is player id :" + id);
                    buttonPrev.setVisible(true);
                    buttonNext.setVisible(true);
                    buttonGetCurrentPlayerStatus.setDisable(true);
                    buttonRollDice.setDisable(true);
                    buttonRevealCard.setDisable(true);
                    buttonQuitGame.setDisable(true);
                    buttonRevealWord.setDisable(true);
                }));

        model.setUpdatePlayerScoreConsumer(pl->
            Platform.runLater(() -> {
                        playersTable.getItems().get(pl.getIndex()).setScore(pl.getScore());
                        playersTable.refresh();
                    }));
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

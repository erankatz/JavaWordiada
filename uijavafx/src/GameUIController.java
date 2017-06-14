/**
 * Created by eran on 20/05/2017.
 */

import engine.Card;
import engine.PlayerData;
import engine.exception.EngineException;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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
    @FXML Button buttonPlayTurn;
    @FXML AnchorPane anchorPane;
    @FXML HBox hBoxHistoryPlays;
    @FXML Label labelRoundNumber;
    @FXML ImageView gameLogo;
    private NumberTextField textBoxHistoryPlays;

    GameModel model = new GameModel();
    BoardButtonController boardButtonController;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initNumberTextField();
        Image image = new Image(getClass().getResourceAsStream("gameLogo.jpg"));
        gameLogo.setImage(image);
        buttonPlayTurn.setVisible(false);
        playersTable.setDisable(true);
        buttonPrev.setVisible(false);
        buttonNext.setVisible(false);
        textBoxHistoryPlays.setVisible(false);
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
        buttonQuitGame.setOnMouseClicked(e-> {
            if (Utils.YesNoDialog("Are You sure","Quite Game Dialog")){{
                    model.quitGame();
            }}});
        buttonStart.setOnMouseClicked((Event e) ->{
                    clearGameUI();
                    model.startGame();
                    initNewGameUI();
                    buttonGetCurrentPlayerStatus.disableProperty().bind(((buttonRevealCard.disabledProperty().not()).or(buttonRevealWord.disabledProperty().not()).or(buttonRollDice.disabledProperty().not())).not());
            }
        );
        buttonClearCardSelection.setOnMouseClicked((Event e)->model.clearCardSelection());
        buttonClearCardSelection.disableProperty().bind(((buttonRevealCard.disabledProperty().not()).or(buttonRevealWord.disabledProperty().not())).not());
        buttonQuitGame.disableProperty().bind(((buttonRevealCard.disabledProperty().not()).or(buttonRevealWord.disabledProperty().not()).or(buttonRollDice.disabledProperty().not())).not());
        buttonGetCurrentPlayerStatus.disableProperty().bind(((buttonRevealCard.disabledProperty().not()).or(buttonRevealWord.disabledProperty().not()).or(buttonRollDice.disabledProperty().not())).not());
        buttonRevealCard.setOnMouseClicked((Event e) ->{
            model.revealCards();
        }) ;
        buttonRevealWord.setOnMouseClicked((Event e) ->{
            model.revealWord();
        });
        buttonNext.setOnMouseClicked((Event e)->{
            if (!textBoxHistoryPlays.getNumber().equals(new BigDecimal(model.getTotalNumOfTurnsElapsed())))
                textBoxHistoryPlays.setNumber(textBoxHistoryPlays.getNumber().add(new BigDecimal(1)));
        });
        buttonPrev.setOnMouseClicked((Event e)->{
            if (!textBoxHistoryPlays.getNumber().equals(new BigDecimal(0)))
                textBoxHistoryPlays.setNumber(textBoxHistoryPlays.getNumber().subtract(new BigDecimal(1)));
        });
        buttonStart.setDisable(true);
        buttonLoadXml.setOnMouseClicked((Event e)->{
            Stage newStage = new Stage();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            File file = fileChooser.showOpenDialog(newStage);
            if (file != null){
                try{
                    boardButtonController.clearAll();
                    clearGameUI();
                    model.readXmlFile(file);
                    model.newGame();
                    boardButtonController.setModel(model);
                    initNewGameUI();
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
        this.buttonGetCurrentPlayerStatus.setOnMouseClicked((Event e) ->
                Platform.runLater(
                        ()->Utils.printMessage(model.getCurrentPlayerStatus())
                )
        );
        this.buttonPlayTurn.setOnMouseClicked((Event e)-> model.playPrevMove(Integer.parseInt(textBoxHistoryPlays.getText())));
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

    private void initNewGameUI() {
        if (model.getIsGoldFish())
            labelIsGoldfishMode.setText("Gold Fish Mode: True");
        else
            labelIsGoldfishMode.setText("Gold Fish Mode: False");
        model.getPlayersData().forEach(pl->playersTable.getItems().add(pl));
        textBoxLowestFrequencyDictionaryWords.setText(model.getLowestFrequencyDictionaryWords());
    }

    private void clearGameUI(){
        playersTable.getItems().clear();
        buttonStart.setDisable(false);
        labelPlayerTurn.setText("PlayerTurn:");
        labelRoundNumber.setText("Round Number:");
        labelIsGoldfishMode.setText("Gold Fish Mode:");
    }

    private void initNumberTextField(){
        textBoxHistoryPlays = new NumberTextField();
        textBoxHistoryPlays.setPrefWidth(30);
        textBoxHistoryPlays.setPrefHeight(31);
        textBoxHistoryPlays.setNumber(new BigDecimal(0));
        hBoxHistoryPlays.getChildren().add(2,textBoxHistoryPlays);
        textBoxHistoryPlays.setDisable(true);
    }

    private void setConsumers(GameModel model) {
        model.setDisableAllCardsConsumer((Boolean flag)->
                Platform.runLater(()->
                        //()-> boardButtonController.setDisable(flag)
                        labelIsGoldfishMode.setText(labelIsGoldfishMode.getText())
                )
        );
        model.setCardConsumer((Card c)->
            Platform.runLater(
                    ()->boardButtonController.updateCharCard(c)
            ));
        model.setRolledDicesConsumer((result)->
            Platform.runLater(()->{
                boardButtonController.setDisable(false);
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
                labelPlayerTurn.setText("Player Turn: " + playersTable.getItems().get(playerIndex).getId());
                labelRoundNumber.setText("Round Number: " + model.getCurrentNumofTurnsElapsed());
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
            Platform.runLater(()->{
                if (!model.isComputerPlayerPlays() && !model.getIsReplayMode()){
                    buttonRevealWord.setDisable(!isPending);
                } else {
                    buttonRevealWord.setDisable(true);
                }
            }
            )
        );
        model.setIsRevealedCardPendingConsumer((isPending)->
                Platform.runLater(()->{
                        if (!model.isComputerPlayerPlays() && !model.getIsReplayMode()){
                            buttonRevealCard.setDisable(!isPending);
                        } else {
                            buttonRevealCard.setDisable(true);
                        }
                }
                )
        );
        model.setIsRolledDicesPendingConsumer((isPending)->
            Platform.runLater(()->{
                    boardButtonController.setDisable(true);
                    if (!model.isComputerPlayerPlays() && !model.getIsReplayMode()){
                        buttonRollDice.setDisable(!isPending);
                    } else {
                        buttonRollDice.setDisable(true);
                    }
                }
            )
        );
        model.setGameOverConsumer((playerIndex)->
        Platform.runLater(
                ()-> {
                    buttonGetCurrentPlayerStatus.disableProperty().unbind();
                    buttonGetCurrentPlayerStatus.setDisable(false);
                    labelStatus.setText("The winner is player id :" + playersTable.getItems().get(playerIndex).getId());
                    buttonPrev.setVisible(true);
                    buttonNext.setVisible(true);
                    buttonPlayTurn.setVisible(true);
                    textBoxHistoryPlays.setVisible(true);
                    buttonRollDice.setDisable(true);
                    buttonRevealCard.setDisable(true);
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

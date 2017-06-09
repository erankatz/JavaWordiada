import engine.*;
import engine.exception.EngineException;
import engine.exception.board.BoardSizeOutOfRangeException;
import engine.exception.board.NotEnoughCardsToFillBoardException;
import engine.exception.board.WrongCardPositionException;
import engine.exception.dice.DiceException;
import engine.exception.file.FileExtensionException;
import engine.exception.letter.LetterException;
import engine.listener.*;
import javafx.scene.image.WritableImage;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Executable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by eran on 29/05/2017.
 */
public class GameModel {
    private GameManager manager;
    private Board board;
    private BoardButtonController boardButtonController;
    private List<List<WritableImage>> gameHistory;
    private Consumer<Map.Entry<Integer,Integer>> cardRemoved;
    private Consumer<Map.Entry<Integer,Integer>> cardSelected;
    private Consumer<Boolean> isDisabledAllCards;
    private Consumer<Map.Entry<Integer,Integer>> rolledDicesResult2RetriesLeft;
    private Consumer<Card> updateCard;
    private Consumer<Map<Character,Long>> letterFrequencyInDeck;
    private Consumer<Integer> playerTurn;
    private Consumer<Map.Entry<String,Integer>> wordRevealedWord2Score;
    private Consumer<Boolean> isRealedWordPending;
    private Consumer<Boolean> isReaveledCardPending;
    private Consumer<Boolean> isRolledDicesPending;
    private Consumer<Integer> gameOverConsumer;
    private Consumer<PlayerData> updatePlayerScoreConsumer;

    public void readXmlFile(File file)throws java.io.IOException,LetterException,XPathExpressionException,BoardSizeOutOfRangeException,NotEnoughCardsToFillBoardException,FileExtensionException,DiceException {
        manager = new GameManager();
        manager.registerEnableAllCardsListener(()->isDisabledAllCards.accept(false));
        manager.registerDisableAllCardsListener(()->isDisabledAllCards.accept(true));
        manager.registerCardChangedListener((Card c)->updateCard.accept(c));
        manager.registerCardSelectedListener(
                (int row,int col) -> cardSelected.accept(new  AbstractMap.SimpleEntry(row,col))
        );
        manager.registerCardRemovedListener((row,col)->cardRemoved.accept(new AbstractMap.SimpleEntry(row,col)));
        manager.registerRollDices((result,retriesNumber) -> rolledDicesResult2RetriesLeft.accept(new AbstractMap.SimpleEntry(result,retriesNumber)));
        manager.registerLetterFrequencyInDeckListener((map) ->letterFrequencyInDeck.accept(map));
        manager.registerPlayerTurn((playerId -> playerTurn.accept(playerId)));
        manager.registerWordRevealedListener((word,score)->wordRevealedWord2Score.accept(new AbstractMap.SimpleEntry(word,score)));
        manager.registerRevealWordPendingListener((isPending) ->isRealedWordPending.accept(isPending));
        manager.registerRevealCardPendingListener((isPending) ->isReaveledCardPending.accept(isPending));
        manager.registerRollDicesPendingListeners((isPending) ->isRolledDicesPending.accept(isPending));
        manager.registerGameOverListener((id)->gameOverConsumer.accept(id));
        manager.registerPlayerDataChangedListener(pl->updatePlayerScoreConsumer.accept(pl));
        manager.readXmlFile(file);
    }

    public void selectCard(int row,int col){
        manager.getBoard().selectBoardCard(row, col,true);
    }

    public void setCardRemovedConsumer(Consumer<Map.Entry<Integer,Integer>> listenerConsumer){
        cardRemoved = listenerConsumer;
    }

    public void setWordRevealedWord2Score(Consumer<Map.Entry<String,Integer>> listenerConsumer){
        wordRevealedWord2Score = listenerConsumer;
    }

    public void setCardSelectedConsumer(Consumer<Map.Entry<Integer,Integer>> listenerConsumer){
        cardSelected = listenerConsumer;
    }


    public void setCardConsumer(Consumer<Card> listenerConsumer){
        updateCard = listenerConsumer;
    }

    public void setDisableAllCardsConsumer(Consumer<Boolean> listenerConsumer){
        isDisabledAllCards = listenerConsumer;
    }

    public void setRolledDicesConsumer(Consumer<Map.Entry<Integer,Integer>> listenerConsumer){
        rolledDicesResult2RetriesLeft = listenerConsumer;
    }

    public int getCurrentPlayerRetriesLeft(){
        return  manager.getPlayers()[manager.getCurrentPlayerTurn()].getRetriesNumber();
    }

    public void setLetterFrequencyInDeckConsumer(Consumer<Map<Character,Long>> listenerConsumer){
        letterFrequencyInDeck = listenerConsumer;
    }

    public void setPlayerTurnConsumer(Consumer<Integer> listenerConsumer){
        playerTurn = listenerConsumer;
    }

    public boolean getIsEnabledCardConsumer(int row, int col) {
        try{
            return board.getBoardCard(row,col).getIsEnabled();
        } catch (Exception ex){
            return false;
        }
    }

    public void newGame() throws DiceException,IOException{
        List<Boolean> f = new ArrayList<>();
        f.add(true);
        f.add(false);
        manager.newGame(f);
        board = manager.getBoard();
    }

    public int rollDice() {
        return manager.getPlayers()[manager.getCurrentPlayerTurn()].rollDice();
    }

    public char getCardLetter(int row, int col) {
        try{
            return board.getBoardCard(row,col).getLetter();
        } catch (Exception ex){
            Utils.showExceptionMessage(ex);
        }
        return '*';
    }

    public int getBoardSize() {
        return manager.getBoard().getBoardSize();
    }


    public void revealCards() {
        try{
            manager.getPlayers()[manager.getCurrentPlayerTurn()].revealCards();
        } catch (EngineException ex){
            Utils.showExceptionMessage(ex);
        }
    }

    public void revealWord(){
        try{
            manager.getPlayers()[manager.getCurrentPlayerTurn()].revealWord();
        } catch (EngineException ex){
            Utils.showExceptionMessage(ex);
        }
    }

    public int getNumOfCardInDeck() {
        return manager.getNumOfCardInDeck();
    }

    public boolean getIsGoldFish() {
        return manager.getIsGoldFishMode();
    }

    public void setIsRealedWordPendingConsumer(Consumer<Boolean> isRealedWordPending) {
        this.isRealedWordPending = isRealedWordPending;
    }

    public void setIsRevealedCardPendingConsumer(Consumer<Boolean> isRealedWordPending) {
        this.isReaveledCardPending = isRealedWordPending;
    }

    public void setUpdatePlayerScoreConsumer(Consumer<PlayerData> pl){
        this.updatePlayerScoreConsumer = pl;
    }

    public void setIsRolledDicesPendingConsumer(Consumer<Boolean> isRolledDicesPending) {
        this.isRolledDicesPending = isRolledDicesPending;
    }

    public void setGameOverConsumer(Consumer<Integer> listener){
        gameOverConsumer = listener;
    }

    public void startGame() {
        manager.startGame();
    }

    public boolean isComputerPlayerPlays(){
        return manager.getPlayers()[manager.getCurrentPlayerTurn()] instanceof ComputerPlayer;
    }

    public void clearCardSelection() {
        manager.getBoard().clearSelectedCards();
    }

    public String getLowestFrequencyDictionaryWords(){
        return manager.getBoard().getLowestFrequencyDictionaryWords();

    }

    public List<PlayerData> getPlayersData(){
        return manager.getPlayersData();
    }

    public void quitGame() {
        manager.playerQuit();
    }
}

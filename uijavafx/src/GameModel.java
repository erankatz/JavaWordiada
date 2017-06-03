import engine.Board;
import engine.Card;
import engine.GameManager;
import engine.exception.EngineException;
import engine.exception.board.BoardSizeOutOfRangeException;
import engine.exception.board.NotEnoughCardsToFillBoardException;
import engine.exception.board.WrongCardPositionException;
import engine.exception.dice.DiceException;
import engine.exception.file.FileExtensionException;
import engine.exception.letter.LetterException;
import engine.listener.*;

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

    private Consumer<Map.Entry<Integer,Integer>> cardRemoved;
    private Consumer<Map.Entry<Integer,Integer>> cardSelected;
    private Consumer<Boolean> isDisabledAllCards;
    private Consumer<Integer> rolledDices;
    private Consumer<Card> updateCard;
    private Consumer<Map<Character,Long>> letterFrequencyInDeck;
    private Consumer<Integer> playerTurn;


    public void readXmlFile(File file)throws java.io.IOException,LetterException,XPathExpressionException,BoardSizeOutOfRangeException,NotEnoughCardsToFillBoardException,FileExtensionException,DiceException {
        manager = new GameManager();
        manager.registerEnableAllCardsListener(()->isDisabledAllCards.accept(false));
        manager.registerDisableAllCardsListener(()->isDisabledAllCards.accept(true));
        manager.registerCardChangedListener((Card c)->updateCard.accept(c));
        manager.registerCardSelectedListener(
                (int row,int col) -> cardSelected.accept(new  AbstractMap.SimpleEntry(row,col))
        );
        manager.registerCardRemovedListener((row,col)->cardRemoved.accept(new AbstractMap.SimpleEntry(row,col)));
        manager.registerRollDices((int result) -> rolledDices.accept(result));
        manager.registerLetterFrequencyInDeckListener((map) ->letterFrequencyInDeck.accept(map));
        manager.registerPlayerTurn((playerId -> playerTurn.accept(playerId)));
        manager.readXmlFile(file);
    }

    public void selectCard(int row,int col){
        manager.getBoard().selectBoardCard(row, col);
    }

    public void setCardRemovedConsumer(Consumer<Map.Entry<Integer,Integer>> listenerConsumer){
        cardRemoved = listenerConsumer;
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

    public void setRolledDicesConsumer(Consumer<Integer> listenerConsumer){
        rolledDices = listenerConsumer;
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
        f.add(false);
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
            manager.getBoard().revealCards();
        } catch (EngineException ex){
            Utils.showExceptionMessage(ex);
        }
    }

    public void revealWord(){
        try{
            manager.getBoard().revealWord();
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
}

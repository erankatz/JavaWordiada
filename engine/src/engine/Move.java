package engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by eran on 09/06/2017.
 */
public class Move implements Cloneable {
    private final int sleepTime = Utils.sleepTime;
    private Integer diceResult;
    private List<PlayerData> playersData;
    private int currentPlayerIndex;
    private List<Card> revealedCards;
    private List<List<Card>> revealedWords = new ArrayList<>();
    private GameManager manager;
    private Board board;
    private Player[] players;

    public synchronized void playMove(){
        playersData.stream().forEach(pl ->manager.notifyPlayerDataChangedListener(pl));
        manager.notifyPlayerTurnListeners(currentPlayerIndex);
        manager.notifyLetterFrequencyInDeckListeners(board.getDeck().CreateMapStructureCharToLong());
        Utils.sleepForAWhile(sleepTime);
        board.clearSelectedCards();
        Utils.sleepForAWhile(sleepTime);
        board.notifyAllCardsChanged();
        Utils.sleepForAWhile(sleepTime);
        if (diceResult != null) {
            manager.notifyRollDices(diceResult);
            Utils.sleepForAWhile(sleepTime);
            revealedCards.forEach(c ->{ board.selectBoardCard(c.getRow(), c.getCol(), true);Utils.sleepForAWhile(sleepTime);});
            Utils.sleepForAWhile(sleepTime);
            try {
                board.revealCards();
                Utils.sleepForAWhile(sleepTime);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        revealedWords.forEach(cardWord-> {
                        cardWord.stream().forEach(c -> {board.selectBoardCard(c.getRow(), c.getCol(), true);Utils.sleepForAWhile(sleepTime);});
                        try {
                            Utils.sleepForAWhile(sleepTime);
                            board.revealWord();
                            Utils.sleepForAWhile(sleepTime);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            System.exit(1);
                        }
                    }
        );
        manager.endPlayerTurn();
    }

    @Override
    public Move clone(){
        Move m = new Move();
        m.diceResult = this.diceResult;
        m.playersData = this.playersData;
        m.currentPlayerIndex =  this.currentPlayerIndex;
        m.revealedCards = this.revealedCards;
        m.revealedWords = this.revealedWords;
        m.board = this.board.clone();
        m.players = this.players.clone();
        return m;
    }
    public void setDiceResult(Integer diceResult) {
        this.diceResult = diceResult;
    }

    public void setPlayersData(List<PlayerData> playersData) {
        this.playersData = playersData;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public void setRevealedCards(List<Card> revealedCards) {
        this.revealedCards = revealedCards;
    }

    public void setManager(GameManager manager) {
        this.manager = manager;
    }

    public void addRevealWordTry(List<Card> selectedCards){
        revealedWords.add(selectedCards);
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Board getBoard(){
        return board;
    }

    public List<PlayerData> getPlayersData() {
        return playersData;
    }

    public Player[] getPlayers(){
        return players;
    }

    public void setPlayers(Player[] players2){
        this.players = players2;
    }
}

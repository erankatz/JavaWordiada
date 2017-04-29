package engine;

import engine.exception.board.BoardException;
import engine.exception.board.CardNotReveledException;
import engine.exception.board.WrongCardPositionException;
import engine.exception.card.CardAlreadyRevealedException;
import engine.exception.card.CardException;
import engine.exception.card.NoCardsLeftToRevealException;
import engine.exception.dice.DiceException;
import engine.exception.dice.DiceNotRolledException;

import java.util.*;

/**
 * Created by eran on 30/03/2017.
 */
public class Player implements java.io.Serializable{
    private Deck deck;
    protected Board board;
    private Dice cube;
    protected GameManager manager;
    private int leftCardNumToReveal;
    private long score;
    private Map<String,Long> composedWords = new HashMap<>();
    private int retriesNumber;

    public Player (GameManager manager,Deck deck,Board board,Dice cube)
    {
        this.manager = manager;
        this.deck = deck;
        this.cube = cube;
        this.board = board;
        leftCardNumToReveal = 0;

    }
    public int rollDice()
    {
        leftCardNumToReveal = cube.role();
        return leftCardNumToReveal;
    }

    public void endTurn() {
        manager.endPlayerTurn();
        cube.endTurn();

    }

    public void revealCard(int row,int col) throws DiceException,CardException,WrongCardPositionException
    {
        if (cube.getResult() == null)
        {
            throw new DiceNotRolledException();
        }
        if (leftCardNumToReveal == 0)
        {
            throw new NoCardsLeftToRevealException();
        }

            Card card = board.getBoardCard(row,col);
            if (!card.isRevealed()){
                card.reveal(); // changing flag to 'reveal'
                leftCardNumToReveal--;
            } else {
                throw new CardAlreadyRevealedException(row,col);
            }
    }

    public boolean revealWordPending(){
        if (this.retriesNumber >0 && !manager.isGameOver()){
            return true;
        }else {
            return false;
        }
    }

    public boolean revealWord(List<Map.Entry<Integer,Integer>> pairs) throws WrongCardPositionException,CardNotReveledException,BoardException {
        boolean ret = board.revealWord(pairs);
        if (ret == true){
            retriesNumber=0;
        } else{
            retriesNumber--;
        }
        return ret;
    }

    protected void increaseScore(long value){
        score+=value;
    }

    protected void addComposedWord(String word, long frequency){
        this.composedWords.put(word,frequency);
    }

    public long getScore() {
        return score;
    }

    public Map<String,Long> getComposedWords(){
        return composedWords;
    }

    public boolean isLeftCardsToReveal() {
        return !(board.getNumOfUnrevealedCard() == 0 || leftCardNumToReveal == 0);
    }

    public int getRetriesNumber(){
        return retriesNumber;
    }

    public void setRetriesNumber(int retriesNumber) {
        this.retriesNumber = retriesNumber;
    }
}
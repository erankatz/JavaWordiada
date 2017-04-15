package engine;

import engine.exception.board.BoardException;
import engine.exception.board.CardNotReveledException;
import engine.exception.board.WrongCardPositionException;
import engine.exception.card.AllCardsRevealedException;
import engine.exception.card.CardAlreadyRevealedException;
import engine.exception.card.CardException;
import engine.exception.card.NoCardsLeftToRevealException;
import engine.exception.deck.DeckException;
import engine.exception.dice.DiceException;
import engine.exception.dice.DiceNotRolledException;

import javax.swing.text.html.parser.Entity;
import java.util.*;

/**
 * Created by eran on 30/03/2017.
 */
public class Player {
    private Deck deck;
    protected Board board;
    private Dice cube;
    private GameManager manager;
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
        if (board.getNumOfUnrevealedCard() == 0)
        {
            throw new AllCardsRevealedException();
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

    public boolean revealWord(List<Map.Entry<Integer,Integer>> pairs) throws DeckException,WrongCardPositionException,CardNotReveledException,BoardException {
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
        boolean ret = !(board.getNumOfUnrevealedCard() == 0 || leftCardNumToReveal == 0);
        if (ret == false)
            this.retriesNumber = manager.getRetriesNumber();
        return ret;
    }

    public int getRetriesNumber(){
        return retriesNumber;
    }

}
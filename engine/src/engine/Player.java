package engine;

import engine.exception.EngineException;
import engine.exception.board.BoardException;
import engine.exception.board.CardNotReveledException;
import engine.exception.board.WrongCardPositionException;
import engine.exception.card.CardAlreadyRevealedException;
import engine.exception.card.CardException;
import engine.exception.card.NoCardsLeftToRevealException;
import engine.exception.dice.DiceException;
import engine.exception.dice.DiceNotRolledException;
import engine.exception.player.ChosenWrongNumberOfCardsException;
import engine.listener.CardRemovedListener;
import engine.listener.EnableAllCardsListener;
import engine.listener.RolledDicesListener;
import javafx.event.Event;

import java.util.*;

/**
 * Created by eran on 30/03/2017.
 */
public class Player implements java.io.Serializable,Cloneable{
    private Deck deck;
    protected Board board;
    protected Dice cube;
    protected GameManager manager;
    protected int leftCardNumToReveal;
    private long score;
    private int id =0;
    private Map<String,Long> composedWords = new HashMap<>();
    protected int retriesNumber;
    private List<RolledDicesListener> rolledDicesListenerListeners = new ArrayList<>();
    private int numberOfWordsRevealed =0;

    public Player (GameManager manager,Deck deck,Board board,Dice cube)
    {
        this.manager = manager;
        this.deck = deck;
        this.cube = cube;
        this.board = board;
        this.leftCardNumToReveal = 0;

    }
    public int rollDice()
    {
        leftCardNumToReveal = cube.role();
        notifyRolledDicesListeners(leftCardNumToReveal);
        manager.notifyRollDicesPendingListener(false);
        manager.notifyRollDices(leftCardNumToReveal);
        manager.notifyRevealCardPendingListener(true);
        return leftCardNumToReveal;
    }

    public void endTurn() {
        manager.endPlayerTurn();
        cube.endTurn();
    }



    public boolean revealWordPending(){
        if (this.retriesNumber >0 && !manager.isGameOver()){
            return true;
        } else {
            return false;
        }
    }

    public boolean revealWord() throws WrongCardPositionException,CardNotReveledException,BoardException {
        boolean ret = board.revealWord();
        if (ret == true){
            retriesNumber=0;
        } else{
            retriesNumber--;
        }
        if (revealWordPending() == false){
            manager.notifyRevealWordPendingListener(false);
            endTurn();
        }
        return ret;
    }

    protected synchronized void increaseScore(long value){
        score+=value;
    }

    protected synchronized void setScore(long value){
        score =value;
    }

    protected synchronized void addComposedWord(String word, long score){
        increaseScore(score);
        numberOfWordsRevealed++;
        if (!composedWords.containsKey(word))
            this.composedWords.put(word,score);
        else {
            score += composedWords.get(word);
            composedWords.remove(word);
            composedWords.put(word,score);
        }
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

    public void registerRolledDicesListener(RolledDicesListener listener ){
        rolledDicesListenerListeners.add(listener);
    }

    private void notifyRolledDicesListeners(int result){
        rolledDicesListenerListeners.forEach(listener->listener.rolldDice(result));
    }

    public void revealCards() throws EngineException,DiceNotRolledException,NoCardsLeftToRevealException,ChosenWrongNumberOfCardsException {
        if (cube.getResult() == null)
        {
            throw new DiceNotRolledException();
        }
        if (leftCardNumToReveal == 0)
        {
            throw new NoCardsLeftToRevealException();
        }
        if (board.getSelectedCardsList().size() != cube.getResult()){
            throw new ChosenWrongNumberOfCardsException(cube.getResult(),board.getSelectedCardsList().size());
        }
        board.revealCards();
        manager.notifyRevealCardPendingListener(false);
        manager.notifyRevealWordPendingListener(true);
    }

    public int getId(){
        return id;
    }
    @Override
    public Player clone(){
        Player pl = new Player(this.manager,this.deck.clone(),this.board.clone(),cube);
        pl.leftCardNumToReveal =this.leftCardNumToReveal;
        pl.score = this.score;
        pl.composedWords = new HashMap<>();
        this.composedWords.entrySet().stream().forEach(pair->pl.composedWords.put(pair.getKey(),pair.getValue()));
        pl.retriesNumber = this.retriesNumber;
        pl.rolledDicesListenerListeners = this.rolledDicesListenerListeners;
        pl.numberOfWordsRevealed = this.numberOfWordsRevealed;
        return pl;
    }

    public int getNumberOfWordsRevealed() {
        return numberOfWordsRevealed;
    }
}
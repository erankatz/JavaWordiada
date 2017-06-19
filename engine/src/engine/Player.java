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
    protected Dice cube;
    protected GameManager manager;
    protected int leftCardNumToReveal;
    private long score;
    private String id;
    private String name;
    private Map<String,WordData> composedWords = new HashMap<>();
    protected int retriesNumber;
    private List<RolledDicesListener> rolledDicesListenerListeners = new ArrayList<>();
    private int numberOfWordsRevealed =0;
    private boolean isQuitFromGame = false;

    public Player (GameManager manager,String id,String name)
    {
        this.manager = manager;
        this.leftCardNumToReveal = 0;
        this.id = id;
        this.name = name;
    }

    public void QuitFromGame(){
        isQuitFromGame = true;
    }

    public boolean getisQuiteFromGame(){
        return isQuitFromGame;
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
        boolean ret = manager.getBoard().revealWord();
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
        if (!composedWords.containsKey(word)){
            WordData wordData = new WordData(score);
            this.composedWords.put(word,wordData);
        }
        else {
            WordData wordData =composedWords.get(word);
            wordData.addWord();
        }
    }

    public long getScore() {
        return score;
    }

    public Map<String,WordData> getComposedWords(){
        return composedWords;
    }

    public boolean isLeftCardsToReveal() {
        return !(manager.getBoard().getNumOfUnrevealedCard() == 0 || leftCardNumToReveal == 0);
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
        if (manager.getBoard().getSelectedCardsList().size() != cube.getResult() && manager.getBoard().getNumOfUnrevealedCard() != manager.getBoard().getSelectedCardsList().size()){
            throw new ChosenWrongNumberOfCardsException(cube.getResult(),manager.getBoard().getSelectedCardsList().size());
        }
        manager.getBoard().revealCards();
        manager.notifyRevealCardPendingListener(false);
        manager.notifyRevealWordPendingListener(true);
    }

    public String getId(){
        return id;
    }
    @Override
    public Player clone(){
        Player pl = new Player(manager,this.id,this.name);
        pl.leftCardNumToReveal =this.leftCardNumToReveal;
        pl.score = this.score;
        pl.composedWords = new HashMap<>();
        this.composedWords.entrySet().stream().forEach(pair->pl.composedWords.put(pair.getKey(),pair.getValue()));
        pl.retriesNumber = this.retriesNumber;
        pl.rolledDicesListenerListeners = this.rolledDicesListenerListeners;
        pl.numberOfWordsRevealed = this.numberOfWordsRevealed;
        pl.deck = this.deck.clone();
        pl.cube = this.cube;
        return pl;
    }

    public int getNumberOfWordsRevealed() {
        return numberOfWordsRevealed;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }


    public void setDice(Dice dice) {
        this.cube = dice;
    }

    public String getName(){
        return this.name;
    }
}
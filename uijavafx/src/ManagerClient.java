import engine.Card;
import engine.PlayerData;
import engine.listener.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManagerClient {
    private List<CardRemovedListener> cardRemovedListeners = new ArrayList<>();
    private List<CardSelectedListener> cardSelectedListeners = new ArrayList<>();
    private List<DisableAllCardsListener> disableAllCardsListeners = new ArrayList<>();
    private List<EnableAllCardsListener> enableAllCardsListeners = new ArrayList<>();
    private List<CardChangedListener> cardChangedListeners = new ArrayList<>();
    private List<WordRevealedListener> wordRevealedListeners = new ArrayList<>();
    private List<RolledDicesListener> RolledDicesListeners = new ArrayList<>();
    private List<PlayerTurnListener> playerTurnListeners = new ArrayList<>();
    private List<LetterFrequencyInDeckListener> letterFrequencyInDeckListeners = new ArrayList<>();
    private List<RevealWordPendingListener> revealWordPendingListeners = new ArrayList<>();
    private List<RevealCardPendingListener> revealCardPendingListeners = new ArrayList<>();
    private List<RollDicesPendingListener> rollDicesPendingListeners = new ArrayList<>();
    private List<GameOverListener> gameOverListeners = new ArrayList<>();
    private List<PlayerDataChangedListener> playerDataChangedListeners = new ArrayList<>();

    public void registerDisableAllCardsListener(DisableAllCardsListener listener){
        disableAllCardsListeners.add(listener);
    }

    public void registerEnableAllCardsListener(EnableAllCardsListener listener){
        enableAllCardsListeners.add(listener);
    }

    public void registerCardSelectedListener(CardSelectedListener listener){
        cardSelectedListeners.add(listener);
    }

    public void registerCardChangedListener(CardChangedListener listener){
        cardChangedListeners.add(listener);
    }

    public void registerCardRemovedListener(CardRemovedListener listener ){
        cardRemovedListeners.add(listener);
    }

    public void registerRollDices(RolledDicesListener listener ){
        RolledDicesListeners.add(listener);
    }


    public void registerPlayerTurn(PlayerTurnListener listener ){
        playerTurnListeners.add(listener);
    }

    public void registerRevealWordPendingListener(RevealWordPendingListener listener ){
        revealWordPendingListeners.add(listener);
    }

    public void registerRevealCardPendingListener(RevealCardPendingListener listener ){
        revealCardPendingListeners.add(listener);
    }

    public void registerRollDicesPendingListeners(RollDicesPendingListener listener){
        rollDicesPendingListeners.add(listener);
    }

    public void registerGameOverListener(GameOverListener listener){
        gameOverListeners.add(listener);
    }


    public void registerPlayerDataChangedListener(PlayerDataChangedListener listener){
        playerDataChangedListeners.add(listener);
    }

    public void notifyRollDices(int result){
        RolledDicesListeners.forEach(listener->listener.rolldDice(result));
    }
    public void notifyDisableAllCardsListeners(){
        disableAllCardsListeners.forEach(listener->listener.disableAllCards());
    }

    public void notifyLetterFrequencyInDeckListeners(Map<Character,Long> frequency){
        letterFrequencyInDeckListeners.forEach(listener->listener.LetterFrequencyInDeck(frequency));
    }


    public void notifyRevealWordPendingListener(boolean isPending){
        revealWordPendingListeners.forEach(listener->listener.isWordPendingToBeRevealed(isPending));
    }

    public void notifyRevealCardPendingListener(boolean isPending){
        revealCardPendingListeners.forEach(listener->listener.isCardPendingToBeRevealed(isPending));
    }


    public void notifyRollDicesPendingListener(boolean isPending){
        rollDicesPendingListeners.forEach(listener->listener.isRollDicesToBeRevealed(isPending));
    }

    public void notifyPlayerTurnListeners(int playerId){
        playerTurnListeners.forEach(listener->listener.playerTurn(playerId));
    }

    public void notifyEnableAllCardsListeners(){
        enableAllCardsListeners.forEach(listener->listener.enableAllCards());
    }

    public void notifyCardSelectedListeners(int row,int col){
        cardSelectedListeners.forEach(listener->listener.selectCard(row,col));
    }

    public void notifyCardRemovedListeners(int row,int col){
        cardRemovedListeners.forEach(listener->listener.removeCard(row,col));
    }

    public void notifyCardChangedListener(Card card){
        cardChangedListeners.forEach(listener->listener.cardChanged(card));
    }

    public void registerWordRevealedListener(WordRevealedListener listener ){
        wordRevealedListeners.add(listener);
    }

    public void registerLetterFrequencyInDeckListener(LetterFrequencyInDeckListener listener ){
        letterFrequencyInDeckListeners.add(listener);
    }

    public void notifyWordRevealedListeners(String word,int score){
        wordRevealedListeners.forEach(listener->listener.PrintResultOfWordRevealed(word,score));
    }

    public void notifyGameOverListeners(int id){
        gameOverListeners.forEach(listener->listener.gameOver(id));
    }

    public void notifyPlayerDataChangedListener(PlayerData score){
        playerDataChangedListeners.forEach(listener->listener.updateScore(score));
    }

    private void checkLoginStatus(){

    }

    private void updatePlayersDetails(){

    }

    private void gameStatus(){

    }


}

import engine.Card;
import engine.Player;
import engine.PlayerData;
import engine.listener.*;
import jsonObjectResponse.games.CardData;
import org.json.JSONArray;
import org.json.JSONObject;
import static java.util.concurrent.TimeUnit.*;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

public class ManagerScheduler {
    Timer timer = new Timer();
    private String url;
    private int gameId;
    private String userName;
    private String UserNameTurn;
    private static final long interval  = 5000;
    private List<CardRemovedListener> cardRemovedListeners = new ArrayList<>();
    private List<CardSelectedListener> cardSelectedListeners = new ArrayList<>();
    private List<DisableAllCardsListener> disableAllCardsListeners = new ArrayList<>();
    private List<EnableAllCardsListener> enableAllCardsListeners = new ArrayList<>();
    private List<Consumer<CardData>> cardChangedListeners = new ArrayList<>();
    private List<WordRevealedListener> wordRevealedListeners = new ArrayList<>();
    private List<RolledDicesListener> RolledDicesListeners = new ArrayList<>();
    private List<PlayerTurnListener> playerTurnListeners = new ArrayList<>();
    private List<LetterFrequencyInDeckListener> letterFrequencyInDeckListeners = new ArrayList<>();
    private List<RevealWordPendingListener> revealWordPendingListeners = new ArrayList<>();
    private List<RevealCardPendingListener> revealCardPendingListeners = new ArrayList<>();
    private List<RollDicesPendingListener> rollDicesPendingListeners = new ArrayList<>();
    private List<GameOverListener> gameOverListeners = new ArrayList<>();
    private List<PlayerDataChangedListener> playerDataChangedListeners = new ArrayList<>();
    private List<Consumer<Integer>> registeredPlayersConsumer = new ArrayList<>();
    private List<Consumer<String>> registerGameStatusConsumer = new ArrayList<>();
    private List<Consumer<String>> registerPlayerTurnConsumerName = new ArrayList<>();
    private List<Consumer<Integer>> registerRoundNumberConsumers = new ArrayList<>();


    public void setUrl(String url){
        this.url = url;
    }

    public void setGameId(int gameID){
        this.gameId = gameID;
    }

    public void setUser(String user){
        this.userName = user;
    }
    public void registerDisableAllCardsListener(DisableAllCardsListener listener){
        disableAllCardsListeners.add(listener);
    }

    public void registerEnableAllCardsListener(EnableAllCardsListener listener){
        enableAllCardsListeners.add(listener);
    }

    public void registerCardSelectedListener(CardSelectedListener listener){
        cardSelectedListeners.add(listener);
    }

    public void registerCardChangedListener(Consumer<CardData> listener){
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

    public void registerRegisteredPlayersListener(Consumer<Integer> listener){

        registeredPlayersConsumer.add(listener);
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

    public void notifyLetterFrequencyInDeckListeners(String frequency){
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

    public void notifyPlayerTurnListeners(String playerName){
        playerTurnListeners.forEach(listener->listener.playerTurn(playerName));
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

    public void notifyCardChangedListener(CardData card){
        cardChangedListeners.forEach(listener->listener.accept(card));
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

    public void notifyRoundNumberConsumer(int roundnumber){
        registerRoundNumberConsumers.forEach(listener->listener.accept(roundnumber));
    }

    public void notifyGameOverListeners(int id){
        gameOverListeners.forEach(listener->listener.gameOver(id));
    }

    public void notifyPlayerDataChangedListener(PlayerData score){
        playerDataChangedListeners.forEach(listener->listener.updateScore(score));
    }
    public void notifyRegisteredPlayersListener(int registeredPlayers){
        if (registeredPlayersConsumer != null)
            registeredPlayersConsumer.forEach(listener->listener.accept(registeredPlayers));
    }

    private void checkLoginStatus(){

    }

    private void    updatePlayersDetails(){
        String str =Utils.makeGetJsonRequest(url+ "games?action=gamePlayers&key=" + gameId + "&user=" +userName);
        JSONArray jObj = new JSONArray(str);
        notifyRegisteredPlayersListener(jObj.length());
        for (int i=0;i<jObj.length();i++){
            String i2String = new Integer(i).toString();
            PlayerData playerData = new PlayerData(jObj.getJSONObject(i).getString("type"),
                    i2String,
                    jObj.getJSONObject(i).getString("name"),
                    jObj.getJSONObject(i).getLong("score"),
                    i);
            notifyPlayerDataChangedListener(playerData);
        }
    }

    private void gameStatus(){
        String str = Utils.makeGetJsonRequest(url + "games?action=gameStatus&key=" + gameId+ "&user=" +userName);
        JSONObject jObj = new JSONObject(str);
        notifyGameStatusConsumer(jObj.getString("status"));

        switch (jObj.getString("status")){
            case "WaitingForPlayers":
                disableControls();
                break;
            case "Running":
                notifyPlayerTurnConsumerName(jObj.getString("currentPlayerTurnName"));
                updateGamePage();
                if (UserNameTurn.equals(userName)){
                    notifyEnableAllCardsListeners();
                    notifyRollDicesPendingListener(true);
                }
                break;
            case "Finished":
                disableControls();
        }
    }

    public void updateGamePage() {
        String str = Utils.makeGetJsonRequest(url + "games?action=pageDetails&key=" + gameId+ "&user=" + userName);
        JSONObject jObj = new JSONObject(str);
        notifyLetterFrequencyInDeckListeners(jObj.getString("charFrequencyString"));
        JSONArray board = jObj.getJSONObject("board").getJSONArray("cards");
        for (int i=0;i<board.length();i++){
            for (int j =0;j<board.length();j++){
                CardData card = new CardData(board.getJSONArray(i).getJSONObject(j).getString("letter").charAt(0),
                                board.getJSONArray(i).getJSONObject(j).getBoolean("isSelected"),
                        board.getJSONArray(i).getJSONObject(j).getInt("row"),
                                board.getJSONArray(i).getJSONObject(j).getInt("col"),
                        board.getJSONArray(i).getJSONObject(j).getBoolean("revealed"));
                notifyCardChangedListener(card);
            }
        }
        notifyRoundNumberConsumer(jObj.getInt("move"));
        UserNameTurn = jObj.getJSONArray("playersDetails").getJSONObject(jObj.getInt("turn")-1).getString("name");
        notifyPlayerTurnListeners(UserNameTurn);
    }

    private void disableControls(){
        notifyDisableAllCardsListeners();
        notifyRevealCardPendingListener(false);
        notifyRollDicesPendingListener(false);
    }

    private void startTurn(){
        notifyEnableAllCardsListeners();
        notifyRollDicesPendingListener(true);
    }
    public void activateTimers() {
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                checkLoginStatus();
                updatePlayersDetails();
                gameStatus();
            }
        };

        Utils.sleepForAWhile(2);
        checkLoginStatus();
        updatePlayersDetails();
        gameStatus();
        timer.scheduleAtFixedRate(task1, interval,interval);
    }

    public void registerGamestatusConsumer(Consumer<String> consumer){
        registerGameStatusConsumer.add(consumer);
    }

    public void notifyGameStatusConsumer(String msg){
        registerGameStatusConsumer.forEach(pl->pl.accept(msg));
    }

    public void notifyPlayerTurnConsumerName(String name){
        registerPlayerTurnConsumerName.forEach(pl->pl.accept(name));
    }

    public void registerPlayerTurnConsumerName(Consumer<String> consumer){
        registerPlayerTurnConsumerName.add(consumer);
    }

    public void registerRoundNumberConsumer(Consumer<Integer> consumer){
        registerRoundNumberConsumers.add(consumer);
    }
}

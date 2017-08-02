import engine.*;
import engine.exception.EngineException;
import engine.exception.board.BoardSizeOutOfRangeException;
import engine.exception.board.NotEnoughCardsToFillBoardException;
import engine.exception.board.WrongCardPositionException;
import engine.exception.dice.DiceException;
import engine.exception.dice.WrongNumberOfDiceFacetException;
import engine.exception.file.FileExtensionException;
import engine.exception.letter.LetterException;
import engine.listener.*;
import javafx.scene.image.WritableImage;
import jsonObjectResponse.login.UserLogin;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.lang.reflect.Executable;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by eran on 29/05/2017.
 */
public class GameModel {
    private ManagerClient manager = new ManagerClient();
    private char[][] board;
    private String userName;
    private Boolean isComputer;
    private int gameKey;
    private String creatorName;
    private String gameName;
    private int boardSize;
    private int roundNumber;
    private int score;
    private int turn;
    private boolean isGameOver;
    private String lettersFrequencyInDeckStrings;
    private String lowestFrequencyDictionaryWordsStrings;
    private int registeredPlayers;
    private int requiredPlayers;
    private boolean isGoldFishMode;
    private String scoreMode;
    private Consumer<Map.Entry<Integer,Integer>> cardRemoved;
    private Consumer<Map.Entry<Integer,Integer>> cardSelected;
    private Consumer<Boolean> isDisabledAllCards;
    private Consumer<Map.Entry<Integer,Integer>> rolledDicesResult2RetriesLeft;
    private Consumer<Card> updateCard;
    private Consumer<String> letterFrequencyInDeck;
    private Consumer<Integer> playerTurn;
    private Consumer<Map.Entry<String,Integer>> wordRevealedWord2Score;
    private Consumer<Boolean> isRealedWordPending;
    private Consumer<Boolean> isReaveledCardPending;
    private Consumer<Boolean> isRolledDicesPending;
    private Consumer<Integer> gameOverConsumer;
    private Consumer<PlayerData> updatePlayerScoreConsumer;
    private Consumer<Boolean> isFileLoadedSuccefullyConsumer;
    private Consumer<String> exceptionMessageConsumer;
    private int gameId = 1;
    private String url= "http://localhost:8080/wordiada/";
    private int retriesLeft;
    /*public void readXmlFile(File file) {
        manager = new ManagerClient();
        manager.registerEnableAllCardsListener(()->isDisabledAllCards.accept(false));
        manager.registerDisableAllCardsListener(()->isDisabledAllCards.accept(true));
        manager.registerCardChangedListener((Card c)->updateCard.accept(c));
        manager.registerCardSelectedListener(
                (int row,int col) -> cardSelected.accept(new  AbstractMap.SimpleEntry(row,col))
        );
        manager.registerCardRemovedListener((row,col)->cardRemoved.accept(new AbstractMap.SimpleEntry(row,col)));
        manager.registerRollDices((result) -> rolledDicesResult2RetriesLeft.accept(new AbstractMap.SimpleEntry(result,0)));
        //manager.registerLetterFrequencyInDeckListener((map) ->letterFrequencyInDeck.accept(map));
        manager.registerPlayerTurn((playerId -> playerTurn.accept(playerId)));
        manager.registerWordRevealedListener((word,score)->wordRevealedWord2Score.accept(new AbstractMap.SimpleEntry(word,score)));
        manager.registerRevealWordPendingListener((isPending) ->isRealedWordPending.accept(isPending));
        manager.registerRevealCardPendingListener((isPending) ->isReaveledCardPending.accept(isPending));
        manager.registerRollDicesPendingListeners((isPending) ->isRolledDicesPending.accept(isPending));
        manager.registerGameOverListener((id)->gameOverConsumer.accept(id));
        manager.registerPlayerDataChangedListener(pl->updatePlayerScoreConsumer.accept(pl));
        try {
            FileInputStream inp = new FileInputStream(file);
            manager.readXmlFile(readStream(inp),readStream(inp));
            newGame();
            isFileLoadedSuccefullyConsumer.accept(true);
        } catch (IOException ex){
            exceptionMessageConsumer.accept(ex.getMessage());
        } catch (EngineException ex)
        {
            exceptionMessageConsumer.accept(ex.getMessage());
        } catch (XPathExpressionException ex){
            exceptionMessageConsumer.accept(ex.getMessage());
        }
    }*/

    public List<String> getGamesList(){
        List<String> ret = new ArrayList<>();
        String jsonStr = Utils.makeGetJsonRequest(url + "games?action=gameList");
        JSONObject obj = new JSONObject(jsonStr);
        JSONArray games =  obj.getJSONArray("games");
        for (int i=0;i<games.length();i++){
            JSONObject game = (JSONObject)games.get(i);
            ret.add(game.getString("gameTitle"));
        }
        return ret;
    }

    public boolean login(String username, boolean isComputer){
        UserLogin jsonObj = new UserLogin(username,isComputer);
        String jsonStr = Utils.makeGetJsonRequest(jsonObj.getLoginUrl());
        JSONObject obj = new JSONObject(jsonStr);
        this.userName = username;
        this.isComputer = isComputer;
        return obj.getBoolean("isConnected");
    }

    public boolean joinGame(String gameTitle){
        String jsonStr = Utils.makeGetJsonRequest(url +"games?action=joinGame&user=" + userName + "&isComputer=" + isComputer + "&gameId=1" );
        JSONObject jsonObj = new JSONObject(jsonStr);
        if (jsonObj.getBoolean("isLoaded")){
            loadGameDetails();
            return true;
        } else {
            exceptionMessageConsumer.accept(jsonObj.getString("errorMessage"));
            return false;
        }
    }

    private void loadGameDetails(){
        String jsonStr = Utils.makeGetJsonRequest(url + "games?action=gameDetails&gameId=" + gameId);
        JSONObject jsonObj = new JSONObject(jsonStr);
        gameKey = jsonObj.getInt("key");
        creatorName = jsonObj.getString("creatorName");
        gameName = jsonObj.getString("gameTitle");
        boardSize = jsonObj.getInt("rows");
        lettersFrequencyInDeckStrings = jsonObj.getString("lettersFrequencyInDeck");
        lowestFrequencyDictionaryWordsStrings = jsonObj.getString("lowestFrequencyDictionaryWords");
        registeredPlayers = jsonObj.getInt("registeredPlayers");
        requiredPlayers = jsonObj.getInt("requiredPlayers");
        isGoldFishMode = jsonObj.getBoolean("isGoldFishMode");
        scoreMode = jsonObj.getString("scoreMode");
    }

    public void selectCard(int row,int col){
        //manager.getBoard().selectBoardCard(row, col,true);
        JSONObject obj = new JSONObject();
        obj.append("action","selectCard");
        obj.append("key",gameId);
        obj.append("row",row);
        obj.append("col",col);

        Utils.makePostJsonRequest(obj.toString(),url +"games");
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

    public void setIsFileLoadedSuccefullyConsumer(Consumer<Boolean> consumer){
        isFileLoadedSuccefullyConsumer =consumer;
    }

    //public void playPrevMove(int index){
    //   new Thread(()->manager.playMove(index)).start();
    //   //manager.playPrevMove();
    //}

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
        return retriesLeft;
    }

    public void setLetterFrequencyInDeckConsumer(Consumer<String> listenerConsumer){
        letterFrequencyInDeck = listenerConsumer;
    }

    public void setPlayerTurnConsumer(Consumer<Integer> listenerConsumer){
        playerTurn = listenerConsumer;
    }

    //public boolean getIsEnabledCardConsumer(int row, int col) {
    //    try{
    //        return board.getBoardCard(row,col).getIsEnabled();
    //    } catch (Exception ex){
    //        return false;
    //    }
    //}

    //public void newGame() throws DiceException,IOException{
    //    manager.newGame();
    //    board = manager.getBoard();
    //}

    public int rollDice() {
        String jsonStr =Utils.makeGetJsonRequest(url + "games?/action=rollDice&key=" +gameId );
        JSONObject jsonObj = new JSONObject(jsonStr);
        return jsonObj.getInt("result");
        //return manager.getPlayers()[manager.getCurrentPlayerTurn()].rollDice();
    }

    public char getCardLetter(int row, int col) {
        try{
            return board[row-1][col-1];
        } catch (Exception ex){
            exceptionMessageConsumer.accept(ex.getMessage());
        }
        return '*';
    }

    public int getBoardSize() {
        return boardSize;
    }


    public void revealCards() {
        try{
            String str =Utils.makeGetJsonRequest(url+ "games?action=revealCards&key=" +gameId);
            JSONObject jsonObj = new JSONObject(str);
            if (jsonObj.getBoolean("isSuccess")){
                updateGamePage();
            } else {
                if (jsonObj.getString("currentPlayerMsg") != null){
                    exceptionMessageConsumer.accept(jsonObj.getString("currentPlayerMsg"));
                }
            }
        } catch (Exception ex){
            exceptionMessageConsumer.accept(ex.getMessage());
        }
    }

    private void updateGamePage(){
        String str =Utils.makeGetJsonRequest(url+ "games?action=pageDetails&key=" +gameId);
        JSONObject jsonObj = new JSONObject(str);


    }

    public void revealWord(){
        try{
            String str= Utils.makeGetJsonRequest(url+ "game?action=CheckSelectedWord&key=" +gameId);
            JSONObject jObject = new JSONObject(str);
            if (jObject.getBoolean("isValidWord")){
                retriesLeft = jObject.getInt("numOfRetriesLeft");
                wordRevealedWord2Score.accept(new AbstractMap.SimpleEntry<String, Integer>(jObject.getString("word"),jObject.getInt("score")));
            }
        } catch (Exception ex){
            exceptionMessageConsumer.accept(ex.getMessage());
        }
    }

    //public int getNumOfCardInDeck() {
    //    return manager.getNumOfCardInDeck();
    //}

    public boolean getIsGoldFish() {
        return isGoldFishMode;
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



/*    public void startGame()
    {
        if (manager != null && !manager.isGameStarted())
        {
            if (manager.isGameOver())
            {
                try {
                    manager.createDictionary();
                    manager.newGame();
                } catch (EngineException ex){
                    exceptionMessageConsumer.accept(ex.getMessage());
                } catch (IOException ex){
                    System.out.println(ex.getMessage());
                }
            }
            manager.startGame();
        } else {
            exceptionMessageConsumer.accept("The Game Not Loaded or already started");
        }
    }*/

    public boolean isComputerPlayerPlays(){
        return false;
        //TODO
        //return manager.getPlayers()[manager.getCurrentPlayerTurn()] instanceof ComputerPlayer;
    }

    public void clearCardSelection() {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.append("action","clearCardSelection");
        jsonRequest.append("key",gameId);
        Utils.makePostJsonRequest(jsonRequest.toString(),url);
        updateGamePage();
    }

    public String getLowestFrequencyDictionaryWords(){
        return lowestFrequencyDictionaryWordsStrings;

    }

    public List<PlayerData> getPlayersData(){
        List<PlayerData> playerData = new ArrayList<>();
        String respondStr = Utils.makeGetJsonRequest(url + "games?action=gamePlayers&key="+gameId);
        JSONObject jsonObject = new JSONObject(respondStr);
        for (int i=0;i<jsonObject.getInt("length");i++){
            //jsonObject.getJSONArray()
        }
        //TODO
        return playerData;
        //return manager.getPlayersData();
    }

    public void quitGame() {
        //TODO
        //if (!isComputerPlayerPlays())
        //    manager.playerQuit();
    }

    public String getCurrentPlayerStatus() {

        //TODO

        return "";
    }

    public int getTotalNumOfTurnsElapsed(){
        return roundNumber;
    }

//    public int getCurrentNumofTurnsElapsed(){
 //       return manager.getCurrentNumOfTurnsElapsed();
 //   }

    public boolean getIsReplayMode(){
        return false;
    }

    public boolean isGameOver()
    {
        return isGameOver;
    }

    public String getScoreMode(){
        return scoreMode;
    }


    public void setExceptionMessageConsumer(Consumer<String> exceptionMessageConsumer) {
        this.exceptionMessageConsumer = exceptionMessageConsumer;
    }

    public void updateCards() {
        //manager.updateCards();
    }

    private static String readStream(InputStream is) {
        StringBuilder sb = new StringBuilder(512);
        try {
            Reader r = new InputStreamReader(is, "UTF-8");
            int c = 0;
            while ((c = r.read()) != -1) {
                sb.append((char) c);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public String getUser() {
        return userName;
    }

    public boolean isComputerUser() {
        return isComputer;
    }

    public int getGameKey() {
        return gameKey;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getGameName() {
        return gameName;
    }
}

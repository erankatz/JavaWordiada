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
    private GameManager manager;
    private Board board;
    private String userName;
    private Boolean isComputer;
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
    private Consumer<Boolean> isFileLoadedSuccefullyConsumer;
    private Consumer<String> exceptionMessageConsumer;


    public void readXmlFile(File file) {
        manager = new GameManager();
        manager.registerEnableAllCardsListener(()->isDisabledAllCards.accept(false));
        manager.registerDisableAllCardsListener(()->isDisabledAllCards.accept(true));
        manager.registerCardChangedListener((Card c)->updateCard.accept(c));
        manager.registerCardSelectedListener(
                (int row,int col) -> cardSelected.accept(new  AbstractMap.SimpleEntry(row,col))
        );
        manager.registerCardRemovedListener((row,col)->cardRemoved.accept(new AbstractMap.SimpleEntry(row,col)));
        manager.registerRollDices((result) -> rolledDicesResult2RetriesLeft.accept(new AbstractMap.SimpleEntry(result,0)));
        manager.registerLetterFrequencyInDeckListener((map) ->letterFrequencyInDeck.accept(map));
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
    }

    public List<String> getGamesList(){
        List<String> ret = new ArrayList<>();
        String jsonStr = Utils.makeGetJsonRequest("http://localhost:8080/wordiada/games?action=gameList");
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

    public void joinGame(String gameTitle){
        String jsonStr = Utils.makeGetJsonRequest("http://localhost:8080/wordiada/games?action=joinGame&userName=" + userName + "&iscomputer=" + isComputer + "gameId=1" );

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

    public void setIsFileLoadedSuccefullyConsumer(Consumer<Boolean> consumer){
        isFileLoadedSuccefullyConsumer =consumer;
    }

    public void playPrevMove(int index){
       new Thread(()->manager.playMove(index)).start();
       //manager.playPrevMove();
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
        manager.newGame();
        board = manager.getBoard();
    }

    public int rollDice() {
        return manager.getPlayers()[manager.getCurrentPlayerTurn()].rollDice();
    }

    public char getCardLetter(int row, int col) {
        try{
            return board.getBoardCard(row,col).getLetter();
        } catch (Exception ex){
            exceptionMessageConsumer.accept(ex.getMessage());
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
            exceptionMessageConsumer.accept(ex.getMessage());
        }
    }

    public void revealWord(){
        try{
            manager.getPlayers()[manager.getCurrentPlayerTurn()].revealWord();
        } catch (EngineException ex){
            exceptionMessageConsumer.accept(ex.getMessage());
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



    public void startGame()
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
        if (!isComputerPlayerPlays())
            manager.playerQuit();
    }

    public String getCurrentPlayerStatus() {
        StringBuilder ret = new StringBuilder();
        Player playerPtr = manager.getPlayers()[manager.getCurrentPlayerTurn()];
        Map<String,WordData> composedWords = playerPtr.getComposedWords();
        long score = playerPtr.getScore();
        ret.append(String.format("Player %s composed %d words, scored %d  \n",
                playerPtr.getId(),playerPtr.getNumberOfWordsRevealed(),score));
        if (composedWords.entrySet().size() != 0){
            composedWords.entrySet()
                    .forEach(e1->ret.append(String.format("%s : Total scored %d (composed %d times)\n",e1.getKey(),e1.getValue().getScore(),e1.getValue().getNumberOfWords())));
        }
        return ret.toString();
    }

    public int getTotalNumOfTurnsElapsed(){
        return manager.getTotalNumberofTurnsElapses();
    }

    public int getCurrentNumofTurnsElapsed(){
        return manager.getCurrentNumOfTurnsElapsed();
    }

    public boolean getIsReplayMode(){
        return manager.getIsReplayMode();
    }

    public boolean isGameOver()
    {
        if (manager == null)
            return false;
        return manager.isGameOver();
    }

    public String getScoreMode(){
        if (manager.getScoreMode() == EnumScoreMode.WORDCOUNT){
            return  "Word Count";
        } else {
            return "Word Score";
        }
    }


    public void setExceptionMessageConsumer(Consumer<String> exceptionMessageConsumer) {
        this.exceptionMessageConsumer = exceptionMessageConsumer;
    }

    public void updateCards() {
        manager.updateCards();
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
}

package engine.controller;

import engine.*;
import engine.chat.ChatManager;
import engine.chat.SingleChatEntry;
import engine.exception.EngineException;
import engine.message.DiceResultMessage;
import engine.message.IGameMessage;
import engine.message.RevealCardMessage;
import engine.message.RevealedWordMessage;
import javafx.util.Pair;

import javax.xml.bind.JAXBException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Created by user on 12/10/2016.
 */
public class GameController
{
    private transient GameManager gameLogic;
    private GameStatus status;
    private transient ArrayList<Player> players;
    private int registeredPlayers;
    private int key;

    private int requiredPlayers;
    private String gameTitle;
    private IGameMessage userMessage;
    //Gson Uses on game loaded
    private String dictName;
    private int numOfChars;
    private int rows;
    private int cols;
    private String creatorName;
    private boolean isGoldFishMode;
    private EnumScoreMode scoreMode;
    private String lettersFrequencyInDeck;
    private String lowestFrequencyDictionaryWords;
    private transient engine.chat.ChatManager chatManager;
    //  private int[][] rowBlocks;
  //  private int[][] colBlocks;
    int idToGive;
    int chatVersion =1;

    public GameController()
    {
        idToGive = 1;
        gameLogic = new GameManager();
        status = GameStatus.Building;
        chatManager = new ChatManager();
    }

    public String getGameTitle()
    {
        return gameTitle;
    }

    public String initGame(String xmlDescription,String dictionaryContent, String creator) throws EngineException, XPathExpressionException,java.io.IOException
    {
        creatorName = creator;
        gameLogic.readXmlFile(xmlDescription,dictionaryContent);
        gameLogic.newGame();
        isGoldFishMode = gameLogic.getIsGoldFishMode();
        scoreMode = gameLogic.getScoreMode();
        lettersFrequencyInDeck = getCharFrequencyString();
        lowestFrequencyDictionaryWords = gameLogic.getBoard().getLowestFrequencyDictionaryWords();
        gameTitle = gameLogic.getGameTitle();
        players = new ArrayList<>();
        status = GameStatus.WaitingForPlayers;
        registeredPlayers++;
        initControllerData();
        //return gameLogic.getGameTitle();
        return  gameLogic.getGameTitle();
    }

    public void sendMessage(String chatString,String username){
        chatManager.addChatString(chatString,username);
        chatVersion++;
    }
    public String getCharFrequencyString(){
        AtomicReference<String> ret = new AtomicReference<>();
        ret.set("");
        gameLogic.getCharFrequency()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e1 -> ret.getAndSet(ret.get() + printCharFrequencyToTextBox(e1, gameLogic.getNumOfCardInDeck())))
                .collect(Collectors.joining());
        return ret.get();
    }

    private String printCharFrequencyToTextBox(Map.Entry<Character,Long> ch2Freq,int NumOfCardInDeck){
        return String.format("%c - %d/%d\n",ch2Freq.getKey(),ch2Freq.getValue(), NumOfCardInDeck);
    }
    private void initControllerData()
    {
        registeredPlayers = 0;
        requiredPlayers = gameLogic.getNumOfRequiredPlayers();
        gameTitle = gameLogic.getGameTitle();
        rows = gameLogic.getBoard().getBoardSize();
        cols = gameLogic.getBoard().getBoardSize();
        dictName = gameLogic.getDictName();
        numOfChars = gameLogic.getNumOfChars();
    }

    public int getKey()
    {
        return key;
    }

    public void setKey(int key)
    {
        this.key = key;
    }

    public GameStatus getStatus()
    {
        return status;
    }

    public void addPlayer(String userName, boolean isComputer)
    {
        Player player;
        if (isComputer){
            player = new ComputerPlayer(gameLogic,(new Integer(idToGive)).toString(),userName);
        } else {
            player = new Player(gameLogic,"0",userName);
        }
        gameLogic.addPlayer(player);
        players.add(player);
        idToGive++;
        registeredPlayers++;

        if (registeredPlayers == requiredPlayers)
        {
            status = GameStatus.Running;
        }
    }

    public GameStatus getGameStatus(){
        return status;
    }


    public boolean hasPlayerWithName(String name)
    {
        for (Player player : players)
        {
            if (player.getName().contentEquals(name))
            {
                return true;
            }
        }
        return false;
    }

    public List<PlayerData> getPlayersDetails() {
        return gameLogic.getPlayersData();
    }

    public void playerLeave(String userName) {
        if (gameLogic.playerLeave(userName)) {
            registeredPlayers--;
            if (registeredPlayers == 0 && (status.equals(GameStatus.Running) || status.equals(GameStatus.Finished))) {
                try {
                    gameLogic.newGame();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                status = GameStatus.WaitingForPlayers;
            } else if (registeredPlayers == 1 && requiredPlayers > 1 && status.equals(GameStatus.Running)) {
                status = GameStatus.Finished;
            }
        }
    }

    public String getOtherPlayerMessage(){
        if (userMessage != null)
            return userMessage.getOtherPlayerMessage();
        else return null;
    }

    public void setUserMessage(IGameMessage message){
        this.userMessage = message;
    }

    public int getNumRegisteredPlayers(){
        return registeredPlayers;
    }

    public boolean isReplayMode() {
        return gameLogic.getIsReplayMode();
    }

    public String getCurrentPlayerName() {
        return gameLogic.getPlayers()[gameLogic.getCurrentPlayerTurn()].getName();
    }

    public DiceResultMessage rollDice() {
        userMessage = new DiceResultMessage(gameLogic.getPlayers()[gameLogic.getCurrentPlayerTurn()].rollDice(),getCurrentPlayerName());
        return (DiceResultMessage) userMessage;

    }

    public void selectCard(int row, int col) {
            gameLogic.getBoard().selectBoardCard(row, col,true);
    }

    public Player getPlayer(String userName) {
        return  gameLogic.getPlayer(userName);
    }

    public int getRoundNumber() {
        return gameLogic.getCurrentNumOfTurnsElapsed();
    }

    public int getCurrentTurn() {
        return gameLogic.getCurrentPlayerTurn() +1;
    }

    public Board getBoard() {
        return gameLogic.getBoard();
    }

    public void clearCardSelection() {
        gameLogic.getBoard().clearSelectedCards();
    }

    public RevealCardMessage revealCards() {
        boolean isSuccess = true;
        String currentPlayerMsg = null;
        try{
            gameLogic.getPlayers()[gameLogic.getCurrentPlayerTurn()].revealCards();
        } catch (Exception ex){
            isSuccess = false;
            currentPlayerMsg = ex.getMessage();
        }
        userMessage = new RevealCardMessage(isSuccess,currentPlayerMsg,getCurrentPlayerName());
        return (RevealCardMessage)userMessage;
    }

    public RevealedWordMessage checkSelectedWord() {
        int numOfRetriesLeft;
        String  currentPlayerMessage =null;
        boolean isValidWord =false;
        String word ="";
        try{
            word= getBoard().getSelectedWord();
            isValidWord = gameLogic.getPlayers()[gameLogic.getCurrentPlayerTurn()].revealWord();
        } catch (EngineException ex){
            currentPlayerMessage = ex.getMessage();
        }
        numOfRetriesLeft = gameLogic.getPlayers()[gameLogic.getCurrentPlayerTurn()].getRetriesNumber();
        userMessage = new RevealedWordMessage(numOfRetriesLeft,currentPlayerMessage,isValidWord,gameLogic.getLastReavledWordScore(),word,getCurrentPlayerName());
        return (RevealedWordMessage)userMessage;
    }

    public long getHighestScore() {
        return gameLogic.getPlayers()[gameLogic.getWinnerPlayer()].getScore();
    }

    public ArrayList<Player> getWinners() {
        return players;
    }

    public EnumPlayerTurnPendingAction getcurrentPlayerPendingAction() {
        return gameLogic.getPlayers()[gameLogic.getCurrentPlayerTurn()].getPendingAction();
    }

    public void startGame() {
        gameLogic.startGame();
    }

    public void refreshDeck() {
        gameLogic.getBoard().getLowestFrequencyDictionaryWords();
    }

    public int getChatVersion(){
        return chatVersion;
    }

    public List<SingleChatEntry> getChatEntries(int fromIndex){
        return chatManager.getChatEntries(fromIndex);
    }
}

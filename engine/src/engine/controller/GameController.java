package engine.controller;

import engine.*;
import engine.exception.EngineException;
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

    //  private int[][] rowBlocks;
  //  private int[][] colBlocks;
    int idToGive;

    public GameController()
    {
        idToGive = 1;
        gameLogic = new GameManager();
        status = GameStatus.Building;
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

    private String getCharFrequencyString(){
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
            gameLogic.startGame();
        }
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
        gameLogic.playerLeave(userName);
        registeredPlayers--;
        if (registeredPlayers ==0 && gameLogic.getIsReplayMode()){
            try {
                gameLogic.newGame();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
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

    public int rollDice() {
        return gameLogic.getPlayers()[gameLogic.getCurrentPlayerTurn()].rollDice();
    }
}

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

/**
 * Created by user on 12/10/2016.
 */
public class GameController
{
    private transient GameManager gameLogic;
    private GameStatus status;
    private ArrayList<Player> players;
    private String creatorName;
    private int registeredPlayers;
    private int key;
    private int rows;
    private int cols;
    private int requiredPlayers;
    private String gameTitle;
    private String dictName;
    private int numOfChars;
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

    public String initGame(String xmlDescription, String creator) throws EngineException, XPathExpressionException,java.io.IOException
    {
        creatorName = creator;
        gameLogic.readXmlFile(xmlDescription);
        gameLogic.newGame();
        players = new ArrayList<>();
        status = GameStatus.WaitingForPlayers;
        initControllerData();
        //return gameLogic.getGameTitle();
        return  "Title";
        //TODO:Read Title from xml file
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
        players.add(player);
        idToGive++;
        registeredPlayers++;

        if (registeredPlayers == requiredPlayers)
        {
            status = GameStatus.Running;
            gameLogic.setPlayers(players);
            gameLogic.startGame();
        }
    }

    public boolean hasPlayerWithName(String name)
    {
        for (Player player : players)
        {
            if (player.getName().equals(name))
            {
                return true;
            }
        }
        return false;
    }
}

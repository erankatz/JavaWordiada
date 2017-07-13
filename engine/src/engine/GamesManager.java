package engine;

import engine.controller.GameController;
import engine.exception.EngineException;
import engine.exception.game.DuplicateGameTitle;

import javax.xml.bind.JAXBException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by eran on 13/07/2017.
 */
public class GamesManager
{
    private static GamesManager myInstance;
    private static Integer numberOfGames;
    private HashMap<Integer, GameController> games;

    private GamesManager()
    {
        numberOfGames = 0;
        games = new HashMap<>();
    }

    public static GamesManager getInstance()
    {
        if (myInstance == null)
        {
            myInstance = new GamesManager();
        }
        return myInstance;
    }

    private boolean isGameNameTaken(String name)
    {
        boolean result = false;
        for (Map.Entry<Integer, GameController> pair : games.entrySet())
        {
            if (pair.getValue().getGameTitle().equals(name))
            {
                result = true;
                break;
            }
        }
        return result;
    }

    public void addGame(String xmlDescription, String creator) throws EngineException,IOException,XPathExpressionException
    {
        GameController newGame = new GameController();
        String gameName = newGame.initGame(xmlDescription, creator);
        if (isGameNameTaken(gameName))
        {
                    throw new DuplicateGameTitle();
        }

        numberOfGames++;
        int key = getMapKey();
        newGame.setKey(key);
        games.put(key, newGame);
    }

    private int getMapKey()
    {
        boolean keepRunning = true;
        int i=1;
        while (keepRunning)
        {
            if (games.containsKey(i))
            {
                i++;
            }
            else
            {
                keepRunning = false;
            }
        }

        return i;
    }

    public List<GameController> getLobbyGameList()
    {
        return games.entrySet().stream()
                .map(entry -> entry.getValue())
                .filter(game-> game.getStatus().equals(GameStatus.WaitingForPlayers))
                .collect(Collectors.toList());
    }

    public GameController getGame(int key)
    {
        return games.get(key);
    }

    public GameController getGameByUserName(String userName)
    {
        final GameController[] result = new GameController[1];
        games.forEach( (key,game )->
        {
            if (game.hasPlayerWithName(userName))
            {
                result[0] = game;
            }
        });
        return result[0];
    }
}

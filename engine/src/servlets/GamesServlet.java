package servlets;

import com.google.gson.Gson;
import engine.GameManager;
import engine.GameStatus;
import engine.GamesManager;
import engine.controller.GameController;
import engine.message.DiceResultMessage;
import engine.message.GameStatusMessage;
import engine.model.Games;
import engine.model.LoadGameStatus;
import javafx.util.Pair;
import users.LoginManager;
import users.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by eran on 13/07/2017.
 */


@WebServlet(name = "GamesServlet", urlPatterns = {"/games"})
public class GamesServlet extends HttpServlet
{
    private final int UNKNOWN = -1;
    GamesManager gamesManager;

    public GamesServlet()
    {
        gamesManager = GamesManager.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String action = req.getParameter("action");

        switch (action)
        {
            case "gameList":
                gameListAction(req, resp);
                break;
            case "gameDetails":
                gameDetailsAction(req, resp);
                break;
            case "joinGame":
                joinGameAction(req, resp);
                break;
            case "gamePlayers":
                gamePlayersAction(req, resp);
                break;
            case "leaveGame":
                leaveGameAction(req, resp);
                break;
            case "gameStatus":
                gameStatusAction(req, resp);
                break;
            case "rollDice":
                gameRollDiceAction(req,resp);
        }
    }

    private void gameRollDiceAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int key = Integer.parseInt(request.getParameter("key"));
        Gson gson = new Gson();
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        String userName = SessionUtils.getUsername(request.getSession());
        GameController game = null;

        if (key != UNKNOWN && userName.contentEquals(gamesManager.getGame(key).getCurrentPlayerName()))
        {
             game = gamesManager.getGame(key);
            Integer result = game.rollDice();
            out.println(gson.toJson(result));
        }
        else
        {

             game = gamesManager.getGameByUserName(userName);
            if (!userName.contentEquals(game.getCurrentPlayerName())){
                game =null;
            }
        }

        if (game != null){
            out.println(gson.toJson(new DiceResultMessage(game.rollDice())));
        }
    }


    private void gameDetailsAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        int key = Integer.parseInt(request.getParameter("key"));
        Gson gson = new Gson();
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");

        if (key != UNKNOWN)
        {
            GameController game = gamesManager.getGame(key);
            out.println(gson.toJson(game));
        }
        else
        {
            String userName = SessionUtils.getUsername(request.getSession());
            GameController game = gamesManager.getGameByUserName(userName);
            out.println(gson.toJson(game));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String action = req.getParameter("action");

        switch (action)
        {
            case "loadGame":
                loadGameAction(req, resp);
                break;
            case "joinGame":
                joinGameAction(req, resp);
                break;

        }
    }


    private void joinGameAction(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String userName = request.getParameter("user");
        boolean isComputer = request.getParameter("isComputer").equals("true");
        int gameId = Integer.parseInt(request.getParameter("gameId"));
        GameController currentGame = gamesManager.getGame(gameId);
        LoginManager loginManager = LoginManager.getInstance();
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        response.setContentType("application/json");

        if (loginManager.canUserJoinGame(userName) && currentGame.getStatus().equals(GameStatus.WaitingForPlayers))
        {
            loginManager.userJoinGame(userName, gameId);
            currentGame.addPlayer(userName, isComputer);
            out.print(gson.toJson(new LoadGameStatus(true,"")));
        }
        else
        {
            out.print(gson.toJson(new LoadGameStatus(false, "Couldn't join game.")));
        }
    }

    private void loadGameAction(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String xmlContent = request.getParameter("xml");
        String dictionaryContent  = request.getParameter("dictionary");
        String gameCreator = request.getParameter("creatorName");

        Gson gson = new Gson();
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");

        try
        {
            gamesManager.addGame(xmlContent,dictionaryContent, gameCreator);
            out.println(gson.toJson(new LoadGameStatus(true, "")));
        }
        catch (Exception e)
        {
            out.println(gson.toJson(new LoadGameStatus(false, e.getMessage())));
        }
    }

    private void gamePlayersAction(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String userName = SessionUtils.getUsername(request.getSession());
        GameController game = gamesManager.getGameByUserName(userName);
        Gson gson = new Gson();
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");

        if (game != null)
        {
            out.println(gson.toJson(game.getPlayersDetails()));
        }
    }

    private void gameListAction(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        Gson gson = new Gson();
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        out.println(gson.toJson(new Games(gamesManager.getLobbyGameList())));
    }

    private void leaveGameAction(HttpServletRequest request, HttpServletResponse response)
    {
        String userName = SessionUtils.getUsername(request.getSession());
        GameController game = gamesManager.getGameByUserName(userName);
        if (game != null)
        {
            game.playerLeave(userName);
        }
        LoginManager.getInstance().userLeaveGame(userName);
        if (game.getNumRegisteredPlayers() == 0 && game.isReplayMode()){
            gamesManager.removeGame(game);
        }
    }

    private void gameStatusAction(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        Gson gson = new Gson();
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        String userName = SessionUtils.getUsername(request.getSession());
        GameController game = gamesManager.getGameByUserName(userName);

        if (game != null)
        {
            GameStatus status = game.getStatus();
            String name = "";
            if (status == GameStatus.Running)
            {
                name = game.getCurrentPlayerName();
            }
            out.println(gson.toJson(new GameStatusMessage(status, name)));
        }
    }
}

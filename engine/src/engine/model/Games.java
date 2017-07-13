package engine.model;

import engine.controller.GameController;

import java.util.List;

/**
 * Created by user on 12/10/2016.
 */
public class Games
{
    List<GameController> games;

    public Games(List<GameController> games)
    {
        this.games = games;
    }

    public List<GameController> getGames()
    {
        return games;
    }

    public void setGames(List<GameController> games)
    {
        this.games = games;
    }
}

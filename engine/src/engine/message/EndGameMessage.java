package engine.message;

import engine.Board;
import engine.Player;
import engine.PlayerData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eran on 18/07/2017.
 */
public class EndGameMessage {
    List<PlayerData> winners;
    long winnersScore;
    Board board;

    public EndGameMessage( List<PlayerData> winners, long winnersScore, Board board)
    {
        this.winners = winners;
        this.winnersScore = winnersScore;
        this.board = board;
    }
}

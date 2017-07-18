package engine.message;

import engine.Board;
import engine.PlayerData;

import java.util.List;

/**
 * Created by eran on 18/07/2017.
 */
public class PageDetailsMessage {
    Board board;
    int move;//
    int turn;//
    int score;//
    List<PlayerData> playersDetails;
    String charFrequencyString;

    public PageDetailsMessage(Board b, int move, int turn,int score,List<PlayerData> playersDetails,String charFrequencyString){
        this.board = b;
        this.move = move;
        this.turn = turn;
        this.score = score;
        this.charFrequencyString = charFrequencyString;
        this.playersDetails = playersDetails;
    }
}

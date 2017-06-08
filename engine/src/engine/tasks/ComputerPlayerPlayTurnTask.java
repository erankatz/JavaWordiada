package engine.tasks;

import engine.ComputerPlayer;
import javafx.concurrent.Task;

import java.util.Comparator;

/**
 * Created by eran on 06/06/2017.
 */
public class ComputerPlayerPlayTurnTask extends Task<Boolean> {
    private ComputerPlayer computerPlayer;
    public ComputerPlayerPlayTurnTask(ComputerPlayer computerPlayer){
        this.computerPlayer = computerPlayer;
    }
    public Boolean call(){
        computerPlayer.playTurn();
        return true;
    }
}

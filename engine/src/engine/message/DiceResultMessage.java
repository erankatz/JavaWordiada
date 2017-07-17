package engine.message;

/**
 * Created by eran on 17/07/2017.
 */
public class DiceResultMessage {
    private int result;
    String msg;
    public DiceResultMessage(int result){
        this.result = result;
        msg = "You got " + result + " on dice";
        //TODO:Handle with double press
    }
}

package engine.message;

/**
 * Created by eran on 17/07/2017.
 */
public class DiceResultMessage implements IGameMessage {
    private int result;
    String msg;
    private String userName;
    private String  otherPlayerMessage;

    @Override
    public String getOtherPlayerMessage(){
        return otherPlayerMessage;
    }

    public DiceResultMessage(int result,String userName){
        this.result = result;
        msg = "You got " + result + " on dice";
        this.userName = userName;
        //TODO:Handle with double press
    }
}

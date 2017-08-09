package engine.message;

/**
 * Created by eran on 18/07/2017.
 */
public class RevealCardMessage implements IGameMessage{
    private String currentPlayerMsg;
    private String remotePlayerMsg;
    private boolean isSuccess;
    private String userName;
    private String  otherPlayerMessage;

    @Override
    public String getOtherPlayerMessage(){
        return otherPlayerMessage;
    }

    public RevealCardMessage(boolean errorCode,String currentPlayerMsg,String userName){
        this.currentPlayerMsg = currentPlayerMsg;
        this.otherPlayerMessage = "The Player " + userName + " is revealing cards " + " according to the dice result";
        this.isSuccess = errorCode;
        this.userName = userName;
    }
}

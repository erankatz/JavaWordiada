package engine.message;

/**
 * Created by eran on 18/07/2017.
 */
public class RevealCardMessage {
    private String currentPlayerMsg;
    private String remotePlayerMsg;
    private boolean isSuccess;

    public RevealCardMessage(boolean errorCode,String currentPlayerMsg,String remotePlayerMsg){
        this.currentPlayerMsg = currentPlayerMsg;
        this.remotePlayerMsg = remotePlayerMsg;
        this.isSuccess = errorCode;
    }
}

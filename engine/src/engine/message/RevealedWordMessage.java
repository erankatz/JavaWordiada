package engine.message;

/**
 * Created by eran on 18/07/2017.
 */
public class RevealedWordMessage {
    int numOfRetriesLeft;
    String  currentPlayerMessage;
    String otherPlayerMessage;
    boolean isValidWord;

    public RevealedWordMessage(int numOfRetriesLeft,String currentPlayerMessage,String otherPlayerMessage,boolean isValidWord){
        this.numOfRetriesLeft =numOfRetriesLeft;
        this.currentPlayerMessage = currentPlayerMessage;
        this.otherPlayerMessage = otherPlayerMessage;
        this.isValidWord = isValidWord;
    }
}

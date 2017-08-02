package engine.message;

/**
 * Created by eran on 18/07/2017.
 */
public class RevealedWordMessage {
    int numOfRetriesLeft;
    String  currentPlayerMessage;
    String otherPlayerMessage;
    String word;
    boolean isValidWord;
    long score;

    public RevealedWordMessage(int numOfRetriesLeft,String currentPlayerMessage,String otherPlayerMessage,boolean isValidWord,long score,String word){
        this.numOfRetriesLeft =numOfRetriesLeft;
        this.word =word;
        this.otherPlayerMessage = otherPlayerMessage;
        this.isValidWord = isValidWord;
        this.score =score;
        if (currentPlayerMessage == null){
            if (isValidWord){
                this.currentPlayerMessage = "You are right," + "you got " + score;
            } else {
                this.currentPlayerMessage = "You are Wrong, " + " you have " + numOfRetriesLeft +" left";
            }
        } else{
            this.currentPlayerMessage =currentPlayerMessage;
        }
    }
}

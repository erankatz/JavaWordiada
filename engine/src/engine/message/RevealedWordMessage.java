package engine.message;

/**
 * Created by eran on 18/07/2017.
 */
public class RevealedWordMessage implements IGameMessage{
    int numOfRetriesLeft;
    String  currentPlayerMessage;
    String word;
    boolean isValidWord;
    long score;
    private String  otherPlayerMessage;
    private String userName;
    @Override
    public String getOtherPlayerMessage(){
        return otherPlayerMessage;
    }

    public RevealedWordMessage(int numOfRetriesLeft,String currentPlayerMessage,boolean isValidWord,long score,String word,String userName){
        this.numOfRetriesLeft =numOfRetriesLeft;
        this.word =word;
        this.otherPlayerMessage = otherPlayerMessage;
        this.isValidWord = isValidWord;
        this.score =score;
        this.userName = userName;
        if (currentPlayerMessage == null){
            if (isValidWord){
                this.currentPlayerMessage = "You are right," + "you got " + score + " for word " + word;
                this.otherPlayerMessage = "Player " + userName + " got " + score + " for word " + word;
            } else {
                this.currentPlayerMessage = "You are Wrong, " + " you have " + numOfRetriesLeft +" left";
                this.otherPlayerMessage = "Player " + userName + " tried to compose word " + word + " but failed (" + numOfRetriesLeft + "retries left)";
            }
        } else{
            this.currentPlayerMessage =currentPlayerMessage;
        }
    }
}

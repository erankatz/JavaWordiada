package engine.exception.player;

/**
 * Created by eran on 06/06/2017.
 */
public class ChosenWrongNumberOfCardsException extends PlayerException {
    int cubeResult;
    int numberOfSelectedCards;
    public ChosenWrongNumberOfCardsException(int cubeResult,int numberOfSelectedCards){
        this.cubeResult = cubeResult;
        this.numberOfSelectedCards = numberOfSelectedCards;
    }

    @Override
    public String getMessage(){
        return "You chose wrong number of cards. Instead of picking " + cubeResult + " cards you picked " + numberOfSelectedCards;
    }

}

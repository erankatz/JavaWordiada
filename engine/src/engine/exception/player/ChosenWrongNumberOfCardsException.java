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
        return "You chosen wrong number of card you got " + cubeResult + " and chosen " + numberOfSelectedCards;
    }

}

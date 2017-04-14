package engine.exception.dice;

/**
 * Created by eran on 11/04/2017.
 */
public class WrongNumberOfDiceFacetExecption extends DiceException{
    private int numOfFacet;

    public WrongNumberOfDiceFacetExecption(int numOfFacet) {
        this.numOfFacet = numOfFacet;
    }

    public int getNumOfFacet(){
        return numOfFacet;
    }
}

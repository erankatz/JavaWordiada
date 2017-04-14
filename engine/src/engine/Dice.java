package engine;
import engine.exception.dice.DiceException;
import engine.exception.dice.WrongNumberOfDiceFacetExecption;

import java.security.PublicKey;
import java.util.Random;

/**
 * Created by eran on 30/03/2017.
 */
public class Dice {
    private final int minFacets = 6;
    private final int maxFacets = 12;
    private final int numOfFacets;
    private Integer result;

    public Dice(int numOfFacets) throws DiceException
    {
        if (numOfFacets >= minFacets && numOfFacets <= maxFacets)
            this.numOfFacets = numOfFacets;
        else {
            this.numOfFacets = 0;
            throw new WrongNumberOfDiceFacetExecption(numOfFacets);
        }
    }

    protected Integer getResult()
    {
        return result;
    }

    public int role()
    {
        //TODO:What happened with 1 facet
        Random rand = new Random();
        this.result = rand.nextInt(numOfFacets-1) + 2;
        return result.intValue();
    }

    public void endTurn() {
        result = null;
    }

}

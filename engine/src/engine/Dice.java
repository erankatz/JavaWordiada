package engine;
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

    public Dice(int numOfFacets)
    {
        if (numOfFacets >= minFacets && numOfFacets <= maxFacets)
            this.numOfFacets = numOfFacets;
        else {
            this.numOfFacets = 0;
            //TODO:Throw too many cube Facets or too low cube facets
        }
    }

    protected Integer getResult()
    {
        return result;
    }

    public int role()
    {
        Random rand = new Random();
        this.result = rand.nextInt(numOfFacets) + 1;
        return result.intValue();
    }

    public void endTurn() {
        result = null;
    }

}

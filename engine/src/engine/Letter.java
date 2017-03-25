package engine;

/**
 * Created by eran on 25/03/2017.
 */
public class Letter {
    private final char sign;
    private final byte score;
    private int occurence;


    public Letter(char _sign, byte _score, int _occurence)
    {
        this.sign = _sign;
        this.score = _score;
        this.occurence = _occurence;
    }

    public int getOccurence() {
        return occurence;
    }

    public byte getScore() {
        return score;
    }

    public char getSign() {
        return sign;
    }

    public void decOccurence()
    {
        occurence--;
    }
}

package engine;

/**
 * Created by eran on 29/03/2017.
 */
public class Alphabet {
    static public final int numOfLetters = 26;
    static public boolean IsInAlphabet(char letter)
    {
        letter = Character.toUpperCase(letter);
        if (letter - 'A' <= numOfLetters && letter - 'A' >= 0)
        {
            return true;
        }
        return false;
    }
}

package engine;

/**
 * Created by eran on 29/03/2017.
 */
public class Alphabet {

    static public boolean IsInAlphabet(char letter)
    {
        letter = Character.toUpperCase(letter);
        if ("!?,.:;-_=+*\"\\'(){}[]%$".indexOf(letter) == -1)
        {
            return true;
        }
        return false;
    }
}

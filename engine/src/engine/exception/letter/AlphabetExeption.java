package engine.exception;

import engine.exception.LetterException;

/**
 * Created by eran on 11/04/2017.
 */
public class AlphabetExeption extends LetterException {
    char ch;
    protected AlphabetExeption(char ch){
        this.ch = ch;
    }

    public char getCharecter()
    {
        return ch;
    }
}

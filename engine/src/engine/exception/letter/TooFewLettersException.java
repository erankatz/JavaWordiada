package engine.exception;

import engine.exception.LetterException;

/**
 * Created by eran on 11/04/2017.
 */
public class TooFewLettersException extends LetterException {
    byte numberOfLettersDefined;
    public TooFewLettersException(byte numberOfLettersDefined){
        this.numberOfLettersDefined = numberOfLettersDefined;
    }

    public byte getnumberOfLettersDefined(){
        return numberOfLettersDefined;
    }
}

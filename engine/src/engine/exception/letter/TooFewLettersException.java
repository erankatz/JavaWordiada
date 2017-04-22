package engine.exception.letter;

import engine.exception.letter.LetterException;

/**
 * Created by eran on 11/04/2017.
 */
public class TooFewLettersException extends LetterException {
    int numberOfLettersDefined;
    public TooFewLettersException(int numberOfLettersDefined){
        this.numberOfLettersDefined = numberOfLettersDefined;
    }

    public int getnumberOfLettersDefined(){
        return numberOfLettersDefined;
    }






    // TODO:is the XML cotains all the A-Z???
}

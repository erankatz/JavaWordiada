package engine.exception;

/**
 * Created by eran on 11/04/2017.
 */
public class DuplicateLetterException extends LetterException{
    private char letter;
    public DuplicateLetterException(char letter){
        this.letter = letter;
    }

    public char getLetter(){
        return letter;
    }
}

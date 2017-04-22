package engine.exception.letter;

/**
 * Created by eran on 11/04/2017.
 */
public class AlphabetExeption extends LetterException {
    char ch;
    public AlphabetExeption(char ch){
        this.ch = ch;
    }

    public char getCharecter()
    {
        return ch;
    }

    @Override
    public String getMessage(){
        return String.format("Illegal character %c was given in the XML file",ch);
    }
}

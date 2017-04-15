package engine.exception.board;

/**
 * Created by eran on 15/04/2017.
 */
public class DuplicateCardPositionException extends BoardException{

    @Override
    public String getMessage(){
        return "You chose the same card position more than once";
    }

}

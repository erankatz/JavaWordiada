package engine.exception.board;

/**
 * Created by eran on 22/04/2017.
 */
public class NotEnoughCardsToFillBoardException extends BoardException{

    @Override
    public String getMessage(){
        return "Relying on the given game description, the deck is too small to fill the board";
    }
}

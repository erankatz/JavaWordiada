package engine.exception.board;

/**
 * Created by eran on 15/04/2017.
 */
public class DuplicateCardPositionException extends BoardException{
    private int row;
    private int col;

    public DuplicateCardPositionException(int row,int col){
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String getMessage(){
        return "You chose the same card position more than once";
    }

}

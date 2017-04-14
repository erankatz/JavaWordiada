package engine.exception.card;

/**
 * Created by eran on 11/04/2017.
 */
public class DuplicateCardException extends CardException {
    private int row;
    private int col;

    public DuplicateCardException(int row,int col){
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}

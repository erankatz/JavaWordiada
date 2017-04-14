package engine.exception.board;

/**
 * Created by eran on 14/04/2017.
 */
public class CardNotReveledException extends BoardException {
    public CardNotReveledException(int row,int col){
        this.row = row;
        this.col = col;
    }

    private int row;
    private int col;

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String getMessage(){
        return "";
    }
}

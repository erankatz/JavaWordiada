package engine.exception.board;

import engine.Board;

/**
 * Created by eran on 14/04/2017.
 */
public class WrongCardPositionException extends BoardException{
    public WrongCardPositionException(int row,int col){
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
}

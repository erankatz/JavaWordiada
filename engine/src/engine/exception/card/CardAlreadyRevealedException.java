package engine.exception.card;

/**
 * Created by eran on 11/04/2017.
 */
public class CardAlreadyRevealedException extends CardException {
    private int row;
    private int col;

    public CardAlreadyRevealedException(int row, int col) {
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
    public String getMessage() {
        return String.format("You chose a revealed card: {%d,%d}\nPlease choose again", row, col);
    }
}
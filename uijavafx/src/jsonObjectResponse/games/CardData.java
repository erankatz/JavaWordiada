package jsonObjectResponse.games;

public class CardData {
    boolean isSelected;
    char letter;
    int row;
    int col;
    private boolean revealed;

    public CardData(char letter,boolean isSelected,int row,int col,boolean isHidden){
        this.letter = letter;
        this.isSelected = isSelected;
        this.row = row;
        this.col = col;
        this.revealed = isHidden;
    }
    public boolean getSelected(){
        return isSelected;
    }

    public char getLetter(){
        if (revealed) {
            return letter;
        }else {
            return '?';
        }
    }

    public int getCol(){
        return col;
    }

    public int getRow(){
        return row;
    }

}

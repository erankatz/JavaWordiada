package jsonObjectResponse.games;

public class CardData {
    boolean isSelected;
    char letter;
    int row;
    int col;
    public CardData(char letter,boolean isSelected,int row,int col){
        this.letter = letter;
        this.isSelected = isSelected;
    }
    public boolean getSelected(){
        return isSelected;
    }

    public char getLetter(){
        return letter;
    }

    public int getCol(){
        return col;
    }

    public int getRow(){
        return row;
    }

}

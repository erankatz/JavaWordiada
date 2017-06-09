package engine;

import engine.exception.card.CardAlreadyRevealedException;

/**
 * Created by eran on 29/03/2017.
 */
public class Card implements java.io.Serializable,Cloneable{
    private char letter;
    private byte score;
    private boolean revealed;
    private boolean isEverRevealed; //For computer player in gold fish mode
    private boolean isEnabled;
    private int row;
    private int col;
    private boolean isSelected;

    protected Card(char letter,byte score)
    {
        this.letter = letter;
        this.score = score;
        this.revealed = false;
        this.isEverRevealed = false;
        this.isEnabled = false;
    }
    @Override
    public Card clone(){
        Card c = new Card(letter,score);
        c.revealed = revealed;
        c.isEverRevealed = isEverRevealed;
        c.isEnabled = isEnabled;
        c.row = row;
        c.col = col;
        c.isSelected = isSelected;

        return c;
    }


    public boolean getIsEnabled(){
        return isEnabled;
    }
    public char getLetter()
    {
        if (revealed)
            return letter;
        else
            return '?';
    }

    public char getHiddenChar()
    {
        return letter;
    }

    public boolean isRevealed() {
        return revealed;
    }

    protected void reveal() {
        revealed = true;
        isEverRevealed =true;
    }

    protected void unReveal(){
        revealed =false;
    }


    public void setLocation(int row,int col){
        this.row = row;
        this.col = col;
    }

    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

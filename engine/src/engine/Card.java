package engine;

import engine.exception.card.CardAlreadyRevealedException;

/**
 * Created by eran on 29/03/2017.
 */
public class Card {
    private char letter;
    private byte score;
    private boolean revealed;

    protected Card(char letter,byte score)
    {
        this.letter = letter;
        this.score = score;
        revealed = false;
    }

    public char getLetter()
    {
        if (revealed)
            return letter;
        else
            return '?';
    }

    protected char getHiddenChar()
    {
        return letter;
    }

    public boolean isRevealed()
    {
        return revealed;
    }

    protected void reveal() {
        revealed = true;
    }

    protected void unReveal(){
        revealed =false;
    }
}

package engine;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by eran on 30/03/2017.
 */
public class Player {
    private Deck deck;
    private Board board;
    private Dice cube;
    private int leftCardNumToReveal;

    public Player (Deck deck,Board board,Dice cube)
    {
        this.deck = deck;
        this.cube = cube;
        this.board = board;
        leftCardNumToReveal = 0;
    }
    public int rollDice()
    {
        leftCardNumToReveal = cube.role();
        return leftCardNumToReveal;
    }

    public void revealCard(int row,int col)
    {
        if (cube.getResult() == null)
        {
            //TODO: Return Error Cube not rolled
        }
        if (leftCardNumToReveal == 0)
        {
            //TODO: return Error No cards Left to reveal
        }
        if (board.getNumOfUnrevealedCard() == 0)
        {
            //TODO: Exception All cards already reveled
        }

            Card card = board.getBoardCard(row,col);
            if (!card.isRevealed()){
                card.reveal();
                leftCardNumToReveal--;
            } else {
                //TODO: Card is already revealed Exception and undo function
            }
        this.cube.endTurn();
    }

    public boolean isLeftCardsToReveal()
    {
        return board.getNumOfUnrevealedCard() == 0 || leftCardNumToReveal == 0;
    }
}

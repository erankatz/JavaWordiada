package engine;

import java.util.ArrayList;

/**
 * Created by eran on 29/03/2017.
 */
public class Board {
    private Card cards[][];
    private int boardSize;
    private int numOfUnrevealedCard;

    protected Board(int boardSize)
    {
        this.boardSize = boardSize;
        numOfUnrevealedCard =0;
    }

    protected int getNumOfUnrevealedCard()
    {
        return numOfUnrevealedCard;
    }

    public int getBoardSize(){
        return boardSize;
    }

    public Card getBoardCard(int row,int col)
    {
        return cards[row][col];
    }

    protected void setInitCards(ArrayList<Card> initCards){
        cards = new Card[boardSize][boardSize];
        for (int i=0;i<boardSize;i++)
        {
            for (int j=0;j<boardSize;j++)
            {
                cards[i][j] = initCards.get((i+1)*(j+1) -1);
            }
        }
    }
}

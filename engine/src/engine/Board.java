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
        numOfUnrevealedCard =boardSize*boardSize;
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
        return cards[row-1][col-1];
    }

    protected void setInitCards(ArrayList<Card> initCards){
        cards = new Card[boardSize][boardSize];
        int z =0;
        for (int i=0;i<boardSize;i++)
        {
            for (int j=0;j<boardSize;j++)
            {
                cards[i][j] = initCards.get(z);
                z++;
            }
        }
    }
}

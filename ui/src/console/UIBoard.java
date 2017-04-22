package console;

import engine.Board;
import engine.Card;
import engine.exception.board.WrongCardPositionException;

import java.lang.Math;

/**
 * Created by eran on 29/03/2017.
 */

public class UIBoard {
    private Board board;
    private int spaceAlign;
    public UIBoard(Board board)
    {
        this.board = board;
    }

    public void printGameBoard() {
        spaceAlign = new Double(Math.log10(board.getBoardSize())+ 1).intValue();
        printColumnLabels();
        System.out.println("");
        printBorderLine('_');
        System.out.println("");
        for (int j = 0; j < board.getBoardSize(); j++) {
            System.out.format("%" + spaceAlign + "d",j+1);
            System.out.print(" ");
            for (int i = 0; i < board.getBoardSize(); i++) {
                try {
                    char ch;
                    Card card = board.getBoardCard(j + 1, i + 1);
                    if (card == null){
                        ch =  ' ';
                    } else{
                        ch=card.getLetter();
                    }
                    System.out.format("│%" + spaceAlign + "s",ch);
                }catch(WrongCardPositionException ex){
                    System.out.format("Error in printing the board %d,%d\n",ex.getRow(),ex.getCol());
                    System.exit(1);
                }
            }
            System.out.print('│');
            System.out.format("%" + spaceAlign + "d",j+1);
            System.out.println("");
        }
        printBorderLine('¯');
        System.out.println("");
        printColumnLabels();
        System.out.println("");
    }

    public long getNumOfUnrevealedCard(){
        return board.getNumOfUnrevealedCard();
    }
    private void printStartLineSpace()
    {
        for (int i = 0; i <= spaceAlign; i++) {
            System.out.print(' ');
        }
    }
    private void printColumnLabels() {

        printStartLineSpace();
        for (int i = 0; i < board.getBoardSize(); i++) {
            System.out.format(" %"+ spaceAlign+ "d",i+1);
        }
    }

    public void printNumberOLegalWords(){
        System.out.println("Number of legal words in dictionary is: "+board.getNumberOfLegalWords(card->card != null && card.isRevealed()));
    }
    private  void printBorderLine(char ch)
    {
        String ch2 = new Character(ch).toString();
        printStartLineSpace();
        for (int i = 0; i < board.getBoardSize(); i++) {
            for (int j = 0; j < spaceAlign+1; j++)
                System.out.format(ch2);
        }
    }
}

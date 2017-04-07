package console;

import engine.GameManager;
import engine.Player;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by eran on 30/03/2017.
 */
public class UIPlayer {
    Player player[];
    GameManager manager;
    UIBoard board;

    public UIPlayer(Player player[], GameManager manager)
    {
        this.manager = manager;
        this.board = new UIBoard(manager.getBoard());
        this.player = player;
    }

    public void printCurrentPlayerTurn()
    {
        System.out.format("Player Turn: %d\n",manager.getCurrentPlayerTurn());
    }

    public void playTurn()
    {
        printCurrentPlayerTurn();
        Player currentPlayer = player[manager.getCurrentPlayerTurn()];
        revealCards(currentPlayer);
        this.board.printGameBoard();
        chooseCards(currentPlayer);
        currentPlayer.endTurn();
    }

    private void chooseCards(Player currentPlayer) {
        Scanner s = new Scanner(System.in);
        System.out.format("Build a word by choosing characters in the board according to the format:\n",currentPlayer.rollDice());
        System.out.println("{row,column} {row,column} .....");
        String UserInput = s.nextLine();
        String strWords[] = UserInput.split("(\\d+,\\d+[ ]+){1,}");


    }
    private void revealCards(Player currentPlayer)  { //according to the dice
        System.out.format("Pick %d Cards in the board according to the format:\n",currentPlayer.rollDice());
        System.out.println("{row,column} {row,column} .....");
        System.out.println("Example: 2,3 5,2 1,3");
        Scanner sc = new Scanner(System.in);
        while (currentPlayer.isLeftCardsToReveal()) //reveal cards according to dice
        {
            //System.in.
            while (!sc.hasNext(Pattern.compile("\\d+,\\d+")))
            {
                System.out.println("You entered wrong, not according the requested format");
                sc.next();
            }
            String word = sc.next();
            int row = Integer.parseInt(word.split(",")[0]);
            int col = Integer.parseInt(word.split(",")[1]);
            try{
                currentPlayer.revealCard(row,col);
            }catch (Exception ex)
            {
                System.out.println(ex.getMessage());
            }
            System.out.format("You entered row : %d , col : %d\n",row,col);
        }
    }
}

package console;

import engine.GameManager;
import engine.Player;
import org.omg.CORBA.DynAnyPackage.Invalid;
import org.omg.CORBA.DynAnyPackage.InvalidValue;

import javax.swing.text.html.parser.Entity;
import java.util.*;
import java.util.concurrent.ExecutionException;
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
        System.out.format("Player Turn: %d\n",manager.getCurrentPlayerTurn() + 1);
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
        System.out.format("Build a word by choosing characters in the board according to the format:\n");
        System.out.println("{row,column} {row,column} .....");
        System.out.println("Example: 2,1 1,1 1,2");
        Scanner sc = new Scanner(System.in);
        Boolean done = false;
        while (!done)
        {
            String line = sc.nextLine();
            line = line.replaceAll("(^ )", "").replaceAll("( $)", "");
            if (!line.matches("(\\d+,\\d+)|(\\d+,\\d+[ ]+)+(\\d+,\\d+)")){
                do
                {
                    System.out.println("You entered wrong, not according the requested format");
                    line = sc.nextLine();
                } while (!line.matches("(\\d+,\\d+)|(\\d+,\\d+[ ]+)+(\\d+,\\d+)"));
            }

            try {
                Set<Map.Entry<Integer,Integer>> pairs = MapStringCradsPairsToSet(line);
                done = true;
                currentPlayer.revealWord(pairs);
            } catch (Exception ex) {

            }
        }
    }

    private Set<Map.Entry<Integer,Integer>> MapStringCradsPairsToSet(String sPairs)
    {
        Set<Map.Entry<Integer,Integer>> pairs = new HashSet<>();
        String[] stingPairs =sPairs.split(" ") ;
        for (String pair : stingPairs){
            Integer num1 = Integer.parseInt(pair.split(",")[0]);
            Integer num2 = Integer.parseInt(pair.split(",")[1]);
            Map.Entry<Integer,Integer> entry = new AbstractMap.SimpleEntry<Integer, Integer>(num1, num2);
            if (pairs.add(entry) == false )
            {
                //TODO:Throw Exception
            }
        }
        return pairs;
    }

    private void revealCards(Player currentPlayer)  { //according to the dice
        System.out.format("Pick %d Cards in the board according to the format:\n",currentPlayer.rollDice());
        System.out.println("{row,column} {row,column} .....");
        System.out.println("Example: 2,3 5,2 1,3");
        Scanner sc = new Scanner(System.in);
        while (currentPlayer.isLeftCardsToReveal()) //reveal cards according to dice
        {
            //System.in.
            while (!sc.hasNext(Pattern.compile("\\d+,\\d+\\s{0,}")))
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

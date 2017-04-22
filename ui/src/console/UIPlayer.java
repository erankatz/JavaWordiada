package console;

import engine.GameManager;
import engine.Player;
import engine.exception.board.BoardException;
import engine.exception.board.CardNotReveledException;
import engine.exception.board.DuplicateCardPositionException;
import engine.exception.board.WrongCardPositionException;
import engine.exception.card.CardAlreadyRevealedException;

import java.util.*;
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
        if (board.getNumOfUnrevealedCard() != 0)
            revealCards(currentPlayer);
        this.board.printGameBoard();
        while (currentPlayer.revealWordPending()){
            chooseCards(currentPlayer);
        }

        currentPlayer.endTurn();
    }

    private void chooseCards(Player currentPlayer) {
        System.out.format("Build a word by choosing characters in the board according to the format:\n");
        System.out.println("{row,column} {row,column} .....");
        System.out.println("Example: 2,1 1,1 1,2");
        System.out.println("Retries left: " + currentPlayer.getRetriesNumber());
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
                List<Map.Entry<Integer,Integer>> pairs = MapStringCradsPairsToList(line);
                if(currentPlayer.revealWord(pairs)){
                    System.out.println("You are right! you built a valid word!");
                } else {
                    System.out.println("You are wrong :( ");
                }
                done = true;

            } catch (DuplicateCardPositionException ex) {
                System.out.format("The card in location\n Row: %d\n Col: %d\n Chosen twice\n",
                        ex.getRow(),ex.getCol());
            } catch (WrongCardPositionException ex){
                System.out.format("The card in location\n Row: %d\n Col: %d\n not matches the board sizes\n",
                        ex.getRow(),ex.getCol());
            } catch (CardNotReveledException ex) {
                System.out.format("You chosen the card \n Row: %d\n Col: %d\n This card is not reveled\n You must compose a word from reveled cards\n",
                        ex.getRow(),ex.getCol());
            } catch (BoardException ex){
                System.out.println(ex.getMessage());
            }
        }
    }

    private List<Map.Entry<Integer,Integer>> MapStringCradsPairsToList(String sPairs) throws DuplicateCardPositionException
    {
        List<Map.Entry<Integer,Integer>> pairs = new LinkedList<>();
        String[] stingPairs =sPairs.split(" ") ;
        for (String pair : stingPairs){
            Integer num1 = Integer.parseInt(pair.split(",")[0]);
            Integer num2 = Integer.parseInt(pair.split(",")[1]);
            Map.Entry<Integer,Integer> entry = new AbstractMap.SimpleEntry<Integer, Integer>(num1, num2);
            if (pairs.add(entry) == false )
            {
                throw new DuplicateCardPositionException(num1,num2);
            }
        }
        return pairs;
    }

    public void printPlayerStatistics(){
        int i=1;
        for (Player playerPtr : player){
            Map<String,Long> composedWords = playerPtr.getComposedWords();
            long score = playerPtr.getScore();
            if (composedWords == null){
                System.out.format("The Player %d composed %d words, scored %d \n",
                        i,0,score);
            }else {
                long totalWords = manager.getTotalWordsInDict();
                System.out.format("The Player %d composed %d words, scored %d \n",
                        i, composedWords.size(),score);
                composedWords.entrySet()
                        .forEach(e1->System.out.format("%s: %d / %d\n",e1.getKey(),e1.getValue(),totalWords));
            }
            i++;
        }
    }

    private void revealCards(Player currentPlayer)  { //according to the dice
        int result = currentPlayer.rollDice();
        if (result > board.getNumOfUnrevealedCard()){
            System.out.format("Pick %d Cards in the board according to the format:\n",result);
        } else {
            System.out.format("Pick %d Cards in the board according to the format:\n",currentPlayer.rollDice());
        }
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
            } catch (WrongCardPositionException ex){
                System.out.format("The card in location\n Row: %d\n Col: %d\n not matches the board sizes\n",
                        ex.getRow(),ex.getCol());
            } catch (CardAlreadyRevealedException ex) {
                System.out.format("You chosen the card \n Row: %d\n Col: %d\n This card is already reveled\n You must choose a non reveled cards\n",
                        ex.getRow(),ex.getCol());
            } catch (Exception ex){

            }
            System.out.format("You entered row : %d , col : %d\n",row,col);
        }
    }
}

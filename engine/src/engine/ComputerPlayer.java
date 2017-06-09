package engine;

import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by eran on 15/04/2017.
 */
public class ComputerPlayer extends Player implements java.io.Serializable {
    private final int sleepTime = Utils.sleepTime;
    public ComputerPlayer (GameManager manager,Deck deck,Board board,Dice cube){
        super(manager,deck,board,cube);
    }

    public void playTurn(){
        List<Map.Entry<Integer,Integer>> pairs;
        Random rand = new Random();
        if (board.getNumOfUnrevealedCard() != 0) {
        rollDice();
        Utils.sleepForAWhile(sleepTime);
        pairs = board.AllCardsPositionsFilter(card->card != null && !card.isRevealed());
            while (isLeftCardsToReveal() && pairs.size() !=0) {
                Map.Entry<Integer, Integer> pair = pairs.get(rand.nextInt(pairs.size()));
                pairs.remove(pair);
                try {
                    board.selectBoardCard(pair.getKey(), pair.getValue(),true);
                    Utils.sleepForAWhile(sleepTime);
                    leftCardNumToReveal--;
                } catch (Exception ex) {
                    System.out.println("Error occurred");
                    System.exit(1);
                }
            }
            Utils.sleepForAWhile(sleepTime);
            try{
                leftCardNumToReveal =cube.getResult();
                revealCards();
            } catch (Exception ex)
            {
                ex.printStackTrace();
                System.exit(1);
            }
        }


        List<String> words =  board.getLegalWords(card->card!=null && card.isRevealed()).stream().distinct().collect(Collectors.toList());
        if (words.size() >0){
            List<Map.Entry<Integer,Integer>> wordPairs = new ArrayList<>();
            String word = words.get(rand.nextInt(words.size()));
            pairs = board.AllCardsPositionsFilter(card->card!=null && card.isRevealed());
            for (int k=0;k<word.length();k++) {
                boolean found =false;
                for (int i = 0; i < pairs.size() && !found; i++){
                    try {
                        int row = pairs.get(i).getKey();
                        int col = pairs.get(i).getValue();
                        if (board.getBoardCard(row, col).getLetter() == word.charAt(k)) {
                            board.selectBoardCard(row,col,true);
                            Utils.sleepForAWhile(sleepTime);
                            Map.Entry<Integer, Integer> pair = pairs.get(i);
                            pairs.remove(pair);
                            wordPairs.add(pair);
                            found = true;
                        }
                    } catch (Exception ex) {
                        System.out.println("Error occurred");
                        ex.printStackTrace();
                        System.exit(1);
                    }
                }
            }
            try{
                board.revealWord();
                Utils.sleepForAWhile(sleepTime);
                board.clearSelectedCards();
            }catch (Exception ex)
            {
                System.out.println("Error occurred");
                ex.printStackTrace();
                System.exit(1);
            }
        }

        if (!manager.isComputerMode())
            endTurn();
    }

    @Override
    public int rollDice()
    {
        leftCardNumToReveal = cube.role();
        manager.notifyRollDices(leftCardNumToReveal,retriesNumber);
        return leftCardNumToReveal;
    }
}

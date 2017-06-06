package engine;

import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by eran on 15/04/2017.
 */
public class ComputerPlayer extends Player implements java.io.Serializable {

    public ComputerPlayer (GameManager manager,Deck deck,Board board,Dice cube){
        super(manager,deck,board,cube);
    }

    public void playTurn(){
        List<Map.Entry<Integer,Integer>> pairs;
        Random rand = new Random();
        if (board.getNumOfUnrevealedCard() != 0) {
        rollDice();
        pairs = board.AllCardsPositionsFilter(card->card != null && !card.isRevealed());
            while (isLeftCardsToReveal() && pairs.size() !=0) {
                Map.Entry<Integer, Integer> pair = pairs.get(rand.nextInt(pairs.size()));
                pairs.remove(pair);
                try {
                    board.getBoardCard(pair.getKey(), pair.getValue()).reveal();
                    manager.notifyCardChangedListener(board.getBoardCard(pair.getKey(), pair.getValue()));
                } catch (Exception ex) {
                    System.out.println("Error occurred");
                    System.exit(1);
                }
            }
        }
        List<String> words =  board.getLegalWords(card->card!=null && card.isRevealed());
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
}

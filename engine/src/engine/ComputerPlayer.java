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
    public ComputerPlayer (GameManager manager,String id,String name){
        super(manager,id,name);
    }

    public void playTurn(){
        Utils.sleepForAWhile(sleepTime);
        List<Map.Entry<Integer,Integer>> pairs;
        Random rand = new Random();
        if (manager.getBoard().getNumOfUnrevealedCard() != 0) {
        rollDice();
        Utils.sleepForAWhile(sleepTime);
            pairs = manager.getBoard().AllCardsPositionsFilter(card->card != null && !card.isRevealed());
            while (isLeftCardsToReveal() && pairs.size() !=0) {
                Map.Entry<Integer, Integer> pair = pairs.get(rand.nextInt(pairs.size()));
                try {
                    manager.getBoard().selectBoardCard(pair.getKey(), pair.getValue(),true);
                    Utils.sleepForAWhile(sleepTime);
                    leftCardNumToReveal--;
                    pairs.remove(pair);
                } catch (Exception ex) {
                    System.out.println("Error occurred");
                    System.exit(1);
                }
            }
            Utils.sleepForAWhile(sleepTime);
            leftCardNumToReveal = cube.getResult();
            try{
                if (isLeftCardsToReveal())
                    revealCards();
            } catch (Exception ex)
            {
                ex.printStackTrace();
                System.exit(1);
            }
        }

        List<String> words =  manager.getBoard().getLegalWords(card->card!=null && card.isRevealed()).stream().distinct().collect(Collectors.toList());
        String word =null;
        List<Map.Entry<Integer,Integer>> wordPairs = new ArrayList<>();
        if (words.size() != 0){
            word = words.get(rand.nextInt(words.size()));
        }

        while (revealWordPending()) {
            pairs = manager.getBoard().AllCardsPositionsFilter(card -> card != null && card.isRevealed());
            int numOfCardToSelect;
            if (word == null) {
                if (wordPairs.size() > 2){
                    numOfCardToSelect = rand.nextInt(pairs.size() - 2) + 2;
                } else{
                    numOfCardToSelect =2;
                }
            } else {
                numOfCardToSelect = word.length();
            }
            for (int k = 0; k < numOfCardToSelect; k++) {
                boolean found = false;
                int j = 0;
                if (word == null) {
                    j = rand.nextInt(pairs.size());
                } else {
                    for (int i = 0; i < pairs.size() && !found; i++) {
                        int row1 = pairs.get(i).getKey();
                        int col1 = pairs.get(i).getValue();
                        try {
                            if (manager.getBoard().getBoardCard(row1, col1).getLetter() == word.charAt(k)) {
                                found = true;
                                j = i;
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            System.exit(1);
                        }
                    }
                }
                Map.Entry<Integer, Integer> pair = pairs.get(j);
                manager.getBoard().selectBoardCard(pair.getKey(), pair.getValue(), true);
                Utils.sleepForAWhile(sleepTime);
                pairs.remove(pair);
                wordPairs.add(pair);
            }
            try {
                if (manager.getBoard().revealWord()){
                    retriesNumber =0;
                } else{
                    retriesNumber--;
                }
                Utils.sleepForAWhile(sleepTime);
                manager.getBoard().clearSelectedCards();
            } catch (Exception ex) {
                System.out.println("Error occurred");
                ex.printStackTrace();
                System.exit(1);
            }
        }

        endTurn();
    }

    @Override
    public int rollDice()
    {
        leftCardNumToReveal = cube.role();
        manager.notifyRollDices(leftCardNumToReveal);
        return leftCardNumToReveal;
    }
}

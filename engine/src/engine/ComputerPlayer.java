package engine;

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
        rollDice();
        List<Map.Entry<Integer,Integer>> pairs = board.AllCardsPositionsFilter(card->!card.isRevealed());
        Random rand = new Random();

        while (isLeftCardsToReveal()){
            //TODO:Handle end of the game
            Map.Entry<Integer,Integer> pair = pairs.get(rand.nextInt(pairs.size()));
            pairs.remove(pair);
            try {
                revealCard(pair.getKey(), pair.getValue());
            }catch (Exception ex){
                System.out.println("Error occurred");
                System.exit(1);
            }
        }
        pairs = board.AllCardsPositionsFilter(card->card.isRevealed());
        int wordSize = rand.nextInt(pairs.size());
        List<Map.Entry<Integer,Integer>> wordPairs = new ArrayList<>();
        for (int i=0;i<wordSize;i++){
            Map.Entry<Integer,Integer> pair = pairs.get(rand.nextInt(pairs.size()));
            pairs.remove(pair);
            wordPairs.add(pair);
        }
        try{
            revealWord(wordPairs);
        }catch (Exception ex){
            System.out.println("Error occurred");
            System.exit(1);
        }

        endTurn();
    }
}

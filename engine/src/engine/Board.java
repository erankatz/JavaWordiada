package engine;

import engine.exception.board.BoardException;
import engine.exception.board.CardNotReveledException;
import engine.exception.board.DuplicateCardPositionException;
import engine.exception.board.WrongCardPositionException;
import engine.exception.card.CardAlreadyRevealedException;
import engine.exception.card.CardException;
import engine.exception.deck.DeckException;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by eran on 29/03/2017.
 */
public class Board {
    private Card cards[][];
    private int boardSize;
    private int numOfUnrevealedCard;
    private GameManager manager;
    private Map<String, Long> word2FrequencyDic;
    private Deck deck;

    protected Board(int boardSize,GameManager manager,Deck deck)
    {
        this.boardSize = boardSize;
        this.numOfUnrevealedCard =boardSize*boardSize;
        this.manager = manager;
        this.deck = deck;
    }

    protected void setDictionary(Map<String ,Long> dictionary)
    {
        this.word2FrequencyDic =dictionary;
    }

    public boolean revealWord(List<Map.Entry<Integer,Integer>> pairs) throws DeckException,WrongCardPositionException,CardNotReveledException,BoardException {
        String str = buildWord(pairs);
        if (word2FrequencyDic.containsKey(str))
        {
            replaceCards(pairs);
            manager.wordRevealed(str,word2FrequencyDic.get(str)); //After word is revealed and validation
            return true;
        }
        return false;
    }

    private void replaceCards(List<Map.Entry<Integer,Integer>> pairs) throws DeckException {
        for (Map.Entry<Integer,Integer> pair : pairs){
            try {
                setBoardCard(pair.getKey(),pair.getValue(),deck.removeTopCard());
            } catch (DeckException ex){
                throw ex;
            }
        }
        //pairs.stream()
        //        .forEach(entry-> setBoardCard(entry.getKey(),entry.getValue(),deck.removeTopCard()));
    }

    private String buildWord(List<Map.Entry<Integer,Integer>> pairs) throws BoardException,WrongCardPositionException,CardNotReveledException{
        String str = new String("");

        //check for duplicate positions
        Set<Map.Entry<Integer,Integer>> entries = pairs.stream().collect(Collectors.toSet());
        if (entries.size() != pairs.size()){
            throw new DuplicateCardPositionException();
        }

        //build word
        for (Map.Entry<Integer,Integer> pair : pairs)
        {
            Card card = getBoardCard(pair.getKey(),pair.getValue());
            if (!card.isRevealed()) {
                throw new CardNotReveledException(pair.getKey(),pair.getValue());
            }
            str += card.getLetter();
        }

        return  str.toUpperCase();
    }

    protected void ChangeAllCardsToUnreveal(){
        Arrays.stream(cards).forEach(cardrows-> Arrays.stream(cardrows).forEach(card->card.unReveal()));
    }

    public List<Map.Entry<Integer,Integer>> AllCardsPositionsFilter(Predicate<Card> filter) {
        List<Map.Entry<Integer,Integer>> pairs = new ArrayList<>();
        for (int i=0;i<boardSize;i++)
        {
            for (int j=0;j<boardSize;j++)
            {
                Card card = cards[i][j];
                if (filter.test(card)) {
                    pairs.add(new AbstractMap.SimpleEntry<Integer, Integer>(i+1,j+1));
                }
            }
        }
        return pairs;
    }

    protected int getNumOfUnrevealedCard()
    {
        return numOfUnrevealedCard;
    }

    public int getBoardSize(){
        return boardSize;
    }

    public Card getBoardCard(int row,int col) throws WrongCardPositionException
    {
        if (row > boardSize || col > boardSize){
            throw new WrongCardPositionException(row,col);
        }
        return this.cards[row-1][col-1];
    }

    private void setBoardCard(int row,int col,Card card){
        this.cards[row-1][col-1] = card;
    }

    protected void setInitCards(ArrayList<Card> initCards){
        this.cards = new Card[boardSize][boardSize];
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

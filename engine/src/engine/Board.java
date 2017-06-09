package engine;

import engine.exception.EngineException;
import engine.exception.board.BoardException;
import engine.exception.board.CardNotReveledException;
import engine.exception.board.DuplicateCardPositionException;
import engine.exception.board.WrongCardPositionException;
import engine.exception.card.CardAlreadyRevealedException;
import engine.exception.card.CardException;
import engine.exception.dice.DiceException;
import engine.wordSearch.WordSearch;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by eran on 29/03/2017.
 */
public class Board implements java.io.Serializable,Cloneable{
    private Card cards[][];
    private int boardSize;
    private int numOfUnrevealedCard=0;
    private GameManager manager;
    private Map<String, Long> word2FrequencyDic;
    private Deck deck;
    private WordSearch wordSearcher;
    private List<Map.Entry<Integer,Integer>> selectedCardsList = new ArrayList<>();

    private Board(int boardSize,int numOfUnrevealedCard,GameManager manager, Map<String,Long> word2FrequencyDic,WordSearch wordSearcher){
        this.boardSize = boardSize;
        this.numOfUnrevealedCard = numOfUnrevealedCard;
        this.manager = manager;
        this.word2FrequencyDic = word2FrequencyDic;
        this.wordSearcher = wordSearcher;
    }

    protected Board(int boardSize,GameManager manager,Deck deck)
    {
        this.boardSize = boardSize;
        this.numOfUnrevealedCard =boardSize*boardSize;
        this.manager = manager;
        this.deck = deck;
    }

    @Override
    public Board clone(){
        Board b= new Board(boardSize,numOfUnrevealedCard,manager,word2FrequencyDic,wordSearcher);
        b.deck = this.deck.clone();
        return b;
    }

    protected Deck getDeck(){
        return deck;
    }
    protected void setDictionary(Map<String ,Long> dictionary)
    {
        this.word2FrequencyDic =dictionary;
        wordSearcher = new WordSearch(dictionary.keySet());
    }

    public int getNumberOfLegalWords(Predicate<Card> filter){
        return getLegalWords(filter).size();
    }

    public List<String> getLegalWords(Predicate<Card> filter){
        List<Card> filteredCards = Arrays.stream(cards).flatMap(Arrays::stream).filter(card->card != null).filter(filter).collect(Collectors.toList());
        return wordSearcher.findWords(filteredCards);
    }

    public boolean revealWord() throws WrongCardPositionException,CardNotReveledException,BoardException {
        List<Map.Entry<Integer,Integer>> pairs = selectedCardsList;
        String str = buildWord(pairs);
        if (word2FrequencyDic.containsKey(str))
        {
            replaceCards(pairs);
            manager.wordRevealed(str,word2FrequencyDic.get(str)); //After word is revealed and validation
            manager.notifyWordRevealedListeners(str,1);
            clearSelectedCards();
            selectedCardsList.clear();
            return true;
        }
        manager.notifyWordRevealedListeners(str,0);
        clearSelectedCards();
        selectedCardsList.clear();
        return false;
    }


    private void replaceCards(List<Map.Entry<Integer,Integer>> pairs)  {

        pairs.stream()
                .forEach(entry-> {
                    Card c = deck.removeTopCard();
                    if (c == null){
                        manager.notifyCardRemovedListeners(entry.getKey(),entry.getValue());
                    } else{
                        setBoardCard(entry.getKey(),entry.getValue(),c);
                        manager.notifyCardChangedListener(c);
                    }

                });
    }

    public void notifyAllCardsChanged(){
        Arrays.stream(cards).flatMap(x->Arrays.stream(x)).filter(x->x!= null).forEach(c->manager.notifyCardChangedListener(c));
    }

    private String buildWord(List<Map.Entry<Integer,Integer>> pairs) throws BoardException,WrongCardPositionException,CardNotReveledException{
        String str = new String("");

        //check for duplicate positions
        List<Map.Entry<Map.Entry<Integer,Integer>,Long>> entries = pairs.stream()
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()))
                .entrySet().stream().filter(e-> e.getValue() > 1).collect(Collectors.toList());
        if (entries.size() >0){
            Map.Entry<Integer,Integer> entry = entries.get(0).getKey();
            throw new DuplicateCardPositionException(entry.getKey(),entry.getValue());
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

    protected void addMangerCardsListener(GameManager manager)
    {
        //if (cards != null){
        //    Arrays.stream(cards).flatMap(x->Arrays.stream(x)).forEach(card->card.setMangerListener(manager));
        //}
    }

    protected void ChangeAllCardsToUnrevealed(){
        Arrays.stream(cards).forEach(cardRows-> Arrays.stream(cardRows).filter(card->card!=null).forEach(card->card.unReveal()));
        Arrays.stream(cards).forEach(cardRows-> Arrays.stream(cardRows).filter(card->card!=null).forEach(card->manager.notifyCardChangedListener(card)));
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

    public long getNumOfUnrevealedCard()
    {
        return Arrays.stream(cards).flatMap(x->Arrays.stream(x)).filter(card->card != null && !card.isRevealed()).count();
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
        this.cards[row-1][col-1].setLocation(row,col);
        manager.notifyCardChangedListener(cards[row-1][col-1]);
    }

    protected void setInitCards(ArrayList<Card> initCards){
        this.cards = new Card[boardSize][boardSize];
        int z =0;
        for (int i=0;i<boardSize;i++)
        {
            for (int j=0;j<boardSize;j++)
            {
                cards[i][j] = initCards.get(z);
                cards[i][j].setLocation(i+1,j+1);
                manager.notifyCardChangedListener(cards[i][j]);
                z++;
            }
        }
    }

    public void selectBoardCard(int row, int col,boolean value) {
        if (value ==true)
            selectedCardsList.add(new AbstractMap.SimpleEntry<Integer, Integer>(row,col));
        cards[row-1][col-1].setSelected(value);
        manager.notifyCardChangedListener(cards[row-1][col-1]);

    }

    public void clearSelectedCards(){
        selectedCardsList.clear();
        Arrays.stream(cards).flatMap(Arrays::stream).filter(c->c!=null).forEach(c->selectBoardCard(c.getRow(),c.getCol(),false));
    }

    public void revealCards() throws DiceException,CardException,WrongCardPositionException {
        //ToDO:Handle too many cards or few cards selected
        for (Map.Entry<Integer,Integer> e : selectedCardsList )
        {
            try{

                Card card = cards[e.getKey()-1][e.getValue()-1];
                if (!card.isRevealed()){
                    card.reveal(); // changing flag to 'reveal'
                    card.setSelected(false);
                    manager.notifyCardChangedListener(card);
                } else {
                    throw new CardAlreadyRevealedException(card.getRow(),card.getCol());
                }
            } catch (Exception ex){
                selectedCardsList.clear();
                clearSelectedCards();
                throw ex;
            }
        }
        manager.setRevealCardsMove(selectedCardsList);
        selectedCardsList.clear();
        clearSelectedCards();
    }

    public List<Map.Entry<Integer,Integer>> getSelectedCardsList(){
        return  selectedCardsList;
    }

    public String getLowestFrequencyDictionaryWords(){
        return  word2FrequencyDic
                .entrySet()
                .stream().filter(distinctByKey(p -> p.getKey()))
                .sorted(Map.Entry.comparingByValue())
                .map(e->e.getKey() + "-" + e.getValue())
                .limit(10)
                .collect(Collectors.joining("\n"));
    }
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}

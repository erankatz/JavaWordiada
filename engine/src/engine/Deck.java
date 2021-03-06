package engine;

import com.sun.org.glassfish.gmbal.GmbalException;
import engine.exception.letter.AlphabetExeption;
import engine.exception.letter.DuplicateLetterException;
import engine.exception.letter.LetterException;
import engine.exception.letter.TooFewLettersException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by eran on 29/03/2017.
 */
public class Deck implements java.io.Serializable,Cloneable{
    private HashSet<Letter> letterArr;
    private int deckSize;
    private LinkedList<Card> cards;
    private Map<Character,Long> initCharFrequency;
    private GameManager manager;

    private Deck(){}
    protected Deck(Document doc, XPath xpath) throws XPathExpressionException, LetterException {
        XPathExpression expr =  xpath.compile("/GameDescriptor/Structure/Letters/@target-deck-size");
        Number deckSize = (Number) expr.evaluate(doc, XPathConstants.NUMBER); // get target-deck-size
        this.deckSize = deckSize.intValue();
        readLeters(doc,xpath);
        this.deckSize =0;
        letterArr.stream().forEach(letter->this.deckSize+=letter.getOccurence());
    }

    public long getScoreLetter(char ch ){
        Optional<Letter> letter = letterArr.stream().filter(letter1->letter1.getSign()==ch).findFirst();
        if (letter.isPresent()){
            return ( Byte.toUnsignedLong(letter.get().getScore()));
        }
        return 0;
    }

    @Override
    public Deck clone(){
        Deck deck = new Deck();
        deck.letterArr =letterArr;
        deck.deckSize = deckSize;
        deck.manager =manager;
        deck.initCharFrequency = initCharFrequency;
        deck.cards = new LinkedList<>();
        cards.stream().forEach(c->deck.cards.add(c.clone()));
        return deck;
    }

    public Map<Character,Long> CreateMapStructureCharToLong()
    {// The function creates Map structure : Character -> Long
        return cards.stream()
                .collect(Collectors.groupingBy(e -> e.getHiddenChar(),Collectors.counting()));
    }

    public Map<Character,Long> getInitCharFrequency()
    {
        return this.initCharFrequency;
    }


    public int getDeckSize(){
        if (cards == null)
            return deckSize;
        else
            return cards.size();
    }

    public void NewGame()
    {
        Random rand = new Random();
        ArrayList<Letter> currLetterArr = new ArrayList<Letter>(); //ArrayList of Letters with data from XML
        for (Letter letter : letterArr)
        {
            currLetterArr.add((Letter) letter.clone());
        }
        cards = new LinkedList<>();
        while (currLetterArr.size() !=0)
        {
            Letter letter;
            int n = rand.nextInt(currLetterArr.size());

            letter = currLetterArr.get(n);
            if (letter.getOccurence() == 1 )
            {
                currLetterArr.remove(n);
            }
            letter.decOccurence();
            Card card = new Card(letter.getSign(),letter.getScore());
            cards.add(card);
        }
        this.initCharFrequency = CreateMapStructureCharToLong();
    }

    public Card removeTopCard()
    {

        if (cards.size() == 0)
        {
            return null;
        }

        return cards.removeLast();
    }

    private void readLeters(Document doc, XPath xpath) throws XPathExpressionException,LetterException
    {//Getting NodeList form the XML and then extracting the data
        XPathExpression expr = xpath.compile("sum(/GameDescriptor/Structure/Letters/Letter/Frequency)");
        Number sumOfFreq = (Number) expr.evaluate(doc, XPathConstants.NUMBER); //calculate sum of frequences

        expr = xpath.compile("/GameDescriptor/Structure/Letters/Letter");
        NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        letterArr = new HashSet<Letter>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            char sign = element.getElementsByTagName("Sign").item(0).getTextContent().charAt(0);
            sign = Character.toUpperCase(sign);
            int score = Integer.parseInt(element.getElementsByTagName("Score").item(0).getTextContent());
            double frequency = Double.parseDouble(element.getElementsByTagName("Frequency").item(0).getTextContent());
            int occurence = (int) Math.ceil((frequency / sumOfFreq.doubleValue()) * deckSize);
            if (Alphabet.IsInAlphabet(sign)) {
                if (letterArr.add(new Letter(sign, (byte) score, occurence)) == false) {
                    throw new DuplicateLetterException(sign);
                }
            } else {
                throw new AlphabetExeption(sign);
            }
        }
    }

    public int getNumOfChars() {
        return letterArr.size();
    }


    class Letter implements Cloneable, java.io.Serializable {
        private final char sign;
        private final byte score;
        private int occurence;
        private int initOccurence;

        protected Letter(char _sign, byte _score, int _occurence) {
            this.sign = _sign;
            this.score = _score;
            this.occurence = _occurence;
            this.initOccurence = _occurence;
        }

        public int getOccurence() {
            return occurence;
        }

        public int getInitOccurence() {return initOccurence;}

        public byte getScore() {
            return score;
        }

        public char getSign() {
            return sign;
        }

        protected void decOccurence() {
            occurence--;
        }

        @Override
        public boolean equals(Object o){
            if(o == null)   return false;
            if(!(o instanceof Letter) ) return false;

            Letter other = (Letter) o;
            return this.sign == other.sign;
        }

        @Override
        public int hashCode(){
            return sign;
        }

        @Override
        protected Object clone()
        {
            return new Letter(sign,score,occurence);
        }
    }
}

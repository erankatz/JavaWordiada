package engine;

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
public class Deck implements java.io.Serializable{
    private HashSet<Letter> letterArr = new HashSet<Letter>();
    private final int deckSize;
    private LinkedList<Card> cards;
    private Map<Character,Long> initCharFrequency;

    protected Deck(Document doc, XPath xpath) throws XPathExpressionException, LetterException {
        XPathExpression expr =  xpath.compile("/GameDescriptor/Structure/Letters/@target-deck-size");
        Number deckSize = (Number) expr.evaluate(doc, XPathConstants.NUMBER); // get target-deck-size
        this.deckSize = deckSize.intValue();
        readLeters(doc,xpath);
        if (letterArr.size() != Alphabet.numOfLetters) {
            throw new TooFewLettersException(letterArr.size());
        }
    }

    public Map<Character,Long> getCharFrequency()
    {
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
        ArrayList<Letter> currLetterArr = new ArrayList<Letter>();
        for (Letter letter : letterArr)
        {
            currLetterArr.add(letter);
        }
        cards = new LinkedList<>();
        for (int i=0;i<deckSize;i++)
        {
            Random rand = new Random();
            Letter letter;
            int n;
            if (currLetterArr.size() !=0)
                n = rand.nextInt(currLetterArr.size());
            else {
                System.out.printf("No Cards");
                throw new NullPointerException();
            }
            letter = currLetterArr.get(n);
            if (letter.getOccurence() == 1 )
            {
                currLetterArr.remove(n);
            }
            letter.decOccurence();
            cards.add(new Card(letter.getSign(),letter.getScore()));
        }
        this.initCharFrequency = getCharFrequency();
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
    {
        XPathExpression expr = xpath.compile("sum(/GameDescriptor/Structure/Letters/Letter/Frequency)");
        Number sumOfFreq = (Number) expr.evaluate(doc, XPathConstants.NUMBER); //calculate sum of frequences

        expr = xpath.compile("/GameDescriptor/Structure/Letters/Letter");
        NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        if (nodes.getLength() != Alphabet.numOfLetters ) {
            throw new TooFewLettersException(nodes.getLength());
        }

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

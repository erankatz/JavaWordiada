package engine;

import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;


/**
 * Created by eran on 29/03/2017.
 */
public class Deck {
    private HashSet<Letter> letterArr = new HashSet<Letter>();
    private final int deckSize;
    private LinkedList<Card> cards;

    public Deck(Document doc, XPath xpath) throws XPathExpressionException {
        XPathExpression expr =  xpath.compile("/GameDescriptor/Structure/Letters/@target-deck-size");
        Number deckSize = (Number) expr.evaluate(doc, XPathConstants.NUMBER); // get target-deck-size
        this.deckSize = deckSize.intValue();
        readLeters(doc,xpath);
        if (letterArr.size() != Alphabet.numOfLetters) {
            //TODO: Too few letter
        }
    }

    public int getDeckSize(){
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
    }

    public Card removeTopCard()
    {
        if (cards == null)
        {
            //TODO:Return NO init  Deck Exception
        } else if (cards.size() == 0)
        {
            //TODO:Return Empty Deck Exception
        }

        return cards.removeLast();
    }

    private void readLeters(Document doc, XPath xpath) throws XPathExpressionException
    {
        XPathExpression expr = xpath.compile("sum(/GameDescriptor/Structure/Letters/Letter/Frequency)");
        Number sumOfFreq = (Number) expr.evaluate(doc, XPathConstants.NUMBER); //calculate sum of frequences

        expr = xpath.compile("/GameDescriptor/Structure/Letters/Letter");
        NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        if (nodes.getLength() != Alphabet.numOfLetters ) {
            //TODO:Write Exception about too few letters
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
                    //TODO:Write Exception class when duplicate letter exist
                }
            } else {
                //TODO:Write Exception known letter

            }
        }
    }


    class Letter implements Cloneable {
        private final char sign;
        private final byte score;
        private int occurence;


        public Letter(char _sign, byte _score, int _occurence) {
            this.sign = _sign;
            this.score = _score;
            this.occurence = _occurence;
        }

        public int getOccurence() {
            return occurence;
        }

        public byte getScore() {
            return score;
        }

        public char getSign() {
            return sign;
        }

        public void decOccurence() {
            occurence--;
        }

        @Override
        protected Object clone()
        {
            return new Letter(sign,score,occurence);
        }
    }
}

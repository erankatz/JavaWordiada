package engine;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.validation.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * Created by eran on 21/03/2017.
 */
public class GameManager {
    private Deck deck;
    private final int NUMOFPLAYERS = 2;
    private Board board= new Board(10);
    Player players[] = new Player[NUMOFPLAYERS];
    private String dictionaryFileName;
    private int retriesNumber;
    private int cubeFacets = 6;
    private boolean isGameStarted;
    int currentPlayerTurn;

    public void gameManager()
    {
        this.dictionaryFileName = "war-and-piece.txt";
        this.retriesNumber = 2;
    }

    public Board getBoard()
    {
        return this.board;
    }

    public int getNumofCardInDeck()
    {
        return deck.getDeckSize();
    }

    public  void startGame()
    {
        isGameStarted = true;
        currentPlayerTurn = 1;
    }

    public Player[] getPlayers()
    {
        return players;
    }

    public int getCurrentPlayerTurn()
    {
        return currentPlayerTurn;
    }

    public void readXmlFile(String fileName)
    {
        try {
            File fXmlFile = new File(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            //-----------------------------------------
            Source xmlFile = new StreamSource(fXmlFile);
            File schemaFile = new File("C:\\d\\Wordiada.xsd");
            // or File schemaFile = new File("/location/to/xsd") etc.
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(schemaFile);
            Validator validator = schema.newValidator();
            try {
                validator.validate(xmlFile);
                System.out.println(xmlFile.getSystemId() + " is valid");
            } catch (SAXParseException e) {
            //TODO:  Need to add Excetion Handler
            System.out.println(xmlFile.getSystemId() + " is NOT valid reason:" + e);
            } catch (SAXException e)
            {
                System.out.println(xmlFile.getSystemId() + " is NOT valid reason:" + e);
            }
            //-------------------------------------------


            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            deck = new Deck(doc,xpath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createDictionary() throws  java.io.IOException{
        String bookStr = new String(Files.readAllBytes(Paths.get("C:\\d\\moby dick.txt")));
        String str = "[\\\\!?,.#:;\\-_=\\+\\*\"'\\(\\)\\{\\}\\[\\]%$\\r]";
        Pattern p = Pattern.compile(str);
        bookStr = bookStr.replace("\n", " ").replaceAll(p.pattern(),"");
        bookStr = bookStr.toUpperCase();
        String strWords[] = bookStr.split(" ");
        for(String w:strWords) {
            w=w.trim();
            if(w.length()>=2) {
                System.out.println(w);
            }
        }

    }
    public void newGame()
    {
        deck.NewGame();
        ArrayList<Card> initCards = new ArrayList<Card>();
        for (int i =0; i< this.board.getBoardSize()*this.board.getBoardSize();i++)
        {
            initCards.add(this.deck.removeTopCard());
        }
        this.board.setInitCards(initCards);
        for (Player player : players)
        {
            player = new Player(deck,board,new Dice(cubeFacets));
        }
        isGameStarted = false;
    }

    public void playTurn()
    {

    }

    public void getStatistics()
    {

    }

    public boolean isGameStarted() {
        return isGameStarted;
    }
}

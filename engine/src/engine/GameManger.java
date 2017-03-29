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


/**
 * Created by eran on 21/03/2017.
 */
public class GameManger {
    private Deck deck;
    private Board board= new Board(10);;

    private String dictionaryFileName;
    private int retriesNumber;
    private int cubeFacets;
    public void gameManager()
    {
        this.dictionaryFileName = "war-and-piece.txt";
        this.retriesNumber = 2;
    }

    public Board getBoard()
    {
        return this.board;
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

    public void newGame()
    {
        deck.NewGame();
        ArrayList<Card> initCards = new ArrayList<Card>();
        for (int i =0; i< this.board.getBoardSize()*this.board.getBoardSize();i++)
        {
            initCards.add(this.deck.removeTopCard());
        }
        this.board.setInitCards(initCards);
    }

    public void playTurn()
    {

    }

    public void getStatistics()
    {

    }
}

package engine;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;

import engine.exception.deck.DeckException;
import engine.exception.dice.DiceException;
import org.w3c.dom.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import sun.misc.JavaIOAccess;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.validation.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by eran on 21/03/2017.
 */
public class GameManager {
    private Deck deck;
    private final int NUMOFPLAYERS = 2;
    private Board board;
    Player players[] = new Player[NUMOFPLAYERS];
    private String dictionaryFilePath;
    private int retriesNumber;
    private int cubeFacets;
    private int boardSize;
    private boolean isGameStarted;
    private int roundCounter;
    private LocalTime gameStartedTime;
    private long totalWordsInDict;
    private boolean gameOver;
    private int winnerPlayer;


    public int getNumOfTurnsElapsed()
    {
        return this.roundCounter;
    }

    public int gutNumberofTurns()
    {
        return roundCounter;
    }


    public Duration getTimeElapsed()
    {
        return Duration.between(this.gameStartedTime,LocalTime.now());
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
        roundCounter = 0;
        this.gameStartedTime = LocalTime.now();
    }

    protected void wordRevealed(Map.Entry<String ,Long> word2Frequency){
        players[getCurrentPlayerTurn()].increaseScore(1);
        players[getCurrentPlayerTurn()].addComposedWord(word2Frequency);

    }

    public Player[] getPlayers()
    {
        return players;
    }

    public int getCurrentPlayerTurn()
    {
        return roundCounter % NUMOFPLAYERS ;
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
            URL schemaFile = GameManager.class.getResource("/resources/Wordiada.xsd");
            //File schemaFile = new File("C:\\d\\Wordiada.xsd");
            // or File schemaFile = new File("/location/to/xsd") etc.
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(schemaFile);
            Validator validator = schema.newValidator();
            try {
                validator.validate(xmlFile);
                System.out.println(xmlFile.getSystemId() + " is valid");
            } catch (SAXParseException e) {
            System.out.println(xmlFile.getSystemId() + " is NOT valid reason:" + e);
            } catch (SAXException e)
            {
                System.out.println(xmlFile.getSystemId() + " is NOT valid reason:" + e);
            }
            //-------------------------------------------


            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            XPathExpression expr;

            //Create deck
            deck = new Deck(doc,xpath);

            //GetDictionaryFileNameFromFile
            expr =  xpath.compile("/GameDescriptor/Structure/DictionaryFileName/text()");
            dictionaryFilePath =  fXmlFile.getParent() + "\\dictionary\\"+  (String)expr.evaluate(doc, XPathConstants.STRING);

            //GetNumberOfCubeFacetsFromFile
            expr =  xpath.compile("/GameDescriptor/Structure/CubeFacets/text()");
            cubeFacets = ((Number) expr.evaluate(doc, XPathConstants.NUMBER)).intValue();

            //GetBoardSizeFromFile
            expr =  xpath.compile("/GameDescriptor/Structure/BoardSize/text()");
            boardSize = ((Number) expr.evaluate(doc, XPathConstants.NUMBER)).intValue();

            //GetRetriesNumberFromFile
            expr =  xpath.compile("/GameDescriptor/Structure/RetriesNumber/text()");
            retriesNumber = ((Number) expr.evaluate(doc, XPathConstants.NUMBER)).intValue();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String,Long> createDictionary() throws  java.io.IOException{
        String bookStr = new String(Files.readAllBytes(Paths.get(dictionaryFilePath)));
        String str = "[\\\\!?,.#:;\\-_=\\+\\*\"'\\(\\)\\{\\}\\[\\]%$\\r]";
        Pattern p = Pattern.compile(str);
        bookStr = bookStr.replace("\n", " ").replaceAll(p.pattern(),"");
        bookStr = bookStr.toUpperCase();
        String strWords[] = bookStr.split(" ");
        return CreateMapAndCalcFrequency(strWords);

    }

    private Map<String, Long> CreateMapAndCalcFrequency(String[] words) {
        //key=name value=number of appearances
        Map<String, Long> frequency = new HashMap<>();
        List<String> filteredWords = Arrays.stream(words).filter(str->str.length()>1).collect(Collectors.toList());
        this.totalWordsInDict = filteredWords.size();
        for(String word : filteredWords) {
            Long currentCount = frequency.get(word); //check if word already exists
            if(currentCount == null) {
                currentCount = new Long(0); // auto-boxing
            }
            frequency.put(word, ++currentCount);
        }
        return frequency;
    }

    public void playerQuit(){
        isGameStarted = false;
        endPlayerTurn();
        gameOver = true;
    }

    public boolean isGameOver(){
        return gameOver;
    }
    public int getWinnerPlayer(){
        return getCurrentPlayerTurn();
    }

    public long getTotalWordsInDict(){
        return totalWordsInDict;
    }

    protected void endPlayerTurn(){
        this.roundCounter++;
    }

    public void newGame() throws java.io.IOException,DiceException,DeckException
    {
        deck.NewGame();
        ArrayList<Card> initCards = new ArrayList<Card>();
        this.board= new Board(boardSize,this,deck);
        for (int i =0; i< this.board.getBoardSize()*this.board.getBoardSize();i++)
        {
            initCards.add(this.deck.removeTopCard());
        }
        this.board.setInitCards(initCards);
        board.setDictionary(createDictionary());

        for (int i =0;i<players.length; i++)
        {
            players[i] = new Player(this,deck,board,new Dice(cubeFacets));
        }
        isGameStarted = false;
    }

    public Map<Character,Long> getCharFrequency()
    {
        return deck.getCharFrequency();
    }

    public Map<Character,Long> getInitCharFrequency()
    {
        return deck.getInitCharFrequency();
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }
}

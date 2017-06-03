package engine;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.*;
import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

import engine.exception.EngineException;
import engine.exception.board.BoardSizeOutOfRangeException;
import engine.exception.board.NotEnoughCardsToFillBoardException;
import engine.exception.dice.DiceException;
import engine.exception.file.FileException;
import engine.exception.file.FileExtensionException;
import engine.exception.letter.LetterException;
import engine.listener.*;
import org.w3c.dom.Document;
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
import java.util.stream.Collectors;

/**
 * Created by eran on 21/03/2017.
 */
public class GameManager implements Serializable{
    private List<CardRemovedListener> cardRemovedListeners = new ArrayList<>();
    private List<CardSelectedListener> cardSelectedListeners = new ArrayList<>();
    private List<DisableAllCardsListener> disableAllCardsListeners = new ArrayList<>();
    private List<EnableAllCardsListener> enableAllCardsListeners = new ArrayList<>();
    private List<CardChangedListener> cardChangedListeners = new ArrayList<>();
    private List<WordRevealedListener> wordRevealedListeners = new ArrayList<>();
    private List<RolledDicesListener> RolledDicesListeners = new ArrayList<>();
    private List<PlayerTurnListener> playerTurnListeners = new ArrayList<>();
    private List<LetterFrequencyInDeckListener> letterFrequencyInDeckListeners = new ArrayList<>();

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
    private boolean isGoldFishMode;

    public boolean getIsGoldFishMode(){
        return isGoldFishMode;
    }

    public int getNumOfTurnsElapsed()
    {
        return this.roundCounter;
    }

    //public int getRetriesNumber(){ return retriesNumber;}
    /*public int getNumberOfTurns()
    {
        return roundCounter;
    }*/


    public Duration getTimeElapsed()
    {
        return Duration.between(this.gameStartedTime,LocalTime.now());
    }

    public Board getBoard()
    {
        return this.board;
    }

    public int getNumOfCardInDeck()
    {
        return deck.getDeckSize();
    }

    public  void startGame()
    {
        isGameStarted = true;
        roundCounter = 0;
        this.gameStartedTime = LocalTime.now();
        notifyPlayerTurnListeners(getCurrentPlayerTurn());
        notifyLetterFrequencyInDeckListeners(getCharFrequency());
        if (isComputerMode()){
            while (!gameOver){
                notifyPlayerTurnListeners(getCurrentPlayerTurn());
                notifyLetterFrequencyInDeckListeners(getCharFrequency());
                ((ComputerPlayer)players[getCurrentPlayerTurn()]).playTurn();
                endPlayerTurn();
            }
        } else if (players[getCurrentPlayerTurn()] instanceof ComputerPlayer){

            ((ComputerPlayer)players[getCurrentPlayerTurn()]).playTurn();
        }
    }

    public boolean isComputerMode(){
        return players[0] instanceof ComputerPlayer && players[1] instanceof ComputerPlayer;
    }

    protected void wordRevealed(String word, long frequency){
        players[getCurrentPlayerTurn()].increaseScore(1);
        players[getCurrentPlayerTurn()].addComposedWord(word,frequency);

    }

    public Player[] getPlayers()
    {
        return players;
    }

    public int getCurrentPlayerTurn()
    {
        return roundCounter % NUMOFPLAYERS ;
    }

    public void readXmlFile (String filePath) throws java.io.IOException,LetterException,XPathExpressionException,BoardSizeOutOfRangeException,NotEnoughCardsToFillBoardException,FileExtensionException
    {
        try{
            File file = new File(filePath);
            readXmlFile(file);
        }catch (Exception ex){
            throw new FileException(filePath, ex);
        }
    }
    public void readXmlFile(File file) throws java.io.IOException,LetterException,XPathExpressionException,BoardSizeOutOfRangeException,NotEnoughCardsToFillBoardException,FileExtensionException
    {
        if (!file.getName().toLowerCase().endsWith(".xml")){
            throw new FileExtensionException (file.getAbsolutePath());
        }
        Document doc;
        File fXmlFile;
        try {
            fXmlFile = file;
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(fXmlFile);
        }catch (Exception ex){
            throw new FileException(file.getAbsolutePath(), ex);
        }

            doc.getDocumentElement().normalize();

            //-----------------------------------------
            Source xmlFile = new StreamSource(fXmlFile);
            URL schemaFile = GameManager.class.getResource("/resources/Wordiada.xsd");

            try {
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = schemaFactory.newSchema(schemaFile);
                Validator validator = schema.newValidator();
                validator.validate(xmlFile);
            } catch (SAXParseException e) {
            System.out.println(xmlFile.getSystemId() + " is NOT valid reason:" + e);
            } catch (SAXException e)
            {
                System.out.println(xmlFile.getSystemId() + " is NOT valid reason:" + e);
            } catch (IOException ex){
                throw new FileException(file.getAbsolutePath(),ex);
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

            if(boardSize <5 || boardSize> 50)
                throw new BoardSizeOutOfRangeException(boardSize);

            if( (boardSize * boardSize) > deck.getDeckSize())
                throw new NotEnoughCardsToFillBoardException();

            //GetRetriesNumberFromFile
            expr =  xpath.compile("/GameDescriptor/Structure/RetriesNumber/text()");
            retriesNumber = ((Number) expr.evaluate(doc, XPathConstants.NUMBER)).intValue();

            //CheckIfGoldFishMode
            expr =  xpath.compile("/GameDescriptor/GameType/@gold-fish-mode");
            isGoldFishMode = Boolean.parseBoolean((String) expr.evaluate(doc, XPathConstants.STRING));
    }

    public Map<String,Long> createDictionary() throws  java.io.IOException{
        String bookStr;
        try{
            bookStr = new String(Files.readAllBytes(Paths.get(dictionaryFilePath)));
        } catch (IOException ex){
            throw new FileException(Paths.get(dictionaryFilePath).toString(),ex);
        }
        String str = "[\\\\!?,.#:;\\-_=\\+\\*\"'\\(\\)\\{\\}\\[\\]%$\\r]";
        Pattern p = Pattern.compile(str);
        bookStr = bookStr.replace("\n", " ").replaceAll(p.pattern(),"");
        bookStr = bookStr.toUpperCase();
        List<String> strWords = Arrays.stream(bookStr.split(" ")).filter(word->word.length()>1).collect(Collectors.toList());
        return CreateMapAndCalcFrequency(strWords);

    }

    private Map<String, Long> CreateMapAndCalcFrequency(List<String> words) {
        //key=name value=number of appearances
        Map<String, Long> frequency = new HashMap<>();
        this.totalWordsInDict = words.size();
        for(String word : words) {
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
        return winnerPlayer;
    }

    public long getTotalWordsInDict(){
        return totalWordsInDict;
    }

    protected void endPlayerTurn(){
        notifyDisableAllCardsListeners();
        if ((deck.getDeckSize() == 0 && board.getNumOfUnrevealedCard() ==0) ||
                (this.isGoldFishMode && board.getNumberOfLegalWords(card->true) ==0)){
            gameOver =true;
            isGameStarted =false;
            if (players[0].getScore() > players[1].getScore()){
                winnerPlayer =0;
            } else if (players[0].getScore() == players[1].getScore()){
                winnerPlayer =-1;
            } else {
                winnerPlayer =1;
            }
        }
        this.roundCounter++;
        if (isGoldFishMode){
            board.ChangeAllCardsToUnrevealed();
        }
        players[getCurrentPlayerTurn()].setRetriesNumber(retriesNumber);

        if (!isComputerMode() && players[getCurrentPlayerTurn()] instanceof ComputerPlayer){
            ((ComputerPlayer)players[getCurrentPlayerTurn()]).playTurn();
        }
    }

    public void newGame(List<Boolean> booleanList) throws java.io.IOException,DiceException
    {
        notifyDisableAllCardsListeners();
        deck.NewGame();
        //deck.addMangerCardsListener(this);
        ArrayList<Card> initCards = new ArrayList<Card>();
        this.board= new Board(boardSize,this,deck);
        for (int i =0; i< this.board.getBoardSize()*this.board.getBoardSize();i++)
        {
            initCards.add(this.deck.removeTopCard());
        }
        this.board.setInitCards(initCards);
        board.setDictionary(createDictionary());
        board.addMangerCardsListener(this);
        notifyLetterFrequencyInDeckListeners(getCharFrequency());
        for (int i =0;i<players.length; i++)
        {
            if (booleanList.get(i))
                players[i] = new ComputerPlayer(this,deck,board,new Dice(cubeFacets));
            else{
                Player p = new Player(this,deck,board,new Dice(cubeFacets));
                p.registerRolledDicesListener(result -> notifyEnableAllCardsListeners());
                players[i] =p;
            }
            players[i].setRetriesNumber(retriesNumber);
        }
        gameOver =false;
        isGameStarted = false;
    }

    public Map<Character,Long> getCharFrequency()
    {
        return deck.CreateMapStructureCharToLong();
    }

    public Map<Character,Long> getInitCharFrequency()
    {
        return deck.getInitCharFrequency();
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public void saveGameToFile(String fullFileName) throws IOException
    {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream(fullFileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        } catch (IOException ex){
            throw new IOException(ex);
        }

    }

    public static GameManager loadGameFromFile(String fullFileName) throws IOException{
        GameManager e =null;
        try{
            FileInputStream fileIn = new FileInputStream(fullFileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            e = (GameManager) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException ex) {
            throw new IOException(ex);
        } catch (ClassNotFoundException ex){
            throw new IOException(ex);
        }

        return e;
    }

    public void registerDisableAllCardsListener(DisableAllCardsListener listener){
        disableAllCardsListeners.add(listener);
    }

    public void registerEnableAllCardsListener(EnableAllCardsListener listener){
        enableAllCardsListeners.add(listener);
    }

    public void registerCardSelectedListener(CardSelectedListener listener){
        cardSelectedListeners.add(listener);
    }

    public void registerCardChangedListener(CardChangedListener listener){
        cardChangedListeners.add(listener);
    }

    public void registerCardRemovedListener(CardRemovedListener listener ){
        cardRemovedListeners.add(listener);
    }

    public void registerRollDices(RolledDicesListener listener ){
        RolledDicesListeners.add(listener);
    }


    public void registerPlayerTurn(PlayerTurnListener listener ){
        playerTurnListeners.add(listener);
    }

    public void notifyRollDices(int result){
        RolledDicesListeners.forEach(listener->listener.rolldDice(result));
    }
    public void notifyDisableAllCardsListeners(){
        disableAllCardsListeners.forEach(listener->listener.disableAllCards());
    }

    public void notifyLetterFrequencyInDeckListeners(Map<Character,Long> frequency){
        letterFrequencyInDeckListeners.forEach(listener->listener.LetterFrequencyInDeck(frequency));
    }

    public void notifyPlayerTurnListeners(int playerId){
        playerTurnListeners.forEach(listener->listener.playerTurn(playerId));
    }

    public void notifyEnableAllCardsListeners(){
        enableAllCardsListeners.forEach(listener->listener.enableAllCards());
    }

    public void notifyCardSelectedListeners(int row,int col){
        cardSelectedListeners.forEach(listener->listener.selectCard(row,col));
    }

    public void notifyCardRemovedListeners(int row,int col){
        cardRemovedListeners.forEach(listener->listener.removeCard(row,col));
    }

    public void notifyCardChangedListener(Card card){
        cardChangedListeners.forEach(listener->listener.cardChanged(card));
    }

    public void registerWordRevealedListener(WordRevealedListener listener ){
        wordRevealedListeners.add(listener);
    }

    public void registerLetterFrequencyInDeckListener(LetterFrequencyInDeckListener listener ){
        letterFrequencyInDeckListeners.add(listener);
    }

    public void notifyWordRevealedListeners(boolean result){
        wordRevealedListeners.forEach(listener->listener.PrintResultOfWordRevealed(result));
    }
}
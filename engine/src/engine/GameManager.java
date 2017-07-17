package engine;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

import com.sun.org.apache.xpath.internal.operations.Bool;
import engine.exception.EngineException;
import engine.exception.board.BoardSizeOutOfRangeException;
import engine.exception.board.NotEnoughCardsToFillBoardException;
import engine.exception.dice.DiceException;
import engine.exception.file.FileException;
import engine.exception.file.FileExtensionException;
import engine.exception.letter.AlphabetExeption;
import engine.exception.letter.DuplicateLetterException;
import engine.exception.letter.LetterException;
import engine.exception.player.DuplicatePlayerIDException;
import engine.listener.*;
import engine.tasks.ComputerPlayerPlayTurnTask;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import sun.plugin.javascript.navig.Array;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.validation.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by eran on 21/03/2017.
 */
public class GameManager implements Serializable,Cloneable{
    private List<CardRemovedListener> cardRemovedListeners = new ArrayList<>();
    private List<CardSelectedListener> cardSelectedListeners = new ArrayList<>();
    private List<DisableAllCardsListener> disableAllCardsListeners = new ArrayList<>();
    private List<EnableAllCardsListener> enableAllCardsListeners = new ArrayList<>();
    private List<CardChangedListener> cardChangedListeners = new ArrayList<>();
    private List<WordRevealedListener> wordRevealedListeners = new ArrayList<>();
    private List<RolledDicesListener> RolledDicesListeners = new ArrayList<>();
    private List<PlayerTurnListener> playerTurnListeners = new ArrayList<>();
    private List<LetterFrequencyInDeckListener> letterFrequencyInDeckListeners = new ArrayList<>();
    private List<RevealWordPendingListener> revealWordPendingListeners = new ArrayList<>();
    private List<RevealCardPendingListener> revealCardPendingListeners = new ArrayList<>();
    private List<RollDicesPendingListener> rollDicesPendingListeners = new ArrayList<>();
    private List<GameOverListener> gameOverListeners = new ArrayList<>();
    private List<PlayerDataChangedListener> playerDataChangedListeners = new ArrayList<>();

    private Deck deck;
    private Board board;
    private String title;
    Player players[];
    private String bookStr;
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
    private List<Move> moves = new ArrayList<>();
    private int totalNumberofTurnsElapses = 0;
    private boolean replayMode = false;
    private EnumScoreMode scoreMode;
    private int NumOfRequiredPlayers;
    private String dictName;

    public boolean getIsGoldFishMode(){
        return isGoldFishMode;
    }

    public int getCurrentNumOfTurnsElapsed()
    {
        return this.roundCounter;
    }

    //public int getRetriesNumber(){ return retriesNumber;}
    /*public int getNumberOfTurns()
    {
        return roundCounter;
    }*/

    public void setPlayers(ArrayList<Player> players){
        this.players = players.toArray(new Player[players.size()]);
    }

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

    public int getNumOfChars(){
        return deck.getNumOfChars();
    }
    private void createNewMove(){
        if (!gameOver){
            Move move = new Move();
            move.setBoard(board.clone());
            Player[] players2 = new Player[this.players.length];
            AtomicInteger i = new AtomicInteger(0);
            Arrays.stream(this.players).forEach(pl->players2[i.getAndAdd(1)] = pl.clone());
            move.setPlayers(players2);
            move.setCurrentPlayerIndex(getCurrentPlayerTurn());
            move.setPlayersData(getPlayersData());
            move.setManager(this);
            moves.add(move);
        }
    }

    public  void startGame()
    {
        for (int i =0;i<players.length; i++)
        {
            try{
                players[i].setDice(new Dice(cubeFacets));
            } catch (Exception ex){
                ex.printStackTrace();
                System.exit(1);
            }
            players[i].setRetriesNumber(retriesNumber);
            players[i].registerRolledDicesListener((result) -> notifyEnableAllCardsListeners());
        }
        isGameStarted = true;
        roundCounter = 0;
        replayMode = false;
        this.gameStartedTime = LocalTime.now();
        createNewMove();
        notifyPlayerTurnListeners(getCurrentPlayerTurn());
        notifyLetterFrequencyInDeckListeners(getCharFrequency());
        if (players[getCurrentPlayerTurn()] instanceof ComputerPlayer){
            ComputerPlayerPlayTurnTask task = new ComputerPlayerPlayTurnTask((ComputerPlayer)players[getCurrentPlayerTurn()]);
            new Thread(task).start();
        }
        Arrays.stream(players).forEach(pl->pl.setScore(0));
        getPlayersData().stream().forEach(pl ->notifyPlayerDataChangedListener(pl));
        notifyStartPlayerTurn();
    }

    public boolean isComputerMode(){
        long computerPlayers = Arrays.stream(players).filter(pl -> pl instanceof  ComputerPlayer).count();
        return computerPlayers == players.length;
    }

    protected void wordRevealed(String word, long frequency){
            Long score = null;
            if (scoreMode == EnumScoreMode.WORDCOUNT){
                players[getCurrentPlayerTurn()].addComposedWord(word,1);
            }
            else {
                AtomicLong sumOfCharsScore = new AtomicLong(0);
                word.chars().forEach(ch->sumOfCharsScore.addAndGet(board.getDeck().getScoreLetter((char)ch)));
                score = sumOfCharsScore.get() * board.getWord2Segment(word);
                players[getCurrentPlayerTurn()].addComposedWord(word,score);
            }
        if (score != null){
            notifyWordRevealedListeners(word,score.intValue());
        } else {
            notifyWordRevealedListeners(word,1);
        }
        Utils.sleepForAWhile(Utils.sleepTime);
        Player pl = players[getCurrentPlayerTurn()];
            if (players[getCurrentPlayerTurn()] instanceof ComputerPlayer)
                notifyPlayerDataChangedListener(new PlayerData("Computer",pl.getId(),pl.getName(),pl.getScore(),getCurrentPlayerTurn()));
            else
                notifyPlayerDataChangedListener(new PlayerData("Human",pl.getId(),pl.getName(),pl.getScore(),getCurrentPlayerTurn()));

    }


    public Player[] getPlayers()
    {
        return players;
    }

    public int getCurrentPlayerTurn()
    {
        if (roundCounter == 0){
            return 0;
        }
        return roundCounter % players.length ;
    }

    //public void readXmlFile (String filePath) throws java.io.IOException,LetterException,XPathExpressionException,BoardSizeOutOfRangeException,NotEnoughCardsToFillBoardException,FileExtensionException
    //{
    //    try{
    //        File file = new File(filePath);
    //        readXmlFile(file);
    //    }catch (Exception ex){
    //        throw new FileException(filePath, ex);
    //    }
    //}
    public void readXmlFile(String xmlDescription,String dictionaryContent) throws java.io.IOException,LetterException,XPathExpressionException,BoardSizeOutOfRangeException,NotEnoughCardsToFillBoardException,FileExtensionException,DuplicatePlayerIDException
    {
        //if (!file.getName().toLowerCase().endsWith(".xml")){
        //    throw new FileExtensionException (file.getAbsolutePath());
        //}
        bookStr = dictionaryContent;
        Document doc;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            final InputStream stream = new ByteArrayInputStream(xmlDescription.getBytes(StandardCharsets.UTF_8));
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = builder.parse(stream);
        }catch (Exception ex){
            throw new FileException(xmlDescription, ex);
        }

            doc.getDocumentElement().normalize();

            //-----------------------------------------
            /*URL schemaFile = GameManager.class.getResource("/resources/Wordiada.xsd");

            try {
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = schemaFactory.newSchema(schemaFile);
                Validator validator = schema.newValidator();
                validator.validate(file);
            } catch (SAXParseException e) {
            System.out.println(xmlFile.getSystemId() + " is NOT valid reason:" + e);
            } catch (SAXException e)
            {
                System.out.println(xmlFile.getSystemId() + " is NOT valid reason:" + e);
            } catch (IOException ex){
                throw new FileException(file.getAbsolutePath(),ex);
            }
            //-------------------------------------------
            */

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            XPathExpression expr;

            //Create deck
            deck = new Deck(doc,xpath);

            //Create Players
            //players = readPlayersFromXml(doc,xpath);
            //    for (Player pl1 : players) {
            //        int i = 0;
            //        for (Player pl2 : players) {
            //            if (pl2.getId().contentEquals(pl1.getId())) {
            //                i++;
            //            }
            //            if (i == 2) {
            //                throw new DuplicatePlayerIDException(pl1.getId());
            //            }
            //        }
            //    }

            //Get Title
            expr =  xpath.compile("/GameDescriptor/DynamicPlayers/@game-title");
            title = (String)expr.evaluate(doc, XPathConstants.STRING);

            //Get Required players
            expr =  xpath.compile("/GameDescriptor/DynamicPlayers/@total-players");
            NumOfRequiredPlayers = ((Number) expr.evaluate(doc, XPathConstants.NUMBER)).intValue();

            //GetDictionaryFileNameFromFile
            expr =  xpath.compile("/GameDescriptor/Structure/DictionaryFileName/text()");
            dictName = (String)expr.evaluate(doc, XPathConstants.STRING);
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

        //scoreMode
        expr =  xpath.compile("/GameDescriptor/GameType/@winner-according-to");
        String scoreMode = (String) expr.evaluate(doc, XPathConstants.STRING);
        if(scoreMode.equals("WordScore")){
            this.scoreMode = EnumScoreMode.WORDSCORE;
        }else{ //WordCount
            this.scoreMode = EnumScoreMode.WORDCOUNT;
        }
    }

    private Player[] readPlayersFromXml(Document doc, XPath xpath) throws XPathExpressionException {
        XPathExpression expr = xpath.compile("/GameDescriptor/Players/Player");
        NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        Player[] players = new Player[nodes.getLength()];
        for (int i = 0; i < nodes.getLength(); i++){
            Element element = (Element)nodes.item(i);
            String name = element.getElementsByTagName("Name").item(0).getTextContent();
            String id = element.getAttribute("id");

            //Create
            String type = element.getElementsByTagName("Type").item(0).getTextContent();
            if (type.equals("Human")){
                players[i] = new Player(this,id,name);
            } else if (type.equals("Computer")){
                players[i] = new ComputerPlayer(this,id,name);
            }
        }
        return players;
    }

    public String getDictName(){
        return dictName;
    }

    public Map<String,Long> createDictionary(){
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
        players[getCurrentPlayerTurn()].QuitFromGame();
        long i = Arrays.stream(players).filter(player ->player.getisQuiteFromGame() ==true ).count();
        if (isGameStarted ==true){
            if (i == (players.length -1)){
                isGameStarted = false;
                gameOver = true;
                endPlayerTurn();
                notifyGameOverListeners(getCurrentPlayerTurn());
            } else {
                endPlayerTurn();
            }
        }
    }

    public void playerLeave(String playerName){
        Optional<Player> quitedPlayer = Arrays.stream(players).filter(pl->pl.getName().contentEquals(playerName)).findFirst();

        if (quitedPlayer.isPresent()){
            quitedPlayer.get().QuitFromGame();
            players = Arrays.stream(players).filter(pl2-> pl2 != quitedPlayer.get()).toArray(Player[]::new);
        }
        if (players.length ==1){
            isGameStarted = false;
            gameOver = true;
            endPlayerTurn();
            notifyGameOverListeners(getCurrentPlayerTurn());
        } else {
            endPlayerTurn();
        }
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

    protected synchronized void endPlayerTurn(){
        notifyDisableAllCardsListeners();
        this.roundCounter++;
        while (players[getCurrentPlayerTurn()].getisQuiteFromGame() == true){
            this.roundCounter++;
        }
        if ((deck.getDeckSize() == 0 && board.getNumOfUnrevealedCard() ==0) || gameOver){
                //|| (this.isGoldFishMode && board.getNumberOfLegalWords(card->card!=null) ==0 || gameOver)){
            gameOver =true;
            isGameStarted =false;
            if (players[0].getScore() > players[1].getScore()){
                winnerPlayer =0;
            } else if (players[0].getScore() == players[1].getScore()){
                winnerPlayer =-1;
            } else {
                winnerPlayer =1;
            }
            notifyGameOverListeners(getCurrentPlayerTurn());
        } else {
            board.clearSelectedCards();
            notifyStartPlayerTurn();
            createNewMove();
            if (isGoldFishMode){
                board.ChangeAllCardsToUnrevealed();
            }
            players[getCurrentPlayerTurn()].setRetriesNumber(retriesNumber);
            if (players[getCurrentPlayerTurn()] instanceof ComputerPlayer){
                ComputerPlayerPlayTurnTask task = new ComputerPlayerPlayTurnTask((ComputerPlayer)players[getCurrentPlayerTurn()]);
                new Thread(task).start();
            }
        }
    }

    public synchronized void playMove(int index){
        //TODO: Handle no exist move
        this.roundCounter = index;
        AtomicInteger i= new AtomicInteger(0);
        moves.get(index).getPlayersData().forEach(pl->players[i.getAndAdd(1)].setScore(pl.getScore()));
        Move move = moves.get(index).clone();
        this.board = move.getBoard().clone();
        this.deck = move.getBoard().getDeck();
        this.players = move.getPlayers();
        this.replayMode = true;
        this.gameOver = false;
        this.isGameStarted = true;
        move.setManager(this);
        move.playMove();
    }

    private void notifyStartPlayerTurn(){
        if (board.getNumOfUnrevealedCard() != 0){
            notifyRollDicesPendingListener(true);
            notifyRevealCardPendingListener(false);
            notifyRevealWordPendingListener(false);
        }
        else{
            notifyRollDicesPendingListener(false);
            notifyRevealCardPendingListener(false);
            notifyRevealWordPendingListener(true);
        }
        notifyEnableAllCardsListeners();
        notifyPlayerTurnListeners(getCurrentPlayerTurn());
    }

    public void newGame() throws java.io.IOException,DiceException
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
        if (scoreMode == EnumScoreMode.WORDSCORE)
            board.createWord2Segment();
        board.addMangerCardsListener(this);
        notifyLetterFrequencyInDeckListeners(getCharFrequency());
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

    public void registerRevealWordPendingListener(RevealWordPendingListener listener ){
        revealWordPendingListeners.add(listener);
    }

    public void registerRevealCardPendingListener(RevealCardPendingListener listener ){
        revealCardPendingListeners.add(listener);
    }

    public void registerRollDicesPendingListeners(RollDicesPendingListener listener){
        rollDicesPendingListeners.add(listener);
    }

    public void registerGameOverListener(GameOverListener listener){
        gameOverListeners.add(listener);
    }


    public void registerPlayerDataChangedListener(PlayerDataChangedListener listener){
        playerDataChangedListeners.add(listener);
    }

    public void notifyRollDices(int result){
        moves.get(roundCounter).setDiceResult(result);
        RolledDicesListeners.forEach(listener->listener.rolldDice(result));
    }
    public void notifyDisableAllCardsListeners(){
        disableAllCardsListeners.forEach(listener->listener.disableAllCards());
    }

    public void notifyLetterFrequencyInDeckListeners(Map<Character,Long> frequency){
        letterFrequencyInDeckListeners.forEach(listener->listener.LetterFrequencyInDeck(frequency));
    }


    public void notifyRevealWordPendingListener(boolean isPending){
        revealWordPendingListeners.forEach(listener->listener.isWordPendingToBeRevealed(isPending));
    }

    public void notifyRevealCardPendingListener(boolean isPending){
        revealCardPendingListeners.forEach(listener->listener.isCardPendingToBeRevealed(isPending));
    }


    public void notifyRollDicesPendingListener(boolean isPending){
        rollDicesPendingListeners.forEach(listener->listener.isRollDicesToBeRevealed(isPending));
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

    public void notifyWordRevealedListeners(String word,int score){
        wordRevealedListeners.forEach(listener->listener.PrintResultOfWordRevealed(word,score));
    }

    public void notifyGameOverListeners(int id){
        totalNumberofTurnsElapses = roundCounter;
        gameOverListeners.forEach(listener->listener.gameOver(id));
    }

    public void notifyPlayerDataChangedListener(PlayerData score){
        playerDataChangedListeners.forEach(listener->listener.updateScore(score));
    }

    public List<PlayerData> getPlayersData(){
        return  Arrays.stream(players).map(pl-> {
            if (pl instanceof ComputerPlayer)
                return new PlayerData("Computer",pl.getId(),pl.getName(),pl.getScore(),getCurrentPlayerTurn());
            else
                return new PlayerData("Human",pl.getId(),pl.getName(),pl.getScore(),getCurrentPlayerTurn());
        }).collect(Collectors.toList());
    }

    protected void setRevealCardsMove(List<Map.Entry<Integer,Integer>> revealCardsMove) {
        if (!replayMode){
            List<Card> revealedCards = new ArrayList<>();
            revealCardsMove.forEach(pair-> {
                try {
                    revealedCards.add(board.getBoardCard(pair.getKey(), pair.getValue()));
                    moves.get(roundCounter).setRevealedCards(revealedCards);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            });
            moves.get(roundCounter).setRevealedCards(revealedCards);
        }
    }

    protected void addRevealWordMove(List<Map.Entry<Integer,Integer>> revealCardsMove){
        if (!replayMode){
            List<Card> revealedCards = new ArrayList<>();
            revealCardsMove.forEach(pair-> {
                try {
                    revealedCards.add(board.getBoardCard(pair.getKey(), pair.getValue()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            });
            moves.get(roundCounter).addRevealWordTry(revealedCards);
        }
    }

    public int getNumOfRequiredPlayers(){
        return NumOfRequiredPlayers;
    }

    public int getTotalNumberofTurnsElapses() {
        return totalNumberofTurnsElapses;
    }

        public boolean getIsReplayMode(){
        return replayMode;
    }

    public EnumScoreMode getScoreMode() {
        return scoreMode;
    }

    public void updateCards() {
        board.updateCards();
    }

    public String getGameTitle(){
            return title;
    }

    public void addPlayer(Player player) {
        if (players == null){
            players = new Player[1];
            players[0] = player;
        } else {
            Player[] temp = new Player[players.length +1];
            for (int i = 0; i < players.length;i++){
                temp[i] = players[i];
            }
            temp[players.length] = player;
            players = temp;
        }
    }
}
package console;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.util.*;

import engine.GameManager;
import engine.exception.deck.DeckException;
import engine.exception.dice.DiceException;
import engine.exception.dice.WrongNumberOfDiceFacetExecption;
import sun.rmi.runtime.Log;

public class MainMenu {
    private UIBoard board;
    private UIPlayer player;
    private Map<Character,Long> currCharFreq;

    public void run ()
    {
        int swValue = getOption();
        GameManager manager = null;
        int currentPlayerTurn;
        //Display menu graphics
        while (swValue != 7)
        {
            switch (swValue){
                case 1:
                    if (manager != null && manager.isGameStarted())
                    {
                        System.out.println("The Game already started");
                    } else {
                        try{
                            manager = new GameManager();
                            manager.readXmlFile("C:\\d\\basic_1.xml");
                            manager.createDictionary();
                            manager.newGame(getComputeBooleanrArray());
                            board = new UIBoard(manager.getBoard());
                            board.printGameBoard();
                            System.out.format("Number of cards in deck %d\n",manager.getNumofCardInDeck());
                        } catch (WrongNumberOfDiceFacetExecption ex){
                            System.out.format("Wrong number of Facets (%d)",ex.getNumOfFacet());
                        } catch (DiceException ex){
                            System.out.println(ex.getMessage());
                        } catch (java.io.IOException ex) {
                            System.out.println(ex.getMessage());
                        } catch (DeckException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                    break;
                case 2:
                    if (manager != null && !manager.isGameStarted())
                    {
                        if (manager.isGameOver())
                        {
                            try {
                                manager.createDictionary();
                                manager.newGame(getComputeBooleanrArray());
                                board = new UIBoard(manager.getBoard());
                            } catch (WrongNumberOfDiceFacetExecption ex){
                                System.out.format("Wrong number of Facets (%d)",ex.getNumOfFacet());
                            } catch (DiceException ex){
                                System.out.println(ex.getMessage());
                            } catch (IOException ex){
                                System.out.println(ex.getMessage());
                            } catch (DeckException ex) {
                                System.out.println(ex.getMessage());
                            }
                        }

                        manager.startGame();
                        this.player = new UIPlayer(manager.getPlayers(),manager);
                        board.printGameBoard();
                        System.out.format("Number of cards in deck %d\n",manager.getNumofCardInDeck());
                    } else {
                        System.out.println("The Game Not Loaded or already started");
                    }
                    break;
                case 3:
                    if (manager != null && manager.isGameStarted()){
                        printGameStatus(manager);
                    } else {
                        System.out.println("The Game not started");
                    }
                    break;
                case 4:
                    if (manager != null && manager.isGameStarted())
                    {
                        this.player.playTurn();
                    }else {
                        System.out.println("The Game not started");
                    }
                    break;
                case 5:
                    if (manager != null && manager.isGameStarted()) {
                        printStatistics(manager);
                    }else {
                        System.out.println("The Game not started");
                    }
                    break;
                case 6:
                    if (manager != null && manager.isGameStarted()) {
                        manager.playerQuit();
                        System.out.format("Player %d is the winner\n", manager.getWinnerPlayer()+1);
                        board.printGameBoard();
                        printStatistics(manager);
                    } else{
                        System.out.println("The Game not started");
                    }
                    break;
                case 7:
                    System.out.println("Exit");
                    break;
            }

            swValue = getOption();
        }
    }

    private List<Boolean> getComputeBooleanrArray()  {
        List<Boolean> isComputerPlayerList = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        for (int i=0;i<2;i++){
            System.out.println("Is Player " + (i +1) + " is Computer Player ?\n Type Y/N");
            String userInput;
            do{
                userInput = sc.nextLine();
            } while (!(userInput.equals("Y") || userInput.equals("N")));

            if (userInput.equals("Y")){
                isComputerPlayerList.add(true);
            }else{
                isComputerPlayerList.add(false);
            }
        }
        return isComputerPlayerList;
    }
    private void printGameStatus(GameManager manager)
    {
        board.printGameBoard();
        System.out.format("Number of cards in the deck %d \n",manager.getNumofCardInDeck());
        System.out.format("Player %d Turn\n",manager.getCurrentPlayerTurn() +1);
    }
    private void printStatistics(GameManager manager)
    {
        System.out.format("Number of Turns Elapsed: %d \n", manager.getNumOfTurnsElapsed());
        System.out.format("Time elapes:\t %d:%d \n",manager.getTimeElapsed().getSeconds() /60 ,manager.getTimeElapsed().getSeconds() % 60);
        System.out.format("Number of cards in the deck %d \n",manager.getNumofCardInDeck());
        this.currCharFreq = manager.getCharFrequency();
        manager.getInitCharFrequency()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e1->printCharFrequency(e1.getKey(),e1.getValue()));
        this.player.printPlayerStatistics();

    }

    private void printCharFrequency(char ch,long initFreq)
    {
        Long currCharFrequency;
        if ((currCharFrequency = currCharFreq.get(ch)) == null){
            System.out.format("%c - %d/%d\n",ch,0,initFreq);
        } else {
            System.out.format("%c - %d/%d\n",ch,currCharFrequency,initFreq);
        }
    }
    private int getOption()
    {
        System.out.println("==========================");
        System.out.println("=======Main Menu==========");
        System.out.println("==========================");
        System.out.println("1. Read File");
        System.out.println("2. Start Game");
        System.out.println("3. Show Game Status");
        System.out.println("4. Play Turn");
        System.out.println("5. Get Statistics");
        System.out.println("6. Quit Game");
        System.out.println("7. Exit");
        System.out.println("==========================");
        System.out.println("==========================");
        boolean check;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        int i = 0;
        do {
            check = true;
            try {
                i = Integer.parseInt(in.readLine());
            }
            catch (NumberFormatException e)
            {
                System.err.println("Input error - Invalid value for number.");
                System.out.print("Reinsert: ");
                check =false;
            }
            catch (IOException e)
            {
                System.exit(0);
            }
            if (!(i > 0) && (i <= 7))
            {
                System.out.println("Input error - enter number between 1 to 6");
                check = false;
            }
        } while (!check);
        return i;
    }
}



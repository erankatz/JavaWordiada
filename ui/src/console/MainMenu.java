package console;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import engine.GameManager;
import sun.rmi.runtime.Log;

public class MainMenu {
    private UIBoard board;
    private UIPlayer player;
    private Map<Character,Long> currCharFreq;

    public void run () throws  java.io.IOException
    {
        int swValue = getOption();
        GameManager manager = null;
        int currentPlayerTurn;
        //Display menu graphics
        while (swValue != 6)
        {
            switch (swValue){
                case 1:
                    if (manager != null && manager.isGameStarted())
                    {
                        System.out.println("The Game already started");
                    } else {
                        manager = new GameManager();
                        manager.readXmlFile("C:\\d\\basic_1.xml");
                        manager.createDictionary();
                        manager.newGame();
                        board = new UIBoard(manager.getBoard());
                        board.printGameBoard();
                        this.player = new UIPlayer(manager.getPlayers(),manager);
                        System.out.format("Number of cards in deck %d\n",manager.getNumofCardInDeck());
                    }
                    break;
                case 2:
                    if (manager != null && !manager.isGameStarted())
                    {
                        manager.startGame();
                        board.printGameBoard();
                        System.out.format("Number of cards in deck %d\n",manager.getNumofCardInDeck());
                    } else {
                        System.out.println("The Game Not Loaded or already started");
                    }
                    break;
                case 3:
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
                    break;
            }

            swValue = getOption();
        }
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
        this.player.printPlayerstatistics();

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
        System.out.println("6. Exit");
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
            if (!(i > 0) && (i < 7))
            {
                System.out.println("Input error - enter number between 1 to 6");
                check = false;
            }
        } while (!check);
        return i;
    }
}



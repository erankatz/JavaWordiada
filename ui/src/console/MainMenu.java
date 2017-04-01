package console;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import engine.GameManager;

public class MainMenu {
    UIBoard board;
    UIPlayer player;

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
                    this.player.playTurn();
                    break;
                case 5:
                    break;
                case 6:

            }

            swValue = getOption();
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



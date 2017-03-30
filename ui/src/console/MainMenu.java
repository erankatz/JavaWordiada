package console;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.Scanner;
import java.util.InputMismatchException;
import com.sun.prism.shader.Solid_TextureYV12_AlphaTest_Loader;
import engine.GameManger;

public class MainMenu {
    UIBoard board;

    public void run()
    {
        int swValue = getOption();
        GameManger manger = null;
        int currentPlayerTurn;
        UIPlayer player;
        //Display menu graphics
        while (swValue != 6)
        {
            switch (swValue){
                case 1:
                    if (manger != null && manger.isGameStarted())
                    {
                        System.out.println("The Game already started");
                    } else {
                        manger = new GameManger();
                        manger.readXmlFile("C:\\d\\basic_1.xml");
                        manger.newGame();
                        board = new UIBoard(manger.getBoard());
                        board.printGameBoard();
                        player = new UIPlayer(manger.getPlayers(),manger);
                        System.out.format("Number of cards in deck %d\n",manger.getNumofCardInDeck());
                    }
                    break;
                case 2:
                    if (manger != null && !manger.isGameStarted())
                    {
                        manger.startGame();
                        currentPlayerTurn = 1;
                        board.printGameBoard();
                        System.out.format("Number of cards in deck %d\n",manger.getNumofCardInDeck());
                    } else {
                        System.out.println("The Game Not Loaded or already started");
                    }
                    break;
                case 3:
                    break;
                case 4:
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



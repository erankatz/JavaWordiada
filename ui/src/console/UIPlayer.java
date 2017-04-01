package console;

import engine.GameManager;
import engine.Player;

/**
 * Created by eran on 30/03/2017.
 */
public class UIPlayer {
    Player player[];
    GameManager manager;

    public UIPlayer(Player player[], GameManager manager)
    {
        this.player = player;
    }

    public void printCurrentPlayerTurn()
    {
        System.out.format("Player Turn: %d",manager.getCurrentPlayerTurn());
    }

    public void playTurn()
    {
        System.out.println("c");
        Player currentPlayer = player[manager.getCurrentPlayerTurn()];
        System.out.format("Pick %d Cards in the board",currentPlayer.rollDice());
        while (currentPlayer.isLeftCardsToReveal())
        {
            //System.in.
            boolean succeeded = false;
            try {
                //currentPlayer.revealCard();
            } catch (Exception ex)
            {
                succeeded =false;
            }

        }



    }

}

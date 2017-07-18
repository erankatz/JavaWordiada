package engine.message;


import engine.EnumPlayerTurnPendingAction;
import engine.GameStatus;

/**
 * Created by user on 20/10/2016.
 */
public class GameStatusMessage
{
    GameStatus status;
    String currentPlayerTurnName;
    EnumPlayerTurnPendingAction pendingAction;

    public GameStatusMessage(GameStatus status, String currentPlayerTurnName, EnumPlayerTurnPendingAction action)
    {
        this.status = status;
        this.currentPlayerTurnName = currentPlayerTurnName;
        this.pendingAction =action;
    }

    public GameStatus getStatus()
    {
        return status;
    }

    public void setStatus(GameStatus status)
    {
        this.status = status;
    }

    public String getCurrentPlayerTurnName()
    {
        return currentPlayerTurnName;
    }

    public void setCurrentPlayerTurnName(String currentPlayerTurnName)
    {
        this.currentPlayerTurnName = currentPlayerTurnName;
    }
}

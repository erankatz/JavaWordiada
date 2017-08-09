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
    String otherPlayerMessage;
    EnumPlayerTurnPendingAction pendingAction;

    public GameStatusMessage(GameStatus status, String currentPlayerTurnName, EnumPlayerTurnPendingAction action,String otherPlayerMessage)
    {
        this.status = status;
        this.currentPlayerTurnName = currentPlayerTurnName;
        this.pendingAction =action;
        if (status == GameStatus.Running){
            if (action != EnumPlayerTurnPendingAction.ROLLDICE){
                this.otherPlayerMessage = otherPlayerMessage;
            } else {
                this.otherPlayerMessage = "The Player Turn " + currentPlayerTurnName + " Started ";
            }
        }else {
            this.otherPlayerMessage = "Game Not Started waiting for players";
        }

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

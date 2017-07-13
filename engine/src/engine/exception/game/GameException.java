package engine.exception.game;

import engine.exception.EngineException;

/**
 * Created by eran on 13/07/2017.
 */
public class GameException extends EngineException{
    public String getMessage(){
        return "Game title is allready in use.";
    }

}

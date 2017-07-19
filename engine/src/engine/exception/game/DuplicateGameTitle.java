package engine.exception.game;

/**
 * Created by eran on 13/07/2017.
 */
public class DuplicateGameTitle extends GameException{
    private String msg;

    public DuplicateGameTitle(String title) {

        msg = "The Game " + title + " already exist";
    }

    @Override
    public String getMessage(){
        return msg;
    }
}

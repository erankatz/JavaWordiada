package engine.exception.player;

/**
 * Created by eran on 30/06/2017.
 */
public class DuplicatePlayerIDException extends PlayerException {
    String id;
    public DuplicatePlayerIDException(String id){
        this.id = id;
    }

    @Override
    public String getMessage(){
        return "Duplicate player id (" + id + ")";
    }

}

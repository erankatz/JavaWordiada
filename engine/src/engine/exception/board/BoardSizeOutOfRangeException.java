package engine.exception.board;

/**
 * Created by eran on 22/04/2017.
 */
public class BoardSizeOutOfRangeException extends BoardException {
    private int size;

     public BoardSizeOutOfRangeException (int size){
         this.size = size;
     }

    @Override
    public String getMessage(){
        return "The board size should be in the range 5 to 50.\n" +
                "In the XML file the given size is: "+ size;
    }
}

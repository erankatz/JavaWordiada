import engine.Card;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.event.Event;
import javafx.scene.control.*;


/**
 * Created by eran on 20/05/2017.
 */
public class CardUI extends javafx.scene.control.Button{
    GameModel model;
    int row;
    int col;

    public CardUI(GameModel model,int row,int col){
        this.model = model;
        this.row = row;
        this.col = col;

        //this.disableProperty().bind(Bindings.createBooleanBinding(()->!model.getIsEnabledCard(row,col)));
        //this.textProperty().bind(Bindings.format("%c",model.getCardLetter(row,col)));
        this.setOnMouseClicked((Event e) -> {

        });

    }
}
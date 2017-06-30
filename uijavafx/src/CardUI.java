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
        //this.autosize();
        //this.disableProperty().bind(Bindings.createBooleanBinding(()->!model.getIsEnabledCard(row,col)));
        //this.textProperty().bind(Bindings.format("%c",model.getCardLetter(row,col)));
        this.setOnMouseClicked((Event e) -> {
            model.selectCard(row,col);
        });
    }

    public void setStyleSelected(){
        //setStyle("-fx-background-color: yellow");
        getStyleClass().clear();
        getStyleClass().add("card-Selected");
        getStyleClass().add("button");
        setDisable(true);
    }

    public void setStyleEmpty(){
        getStyleClass().clear();
        getStyleClass().add("button");
        getStyleClass().add("card-Empty");
        setText("");
    }

    public void setStyleUndefined(Card c){
        setDisable(false);
        getStyleClass().clear();
        getStyleClass().add("button");
        //setStyle("-fx-base: #d3f3ff");
        if (c.getLetter() == '?')
            getStyleClass().add("card-Hidden");
        else
            getStyleClass().add("card-UnHidden");
    }
}
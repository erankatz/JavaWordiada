import engine.Card;
import javafx.beans.binding.Bindings;
import javafx.scene.control.*;


/**
 * Created by eran on 20/05/2017.
 */
public class CardUI extends javafx.scene.control.Button{
    Card logicCard;

    public CardUI(Card card){
        this.setVisible(true);
        logicCard =card;
        this.textProperty().bind(Bindings.format("%c",card.getLetter()));
    }

}

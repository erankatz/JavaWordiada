import com.sun.xml.internal.ws.api.pipe.Engine;
import engine.Board;
import engine.Card;
import engine.GameManager;
import engine.exception.EngineException;
import engine.exception.file.FileException;
import javafx.application.Platform;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by eran on 25/05/2017.
 */
public class BoardButtonController implements Initializable {
    @FXML
    private GridPane gridPaneBoard;
    @FXML
    private ScrollPane scrollPane;
    GameModel model;
    private int rows;
    private int columns;
    public void initialize(URL fxmlFileLocation, ResourceBundle resources){
        gridPaneBoard.getColumnConstraints().clear();
        gridPaneBoard.getRowConstraints().clear();
    }


    private void createRowsAndCols(){
        for (int i = 0; i < rows; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / columns);
            gridPaneBoard.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < columns; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / rows);
            gridPaneBoard.getRowConstraints().add(rowConst);
        }
        gridPaneBoard.setPrefHeight(500);
        gridPaneBoard.setPrefWidth(500);
    }

    private void createButtons(){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                    CardUI card = new CardUI(model,i+1,j+1);
                    gridPaneBoard.add(card,i,j);
                    card.setPrefSize(50,50);
            }
        }
    }

    public void draw()
    {
        new StringBuilder();
        gridPaneBoard.getRowConstraints().clear();
        gridPaneBoard.getColumnConstraints().clear();
        this.gridPaneBoard.setAlignment(Pos.CENTER);
        this.gridPaneBoard.setHgap(0);
        this.gridPaneBoard.setVgap(0);
        this.rows = model.getBoardSize();
        this.columns = model.getBoardSize();
        createRowsAndCols();
        createButtons();
    }




    public void setModel(GameModel model) {
        this.model = model;
        if (model != null)
            draw();
        gridPaneBoard.setDisable(true);
    }

    public void setDisable(Boolean flag){
        gridPaneBoard.setDisable(flag);
    }



    public void removeCard(int row, int col) {
        CardUI card =  (CardUI) Utils.getNodeByRowColumnIndex(col-1,row-1,gridPaneBoard);
        if (card != null){
            card.setDisable(true);
            card.setStyleEmpty();
            gridPaneBoard.getChildren().remove(card);
        }
    }

    public void selectCard(int row,int col){
        CardUI cardUI = (CardUI)Utils.getNodeByRowColumnIndex(col-1,row-1,gridPaneBoard);
        cardUI.setStyleSelected();
    }

    public void updateCharCard(Card c) {
        if (c !=null){
            CardUI cardUI = (CardUI)Utils.getNodeByRowColumnIndex(c.getCol()-1,c.getRow()-1,gridPaneBoard);
            if (cardUI != null){
                cardUI.setText(Character.toString(c.getLetter()));
                if (c.getSelected()){
                    cardUI.setStyleSelected();
                } else{
                    cardUI.setStyleUndefined(c);
                }
            }
        }
    }

    public void clearAll() {
        gridPaneBoard.getChildren().clear();

    }
}
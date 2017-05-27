import com.sun.xml.internal.ws.api.pipe.Engine;
import engine.Board;
import engine.GameManager;
import engine.exception.EngineException;
import engine.exception.file.FileException;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    Board logicBoard;
    private int rows;
    private int columns;
    public void initialize(URL fxmlFileLocation, ResourceBundle resources){
        /*GameManager engine = new GameManager();
        try {
            engine.readXmlFile("c:\\d\\basic_1.xml");
            engine.startGame();
            List<Boolean> f = new ArrayList<>();
            f.add(false);
            f.add(false);
            engine.newGame(f);
            logicBoard = engine.getBoard();
            draw();
        }catch (EngineException ex)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        } catch (Exception ex){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }*/
    }

    private VBox createVboxCell() {
        VBox cell = new VBox();
        cell.setPrefSize(30.0D, 15.0D);
        cell.setAlignment(Pos.CENTER_RIGHT);
        cell.setMaxSize(60.0D, 40.0D);
        return cell;
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
    }

    private void createButtons(){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                try{
                    CardUI card = new CardUI(logicBoard.getBoardCard(i+1,j+1));
                    gridPaneBoard.add(card,i,j);
                }catch (EngineException ex)
                {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                }

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
        this.gridPaneBoard.setPadding(new Insets(0, 25.0D, 0, 25.0D));
        this.rows = logicBoard.getBoardSize();
        this.columns = logicBoard.getBoardSize();
        createRowsAndCols();
        createButtons();
        /*
        int indexRowButton = 0;
        int indexColumnButton = 0;
        int index = 2;
        int minWidth = 0;
        int minHeight = 0;
        HBox blockUp = new HBox(5.0D);
        VBox blockLeft = new VBox(5.0D);
        HBox table = new HBox(5.0D);

        for(int row = 0; row < this.rows; ++row) {
            HBox cellInRow = this.createHboxCell();

            int column;
            for(column = 0; column < columns; ++column) {
                Label label = new Label();
                try {
                    label.textProperty().bind(Bindings.format("%c",logicBoard.getBoardCard(row,column)));
                    cellInRow.getChildren().add(label);
                } catch (EngineException ex){
                    ex.getMessage();
                }
            }
            minWidth = logicBoard.getBoardSize() * (logicBoard.getBoardSize()  + 15);
            cellInRow.setMinWidth((double)minWidth);
            blockLeft.getChildren().add(cellInRow);
            table.getChildren().add(cellInRow);

            for(column = 0; column < this.columns; ++column) {
                if(row == 0) {
                    VBox cellInColumn = this.createVboxCell();


                    for(int blockInColumn = 0; blockInColumn < logicBoard.getBoardSize(); ++blockInColumn) {
                        Label label = new Label();
                        try {
                            label.textProperty().bind(Bindings.format("%c",logicBoard.getBoardCard(row,column)));
                            cellInRow.getChildren().add(label);
                        } catch (EngineException ex){
                            ex.getMessage();
                        }
                        cellInColumn.getChildren().add(label);
                    }

                    minHeight = logicBoard.getBoardSize() * (logicBoard.getBoardSize() + 15);
                    cellInColumn.setMinHeight((double)minHeight);
                    cellInColumn.setAlignment(Pos.BOTTOM_CENTER);
                    if(row == 0 && column == 0) {
                        blockUp.getChildren().add(this.createCell());
                    }

                    blockUp.getChildren().add(cellInColumn);
                }

                buttonInBoard = this.createTableCell(row, column);
                table.getChildren().add(buttonInBoard);
                this.boardButtonsMap.put(new Key(row, column), buttonInBoard);
            }

            this.gridPaneBoardAndBlocks.addRow(index++, new Node[]{table});
            table = new HBox(5.0D);
        }

        Insets insets = new Insets(0.0D, 0.0D, 0.0D, (double)(minWidth - 30));
        blockUp.setPadding(insets);
        this.gridPaneBoardAndBlocks.addRow(1, new Node[]{blockUp});
        this.gridPaneBoardAndBlocks.setAlignment(Pos.TOP_CENTER);
        blockLeft.setAlignment(Pos.CENTER_LEFT);
        return this.gridPaneBoardAndBlocks;
        */
    }

    private HBox createHboxCell() {
        HBox cell = new HBox();
        cell.setPrefSize(30.0D, 15.0D);
        cell.setAlignment(Pos.CENTER_RIGHT);
        cell.setMaxSize(60.0D, 40.0D);
        return cell;
    }

    public void setLogicBoard(Board logicBoard)
    {
        this.logicBoard = logicBoard;
        draw();
    }
}
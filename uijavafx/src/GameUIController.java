/**
 * Created by eran on 20/05/2017.
 */

import engine.GameManager;
import engine.exception.EngineException;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.xpath.XPathExpressionException;

public class GameUIController implements Initializable  {

    @FXML BorderPane boarderPane;
    @FXML Button buttonStart;
    @FXML Button buttonExit;
    @FXML Button buttonLoadXml;
    GameManager manger;
    BoardButtonController boardButtonController;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        buttonStart.setOnMouseClicked((Event e) -> {
                    List<Boolean> f = new ArrayList<>();
                    f.add(false);
                    f.add(false);
                    try {
                        manger.newGame(f);
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                });
        buttonStart.setDisable(true);

        buttonLoadXml.setOnMouseClicked((Event e)->{
            Stage newStage = new Stage();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            File file = fileChooser.showOpenDialog(newStage);
            if (file != null){
                manger = new GameManager();
                try{
                    manger.readXmlFile(file);
                    List<Boolean> f = new ArrayList<>();
                    f.add(false);
                    f.add(false);
                    manger.newGame(f);
                    buttonStart.setDisable(false);
                    boardButtonController.setLogicBoard(manger.getBoard());
                } catch (IOException ex){
                    Utils.showExceptionMessage(ex);
                } catch (EngineException ex)
                {
                    Utils.showExceptionMessage(ex);
                } catch (XPathExpressionException ex){
                    Utils.showExceptionMessage(ex);
                }
            }
        });
        url = getClass().getResource("BoardButton.fxml");
        fxmlLoader.setLocation(url);
        try{
            Node boardButton = fxmlLoader.load();
            boardButtonController = fxmlLoader.getController();
            boarderPane.setCenter(boardButton);
            boardButton.prefWidth(500);
            boardButton.prefHeight(500);
        } catch (java.io.IOException ex){

        }
    }

}

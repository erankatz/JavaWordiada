import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.Optional;

/**
 * Created by eran on 27/05/2017.
 */
public class Utils {
    private static Scene scene = null;

    public static void showExceptionMessage(Exception ex){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error Occured");
        alert.setHeaderText(null);
        alert.setContentText(ex.getMessage());
        alert.showAndWait();
    }

    public static Node getNodeByRowColumnIndex (final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();

        for (Node node : childrens) {
            if(gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }

        return result;
    }

    public static void printMessage(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void sleepForAWhile(long sleepTime) {
        if (sleepTime != 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ignored) {

            }
        }
    }

    public static boolean YesNoDialog(String message,String title){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES,ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.YES)
            return true;
        else
            return false;
    }

    public static void setStyleSheet(Scene scene,String cssFile){
        if (scene == null){
            scene = Utils.scene;
        }
        scene.getStylesheets().clear();
        URL url2 = Main.class.getResource(cssFile);
        if (url2 == null) {
            System.out.println("Resource not found. Aborting.");
            System.exit(-1);
        }
        String css = url2.toExternalForm();
        scene.getStylesheets().add(css);
        Utils.scene = scene;
    }
}

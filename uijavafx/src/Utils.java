import javafx.scene.control.Alert;

/**
 * Created by eran on 27/05/2017.
 */
public class Utils {
    public static void showExceptionMessage(Exception ex){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error Occured");
        alert.setHeaderText(null);
        alert.setContentText(ex.getMessage());
        alert.showAndWait();
    }
}

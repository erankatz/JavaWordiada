import com.sun.javafx.scene.control.skin.TableViewSkin;
import engine.Card;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.URL;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Optional;

/**
 * Created by eran on 27/05/2017.
 */
public class Utils {
    private static Scene scene = null;
    private static Method columnToFitMethod;
    private static MessageBoxInterface controller;

    static {
        try {
            columnToFitMethod = TableViewSkin.class.getDeclaredMethod("resizeColumnToFitContent", TableColumn.class, int.class);
            columnToFitMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void setMessageBoxConsumer(MessageBoxInterface i){
        Utils.controller = i;
    }

    public static void autoFitTable(TableView tableView) {
        tableView.getItems().addListener(new ListChangeListener<Object>() {
            @Override
            public void onChanged(Change<?> c) {
                for (Object column : tableView.getColumns()) {
                    try {
                        columnToFitMethod.invoke(tableView.getSkin(), column, -1);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void showExceptionMessage(String ex){
        controller.showExceptionMessage(ex);
    }

    public static Node getNodeByRowColumnIndex (final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();

        for (Node node : childrens) {
            if (node instanceof CardUI){
                CardUI card = (CardUI) node;
                if(card.row == row && card.col == column) {
                    result = node;
                    break;
                }
            }
        }
        return result;
    }

    public static void printMessage(String message){
        Platform.runLater(()->{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Message");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
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

    public static void makePostJsonRequest(String jsonString,String url) throws IOException
    {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        StringEntity params = new StringEntity(jsonString);
        request.addHeader("content-type", "application/json");
        request.setEntity(params);
        httpClient.execute(request);
    }

    static public String makeGetJsonRequest(String url)
    {
        if(url == null /*|| url.isEmpty() == true*/)
            new IllegalArgumentException("url is empty/null");
        StringBuilder sb = new StringBuilder();
        InputStream inStream = null;
        try
        {
            url = urlEncode(url);
            URL link = new URL(url);
            inStream = link.openStream();
            int i;
            int total = 0;
            byte[] buffer = new byte[8 * 1024];
            while((i=inStream.read(buffer)) != -1)
            {
                if(total >= (1024 * 1024))
                {
                    return "";
                }
                total += i;
                sb.append(new String(buffer,0,i));
            }
        }
        catch(Exception e )
        {
            e.printStackTrace();
            return null;
        }catch(OutOfMemoryError e)
        {
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }

    private static String urlEncode(String url)
    {
        if(url == null /*|| url.isEmpty() == true*/)
            return null;
        url = url.replace("[","");
        url = url.replace("]","");
        url = url.replaceAll(" ","%20");
        return url;
    }
}

package jsonObjectResponse.login;

import com.sun.javafx.util.Utils;
import jsonObjectResponse.games.EnumGamesAction;

/**
 * Created by eran on 26/07/2017.
 */
public class UserLogin {
    EnumLoginAction action;
    String username;
    boolean isComputer;
    public static String loginServletName = "login";
    public static String baseUrl = "http://localhost:8080/wordiada/";

    public UserLogin(String username, boolean isComputer){
        this.username = username;
        this.isComputer = isComputer;
        action = EnumLoginAction.login;
    }

    public String getLoginUrl(){
        return loginServletHelper.baseUrl + "?action=login" + "&userName=" + username + "&iscomputer=" + isComputer;
    }
}

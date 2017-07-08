package  users;

import users.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by user on 7/8/2017.
 */
public class LoginManager
{
    private static LoginManager myInstance;
    private HashMap<String, User> loggedInUsers;
    private LoginManager()
    {
        this.loggedInUsers = new HashMap<>();
    }

    public static LoginManager getInstance()
    {
        if (myInstance == null)
        {
            myInstance = new LoginManager();
        }
        return myInstance;
    }

    public boolean isNameValid(String userName)
    {
        return !loggedInUsers.containsKey(userName);
    }

    public void addUser(User newUser)
    {
        loggedInUsers.put(newUser.getName(), newUser);
    }

    public void removeUser(String newUserName)
    {
        loggedInUsers.remove(newUserName);
    }

    public List<User> getLoggedInUsers()
    {
        return loggedInUsers.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList());
    }

    public boolean canUserJoinGame(String userName)
    {
        User user = loggedInUsers.get(userName);
        int gameNumber = user.getInGameNumber();
        return gameNumber == -1;
    }

    public void userJoinGame(String userName, int gameId)
    {
        User user = loggedInUsers.get(userName);
        user.setInGameNumber(gameId);
    }

    public void userLeaveGame(String userName)
    {
        User user = loggedInUsers.get(userName);
        if (user != null)
        {
            user.setInGameNumber(-1);
        }
    }

    public User getUser(String userName)
    {
        return loggedInUsers.get(userName);
    }

    public boolean isUserInGame(String userName)
    {
        User user = loggedInUsers.get(userName);
        return user.getInGameNumber() != -1;
    }
}

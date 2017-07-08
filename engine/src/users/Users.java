package users;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eran on 07/08/17.
 */

public class Users
{
    private List<User> users;

    public Users(List<User> users)
    {
        this.users = users;
    }

    public List<User> getUsers()
    {
        return users;
    }

    public void setUsers(List<User> users)
    {
        this.users = users;
    }
}

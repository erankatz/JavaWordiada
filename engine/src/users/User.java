package users;

/**
 * Created by eran on 07/08/17.
 */
public class User
{
    private final int EMPTY = -1;

    private String name;
    private boolean isComputer;
    private int inGameNumber;

    public User(String name, boolean isComputer)
    {
        this.name = name;
        this.isComputer = isComputer;
        this.inGameNumber = EMPTY;
    }

    public String getName()
    {
        return name;
    }

    public boolean isComputer()
    {
        return isComputer;
    }

    public int getInGameNumber()
    {
        return inGameNumber;
    }

    public void setInGameNumber(int inGameNumber)
    {
        this.inGameNumber = inGameNumber;
    }
}


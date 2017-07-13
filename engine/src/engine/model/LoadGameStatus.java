package engine.model;

/**
 * Created by user on 12/10/2016.
 */
public class LoadGameStatus
{
    boolean isLoaded;
    String errorMessage;

    public LoadGameStatus(boolean isLoaded, String message)
    {
        this.isLoaded = isLoaded;
        this.errorMessage = message;
    }

    public boolean isLoaded()
    {
        return isLoaded;
    }

    public void setLoaded(boolean loaded)
    {
        isLoaded = loaded;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }
}

package engine;

/**
 * Created by eran on 07/06/2017.
 */
public class Utils
{
    public static final int sleepTime = 1000;
    public static void sleepForAWhile(long sleepTime) {
        if (sleepTime != 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ignored) {

            }
        }
    }
}

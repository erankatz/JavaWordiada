package engine;

/**
 * Created by eran on 14/06/2017.
 */
public class WordData {
    public long score;
    public int numberOfWords;
    public WordData(long score){
        numberOfWords =1;
        this.score =score;
    }

    protected void addWord(){
        numberOfWords++;
    }

    public long getScore(){
        return score*numberOfWords;
    }

    public int getNumberOfWords(){
        return numberOfWords;
    }
}

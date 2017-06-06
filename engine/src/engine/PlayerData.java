package engine;

/**
 * Created by eran on 06/06/2017.
 */
public class PlayerData {
    private String type;
    private int id;
    private String name;
    private long score;
    private int index;

    public PlayerData(String type, int id, String name, long score,int index) {
        this.type = type;
        this.id = index;
        this.name = name;
        this.score = score;
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public String getName(){
        return name;
    }
    public long getScore(){
        return score;
    }

    public void setScore(long score){
        this.score = score;
    }

    public int getIndex(){
        return index;
    }
}

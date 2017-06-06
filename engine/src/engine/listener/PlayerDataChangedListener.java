package engine.listener;

import engine.Player;
import engine.PlayerData;

/**
 * Created by eran on 06/06/2017.
 */
public interface PlayerDataChangedListener {
     void updateScore(PlayerData pl);
}

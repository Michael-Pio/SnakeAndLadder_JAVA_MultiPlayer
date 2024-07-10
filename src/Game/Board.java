package Game;

import java.util.HashMap;
import java.util.Map;

public class Board {
    public String file;
    public Map<Integer, Integer> snake;
    public Map<Integer, Integer> ladder;

    public Board(){
        this.snake=new HashMap<>();
        this.ladder=new HashMap<>();
    }
}

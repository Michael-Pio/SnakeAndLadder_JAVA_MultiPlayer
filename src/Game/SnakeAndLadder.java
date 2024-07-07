package Game;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class SnakeAndLadder {
    private static final int WIN_POINT = 100;
    private static final Map<Integer, Integer> snake = new HashMap<>();
    private static final Map<Integer, Integer> ladder = new HashMap<>();
    private final Random random = new Random();
    
    LoadMap mapManager = new LoadMap();

    //Loading the Map from disk
    String loadedRawMap = mapManager.ReadRawFromDisk("Map\\map1.txt");

    static {
        snake.put(99, 44);
        snake.put(94, 54);
        snake.put(51, 8);
        snake.put(36, 4);

        ladder.put(19, 59);
        ladder.put(14, 47);
        ladder.put(52, 92);
    }
    
    public int rollDice() {
        return random.nextInt(6) + 1;
    }

    public int movePlayer(int playerPosition, int diceValue) {
        playerPosition += diceValue;
        if (playerPosition > WIN_POINT) {
            playerPosition -= diceValue;
        }
        if (snake.containsKey(playerPosition)) {
            playerPosition = snake.get(playerPosition);

        }
        if (ladder.containsKey(playerPosition)) {
            playerPosition = ladder.get(playerPosition);

        }
        return playerPosition;
    }

    public boolean isWin(int playerPosition) {
        return playerPosition == WIN_POINT;
    }
    
    public int[] getBoardCoordinates(int position) {
        int x = (position - 1) / 10;
        int y = (position - 1) % 10;
        if (x % 2 == 1) y = 9 - y; // handle reverse direction for alternate rows
        x = 9 - x; // reverse the row order
        return new int[]{x, y};
    }

    public String visualizeBoard(Map<Integer,Integer> playerPositions){
        StringBuffer board = new StringBuffer(loadedRawMap);
        for (Map.Entry<Integer, Integer> entry : playerPositions.entrySet()) {


            int playerNumber = entry.getKey();
            int playerPosition = entry.getValue();
            int[] boardCoordinates = getBoardCoordinates(playerPosition);
            
            int lineIndex = boardCoordinates[0]; 
            int cellIndex = boardCoordinates[1];  

            int index = lineIndex * 146  + 84 + cellIndex * 6; //Dont fuck with this offset shit 

            // Update the cell with player number, assuming each cell has a fixed width
            String playerNumberStr = "| <P" + playerNumber + ">";
            board.replace(index, index + playerNumberStr.length(), playerNumberStr);
            

        }
        return  mapManager.AddColor(board.toString());
    }


    
}

package GameServer;
import java.io.*;
import java.net.*;
import java.util.*;

import BroadcastServers.UDPServer;
import Game.SnakeAndLadder;

public class SnakeAndLadderServer implements Runnable{
    public static boolean isSetupCompleted = false;
    private static final int PORT = 12345;
    private static int TOTAL_PLAYERS = 2; // Set the total number of players
    private static final List<Handler> handlers = Collections.synchronizedList(new ArrayList<>());
    private static final Map<Integer, Integer> playerPositions = new HashMap<>();
    private static final SnakeAndLadder game = new SnakeAndLadder();
    private static int currentPlayerId = 1;
    private static boolean gameOver = false;
    private static boolean gameStarted = false;


    private static final String ANSI_RESET = "\u001B[0m";
    // private static final String ANSI_BRIGHT_GREEN = "\u001B[92m";
    private static final String ANSI_BRIGHT_YELLOW = "\u001B[93m";
    // private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_BLUE = "\u001B[34m";

    // ANSI escape codes for background colors
    // private static final String ANSI_BG_DARK_GREEN = "\u001B[42m";
    // private static final String ANSI_BG_DARK_BLUE = "\u001B[44m";
    private static final String ANSI_BG_BLACK = "\u001B[40m";
    private static final String ANSI_BG_BRIGHT_GREEN = "\u001B[102m";

    public void run() {
        @SuppressWarnings("resource")
        Scanner scan = new Scanner(System.in);

        // System.out.println("Creating New Game Session !");
        System.out.println(ANSI_BG_BRIGHT_GREEN + ANSI_BLUE + "Creating New Game Session!" + ANSI_RESET);
        System.out.print(ANSI_BG_BLACK + ANSI_BRIGHT_YELLOW + "Enter Session Name: " + ANSI_RESET);
        String sessionName = scan.nextLine();
        System.out.print(ANSI_BG_BLACK + ANSI_BRIGHT_YELLOW + "Enter number of Players: " + ANSI_RESET);
        int TOTAL_PLAYERS = scan.nextInt();
        System.out.println(ANSI_BG_BRIGHT_GREEN + ANSI_BLUE + "Session '" + sessionName + "' created with " + TOTAL_PLAYERS + " players!" + ANSI_RESET);
       

        // System.out.print("Enter Session Name : ");
        // String sessionName = scan.nextLine();

        // System.out.print("Enter number of Players :");
        // TOTAL_PLAYERS = scan.nextInt();


        //This Helps to discover the server from client side
        Thread udpServerThreadThread = new Thread(new UDPServer(sessionName));
        udpServerThreadThread.start();
        System.out.println("[Server]Broadcast started...");

        isSetupCompleted = true;

        //This starts the real server
        startServer();
    }

    private static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[Server]Waiting for players...");
            System.out.println(InetAddress.getLocalHost().toString() + ":" + PORT);
            int playerId = 1;
            while (true) {
                Socket socket = serverSocket.accept();
                Handler handler = new Handler(socket, playerId);
                synchronized (handlers) {
                    handlers.add(handler);
                    playerPositions.put(playerId, 0);
                }
                new Thread(handler).start();
                playerId++;

                if (handlers.size() == TOTAL_PLAYERS && !gameStarted) {
                    startGame();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startGame() {
        synchronized (handlers) {
            for (Handler handler : handlers) {
                handler.out.println("[Server]All players have joined. The game is starting!");
            }
            gameStarted = true;
            handlers.notifyAll();
        }
    }

    private static class Handler implements Runnable {
        private final Socket socket;
        private final int playerId;
        private final BufferedReader in;
        private final PrintWriter out;

        public Handler(Socket socket, int playerId) throws IOException {
            this.socket = socket;
            this.playerId = playerId;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        }

        @Override
        public void run() {
            try {
                out.println("Welcome Player " + playerId);
                synchronized (handlers) {
                    while (!gameStarted) {
                        handlers.wait();
                    }
                }

                while (!gameOver) {
                    synchronized (handlers) {
                        while (playerId != currentPlayerId && !gameOver) {
                            handlers.wait();
                        }
                        if (gameOver) {
                            break;
                        }
                        out.println("\033[1;34m\n\n\nIt's your turn. Type 'roll' to roll the dice.\033[0m");
                        String command = in.readLine();
                        if (command != null && command.equals("roll")) {
                            int diceValue = game.rollDice();
                            int oldPosition = playerPositions.get(playerId);
                            int newPosition = game.movePlayer(playerPositions.get(playerId), diceValue);
                            playerPositions.put(playerId, newPosition);

                            broadcast("\033[1;32mPlayer " + playerId + " rolled a " + diceValue + " and moved to " + newPosition + "\033[0m");
                            broadcastBoard();
                            if(newPosition - oldPosition < 0){
                                broadcast("\033[1;31mPlayer " + playerId + " Got Bitten by a Snake!\033[0m");
                            } else if(newPosition - oldPosition > 6){
                                broadcast("\033[1;33mPlayer " + playerId + " Got Lucky by a Ladder!\033[0m");
                            }

                            if (game.isWin(newPosition)) {
                                out.println("\033[1;36mCongratulations you won the Game " + playerId + " won!\033[0m");
                                broadcast("\033[1;36m Player " + playerId + " won!\033[0m");
                                broadcast("Game Ended !");
                                gameOver = true;
                                handlers.notifyAll();
                            } else {
                                currentPlayerId = (currentPlayerId % handlers.size()) + 1;
                                handlers.notifyAll();
                            }
                        }
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        private void broadcast(String message) {
            synchronized (handlers) {
                for (Handler handler : handlers) {
                    handler.out.println(message);
                }
            }
        }

        private void broadcastBoard(){
            synchronized (handlers) {
                String boardVisualization = game.visualizeBoard(playerPositions);
                for (Handler handler : handlers) {
                    handler.out.println(boardVisualization);
                }
            }
        }
    }
}

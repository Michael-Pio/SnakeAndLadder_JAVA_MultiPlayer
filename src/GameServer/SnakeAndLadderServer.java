package GameServer;
import java.io.*;
import java.net.*;
import java.util.*;

import BroadcastServers.UDPServer;
import Game.SnakeAndLadder;

public class SnakeAndLadderServer implements Runnable{
    private static final int PORT = 12345;
    private static int TOTAL_PLAYERS = 2; // Set the total number of players
    private static final List<Handler> handlers = Collections.synchronizedList(new ArrayList<>());
    private static final Map<Integer, Integer> playerPositions = new HashMap<>();
    private static final SnakeAndLadder game = new SnakeAndLadder();
    private static int currentPlayerId = 1;
    private static boolean gameOver = false;
    private static boolean gameStarted = false;

    public void run() {
        
        //This Helps to discover the server from client side
        Thread udpServerThreadThread = new Thread(new UDPServer());
        udpServerThreadThread.start();
        System.out.println("Broadcast started...");

        @SuppressWarnings("resource")
        Scanner scan = new Scanner(System.in);

        System.out.println("Enter number of Players :");
        TOTAL_PLAYERS = scan.nextInt();


        //This starts the real server
        startServer();
    }

    private static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Waiting for players...");
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
                handler.out.println("All players have joined. The game is starting!");
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
                        out.println("It's your turn. Type 'roll' to roll the dice.");
                        String command = in.readLine();
                        if (command != null && command.equals("roll")) {
                            int diceValue = game.rollDice();
                            int oldPosition = playerPositions.get(playerId);
                            int newPosition = game.movePlayer(playerPositions.get(playerId), diceValue);
                            playerPositions.put(playerId, newPosition);
                            broadcast("Player " + playerId + " rolled a " + diceValue + " and moved to " + newPosition);
                            broadcastBoard();
                            if(newPosition - oldPosition < 0){
                                broadcast("Player " + playerId + " Got Bitten by a Snake!");
                            }else if(newPosition - oldPosition > 6){
                                broadcast("Player " + playerId + " Got Lucky by a Ladder!");
                            }

                            if (game.isWin(newPosition)) {
                                broadcast("Congratulations! Player " + playerId + " won!");
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

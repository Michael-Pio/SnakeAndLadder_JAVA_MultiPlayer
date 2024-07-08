import java.util.Scanner;

import GameServer.SnakeAndLadderClient;
import GameServer.SnakeAndLadderServer;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to Snakes and Ladders!");
        System.out.println("Enter your choice:\n\t1.Create Session\n\t2.Join Session\n\t3.Exit\n======================================");
        @SuppressWarnings("resource")
        Scanner scan = new Scanner(System.in);
        int choice = scan.nextInt();

        Thread snakeAndLadderServerThread , snakeAndLadderClientThread;

        switch (choice) {
            case 1:
                snakeAndLadderServerThread = new Thread(new SnakeAndLadderServer());
                snakeAndLadderServerThread.start();
                // snakeAndLadderServerThread.join();
                Thread.sleep(10000);
                
                snakeAndLadderClientThread = new Thread(new SnakeAndLadderClient(0));
                snakeAndLadderClientThread.start();
                snakeAndLadderClientThread.join();
                

                break;
            case 2:
                snakeAndLadderClientThread = new Thread(new SnakeAndLadderClient(1));
                snakeAndLadderClientThread.start();
                snakeAndLadderClientThread.join();

                Thread.sleep(1000);
                
                
                break;

            case 3:
                System.exit(0);
                break;
        
            default:
                break;
        }
        
    }
}

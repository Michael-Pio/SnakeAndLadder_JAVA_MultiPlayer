import java.util.Scanner;

import GameServer.SnakeAndLadderClient;
import GameServer.SnakeAndLadderServer;



public class App {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BRIGHT_GREEN = "\u001B[92m";
    public static final String ANSI_BRIGHT_YELLOW = "\u001B[93m";
    public static final String ANSI_RED = "\u001B[31m";

    // ANSI escape codes for background colors
    public static final String ANSI_BG_DARK_GREEN = "\u001B[42m";
    public static final String ANSI_BG_DARK_BLUE = "\u001B[44m";
    public static final String ANSI_BG_BLACK = "\u001B[40m";

    public static void main(String[] args) throws Exception {
        
        drawMenu();

        @SuppressWarnings("resource")
        Scanner scan = new Scanner(System.in);

        int choice = scan.nextInt();

        Thread snakeAndLadderServerThread , snakeAndLadderClientThread;

        switch (choice) {
            case 1:
                snakeAndLadderServerThread = new Thread(new SnakeAndLadderServer());
                snakeAndLadderServerThread.start();
                
                //waiting until the init of server completes
                while(!SnakeAndLadderServer.isSetupCompleted)
                    Thread.sleep(100);
            
                //instantiate listen server setup
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

    public static void drawMenu(){

        String border = ANSI_BG_DARK_GREEN + ANSI_BRIGHT_GREEN + "######################################" + ANSI_RESET;

        System.out.println(border);
        System.out.println(ANSI_BG_DARK_GREEN + ANSI_BRIGHT_GREEN + "#" + "   " + ANSI_BG_DARK_GREEN + ANSI_BRIGHT_GREEN + "Welcome to Snakes and Ladders!   " + ANSI_BG_DARK_GREEN + ANSI_BRIGHT_GREEN + "#" + ANSI_RESET);
        System.out.println(border);
        System.out.println(ANSI_BG_DARK_GREEN + ANSI_BRIGHT_GREEN + "#" + ANSI_RESET + "  " + ANSI_RED + "Enter your choice:" + ANSI_RESET + "                " + ANSI_BG_DARK_GREEN + ANSI_BRIGHT_GREEN + "#" + ANSI_RESET);
        System.out.println(ANSI_BG_DARK_GREEN + ANSI_BRIGHT_GREEN + "#" + ANSI_RESET + "  " + ANSI_BRIGHT_YELLOW + "\t1. Create Session" + ANSI_RESET + "            " + ANSI_BG_DARK_GREEN + ANSI_BRIGHT_GREEN + "#" + ANSI_RESET);
        System.out.println(ANSI_BG_DARK_GREEN + ANSI_BRIGHT_GREEN + "#" + ANSI_RESET + "  " + ANSI_BRIGHT_YELLOW + "\t2. Join Session" + ANSI_RESET + "              " + ANSI_BG_DARK_GREEN + ANSI_BRIGHT_GREEN + "#" + ANSI_RESET);
        System.out.println(ANSI_BG_DARK_GREEN + ANSI_BRIGHT_GREEN + "#" + ANSI_RESET + "  " + ANSI_BRIGHT_YELLOW + "\t3. Exit" + ANSI_RESET + "                      " + ANSI_BG_DARK_GREEN + ANSI_BRIGHT_GREEN + "#" + ANSI_RESET);
        System.out.println(border);
    }
}
//4693
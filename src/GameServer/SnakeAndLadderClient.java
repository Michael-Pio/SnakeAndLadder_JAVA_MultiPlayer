package GameServer;
import java.io.*;
import java.net.*;
import java.util.Scanner;

import BroadcastServers.UDPClient;

public class SnakeAndLadderClient implements Runnable{
    private static  String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    int mode;
    int choice = 1;


    public SnakeAndLadderClient(int mode){
        //1 means client only
        //0 means listenServer mode
        this.mode = mode;
    }

    public void run() {


        UDPClient client = new UDPClient();
        Thread clientThread = new Thread(client);
        clientThread.start();

        Scanner scan = new Scanner(System.in);

        try {
            clientThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Choose any one session to play:");

        int i =1;
        for (String serverIp : client.discoveredServers) {
            System.out.println(i++ + " "+client.discoveredSessions.get(i-2)+" " + serverIp );
        }
        if(mode == 1){
            choice = scan.nextInt();

            choice =1;
        }

        if (choice > 0 && choice <= client.discoveredServers.size()) {
            SERVER_ADDRESS = client.discoveredServers.get(choice - 1);
        } else {
            System.out.println("Invalid choice. Exiting...");
            return;
        }
        
        // Connect to the server
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);

             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

                String welcomeMessage = in.readLine();
                if (welcomeMessage != null) {
                    System.out.println(welcomeMessage); // Welcome message
                }

                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println(serverMessage);
                    if (serverMessage.contains("won")) {
                        break;
                    }
                    if (serverMessage.contains("It's your turn.")) {
                        System.out.print("Type 'roll' to roll the dice: ");
                        String input = console.readLine();
                        while (!"roll".equalsIgnoreCase(input)) {
                            System.out.print("Invalid input. Please type 'roll' to roll the dice: ");
                            input = console.readLine();
                        }
                        out.println(input);
                    }
                }
            
            } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}

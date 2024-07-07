import java.io.*;
import java.net.*;

public class SnakeAndLadderClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

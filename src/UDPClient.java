import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class UDPClient {
    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);

            String discoverMessage = "DISCOVER_SERVER";
            byte[] sendData = discoverMessage.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 9876);
            socket.send(sendPacket);

            List<String> discoveredServers = new ArrayList<>();
            byte[] receiveData = new byte[1024];

            socket.setSoTimeout(2000);  // Set a timeout of 2 seconds

            while (true) {
                try {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket);

                    String responseMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    if (responseMessage.startsWith("SERVER_RESPONSE: ")) {
                        String serverIP = responseMessage.split(" ")[1];
                        if (!discoveredServers.contains(serverIP)) {
                            discoveredServers.add(serverIP);
                        }
                    }
                } catch (Exception e) {
                    // Timeout reached, stop listening
                    break;
                }
            }

            System.out.println("Discovered servers: " + discoveredServers);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


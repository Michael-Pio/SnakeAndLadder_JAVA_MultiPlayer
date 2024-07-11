package BroadcastServers;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class UDPClient implements Runnable{
    public List<String>discoveredServers = new ArrayList<>();
    public List<String>discoveredSessions = new ArrayList<>();
    
    @Override
    public void run() {
        try {
            @SuppressWarnings("resource")
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);

            String discoverMessage = "DISCOVER_SERVER";
            byte[] sendData = discoverMessage.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 9876);
            socket.send(sendPacket);

            discoveredServers = new ArrayList<>();
            discoveredSessions = new ArrayList<>();
            byte[] receiveData = new byte[1024];

            socket.setSoTimeout(2000);  // Set a timeout of 2 seconds

            while (true) {
                try {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket);
                    System.out.println();


                    String responseMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    if (responseMessage.startsWith("SERVER_RESPONSE: ")) {
                        String serverIP = receivePacket.getAddress().getHostName(); //responseMessage.split(" ")[1]
                        String sessionName = responseMessage.split(" ")[3];
                        if (!discoveredServers.contains(serverIP)) {
                            discoveredServers.add(serverIP);
                            discoveredSessions.add(sessionName);
                        }
                    }
                } catch (Exception e) {
                    // Timeout reached, stop listening
                    break;
                }
            }

            System.out.println("Discovered servers: " + discoveredServers);
            System.out.println("Discovered Game Session : " + discoveredSessions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread clienThread = new Thread(new UDPClient());
        clienThread.start();
        clienThread.join();
    }
}




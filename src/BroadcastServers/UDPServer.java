package BroadcastServers;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer implements Runnable {
    public String SessionName;



    public UDPServer(String SessionName){
        this.SessionName = SessionName;
    }

    @Override
    public void run() {
        try {
            @SuppressWarnings("resource")
            DatagramSocket socket = new DatagramSocket(9876, InetAddress.getByName("0.0.0.0"));
            byte[] receiveData = new byte[1024];
            
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                if (message.equals("DISCOVER_SERVER")) {
                    InetAddress clientAddress = receivePacket.getAddress();
                    int clientPort = receivePacket.getPort();
                    String responseMessage = "SERVER_RESPONSE: " + InetAddress.getLocalHost().getHostAddress() +" SESSION_NAME: "+ SessionName;
                    

                    byte[] sendData = responseMessage.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                    socket.send(sendPacket);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread clienThread = new Thread(new UDPServer("Demo_Game_Session"));
        clienThread.start();
        
        clienThread.join();
    }
}


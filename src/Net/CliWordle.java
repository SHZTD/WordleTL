
package Net;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class CliWordle {
    private InetAddress serverIP;
    private int serverPort;
    private DatagramSocket socket;
    private Scanner sc;
    private String nombre;

    public CliWordle() {
        sc = new Scanner(System.in);
    }

    public void init(String host, int port) throws SocketException, UnknownHostException {
        serverIP = InetAddress.getByName(host);
        serverPort = port;
        socket = new DatagramSocket();
    }

    public void runClient() throws IOException {
        byte[] receivedData = new byte[1024];
        byte[] sendingData = getFirstRequest();

        while (true) {
            DatagramPacket packet = new DatagramPacket(sendingData, sendingData.length, serverIP, serverPort);
            socket.send(packet);
            packet = new DatagramPacket(receivedData, receivedData.length);
            socket.receive(packet);
            System.out.println("Respuesta del servidor: " + new String(packet.getData(), 0, packet.getLength()));
            sendingData = getDataToRequest();
        }
    }

    private byte[] getFirstRequest() {
        System.out.println("Introduce tu nombre: ");
        nombre = sc.nextLine();
        return nombre.getBytes();
    }

    private byte[] getDataToRequest() {
        System.out.print(nombre + "> ");
        return sc.nextLine().getBytes();
    }

    public static void main(String[] args) {
        try {
            CliWordle client = new CliWordle();
            client.init("localhost", 7484);
            client.runClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

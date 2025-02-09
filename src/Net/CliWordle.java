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

    private int intentos = 7;

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
        byte[] sendingData;

        sendingData = getFirstRequest();
        while (true) {
            DatagramPacket packet = new DatagramPacket(sendingData, sendingData.length, serverIP, serverPort);
            socket.send(packet);

            packet = new DatagramPacket(receivedData, receivedData.length);
            socket.receive(packet);
            String response = new String(packet.getData(), 0, packet.getLength());

            System.out.println(response);

            if (response.contains("¡Has ganado!") || response.contains("Se acabaron los intentos")) {
                break;
            }

            if (response.contains("No es tu turno es turno de: "+ nombre)) {
                continue;
            }

            sendingData = getNextWordAttempt();
        }
    }

    private byte[] getFirstRequest() {
        System.out.print("Ingresa tu nombre: ");
        nombre = sc.nextLine();
        return nombre.getBytes();
    }

    private byte[] getNextWordAttempt() {
        System.out.print("Introduce tu intento: ");
        String msg = sc.nextLine();
        System.out.println("Intentos restantes ahora = " + intentos--);
        return msg.getBytes();
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


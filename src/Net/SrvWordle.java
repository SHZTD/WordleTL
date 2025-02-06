package Net;

import Wordle.Wordle;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

public class SrvWordle {
    private Wordle wordle = new Wordle();
    private DatagramSocket socket;
    private InetAddress clientIP;
    private int clientPort;
    private String palabra;
    private char[] palabraArray;
    private int intentosRestantes = 7;
    private Turnos turnManager;
    private Map<String, Integer> intentosPorJugador = new HashMap<>();

    public void init(int port) throws SocketException {
        socket = new DatagramSocket(port);
        turnManager = new Turnos();
        palabra = wordle.obtenerPalabra();
        palabraArray = palabra.toCharArray();
    }

    public void runServer() throws IOException {
        byte[] receivingData = new byte[1024];

        while (true) {
            DatagramPacket packet = new DatagramPacket(receivingData, receivingData.length);
            socket.receive(packet);
            clientIP = packet.getAddress();
            clientPort = packet.getPort();

            String request = new String(packet.getData(), 0, packet.getLength()).trim();
            String clientId = clientIP.toString() + ":" + clientPort;

            if (!intentosPorJugador.containsKey(clientId)) {
                intentosPorJugador.put(clientId, 0);
                turnManager.addPlayer(clientId);
            }

            if (!turnManager.isPlayerTurn(clientId)) {
                sendMessage("No es tu turno. Espera.", clientIP, clientPort);
                continue;
            }

            String response = processWordleLogic(request, clientId);
            sendMessage(response, clientIP, clientPort);

            turnManager.nextTurn();
        }
    }

    private String processWordleLogic(String input, String clientId) {
        if (input.length() != palabraArray.length) {
            return "La palabra debe tener " + palabraArray.length + " letras.";
        }

        char[] inputArray = input.toCharArray();
        StringBuilder result = new StringBuilder();
        boolean palabraEncontrada = true;

        for (int i = 0; i < palabraArray.length; i++) {
            if (inputArray[i] == palabraArray[i]) {
                result.append("[").append(inputArray[i]).append("] ");
            } else if (palabra.contains(String.valueOf(inputArray[i]))) {
                result.append("(").append(inputArray[i]).append(") ");
                palabraEncontrada = false;
            } else {
                result.append("_ ");
                palabraEncontrada = false;
            }
        }

        intentosPorJugador.put(clientId, intentosPorJugador.get(clientId) + 1);

        if (palabraEncontrada) {
            return "¡Has ganado! La palabra era: " + palabra;
        }

        if (intentosPorJugador.get(clientId) >= intentosRestantes) {
            return "Se acabaron los intentos. La palabra era: " + palabra;
        }

        return result.toString();
    }

    private void sendMessage(String message, InetAddress ip, int port) throws IOException {
        byte[] sendingData = message.getBytes();
        DatagramPacket packet = new DatagramPacket(sendingData, sendingData.length, ip, port);
        socket.send(packet);
    }

    public static void main(String[] args) {
        try {
            SrvWordle server = new SrvWordle();
            server.init(7484);
            server.runServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
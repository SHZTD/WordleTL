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
    private Map<String, String> nombresPorJugador = new HashMap<>();

    public void init(int port) throws SocketException {
        socket = new DatagramSocket(port);
        turnManager = new Turnos();
        palabra = wordle.obtenerPalabra().trim();
        palabraArray = palabra.toCharArray();
        System.out.println("Palabra a adivinar: " + palabra);  // Debugging
    }

    public void runServer() throws IOException {
        byte[] receivingData = new byte[1024];

        while (true) {
            DatagramPacket packet = new DatagramPacket(receivingData, receivingData.length);
            socket.receive(packet);
            clientIP = packet.getAddress();
            clientPort = packet.getPort();

            String request = new String(packet.getData(), 0, packet.getLength()).trim();
            String clientId = clientIP.getHostAddress() + ":" + clientPort;

            // Si el cliente es nuevo, registrar nombre y agregar a turnos
            if (!nombresPorJugador.containsKey(clientId)) {
                nombresPorJugador.put(clientId, request);
                intentosPorJugador.put(clientId, 0);
                turnManager.addPlayer(clientId);
                sendMessage("Bienvenido, " + request + "! Espera tu turno.", clientIP, clientPort);
                continue;
            }

            String nombreJugador = nombresPorJugador.get(clientId);

            if (!turnManager.isPlayerTurn(clientId)) {
                String jugadorEnTurno = nombresPorJugador.get(turnManager.getCurrentPlayerId());
                sendMessage("No es tu turno. Ahora es el turno de " + jugadorEnTurno + ".", clientIP, clientPort);
                continue;
            }

            String response = processWordleLogic(request, clientId, nombreJugador);
            sendMessage(response, clientIP, clientPort);

            // Solo cambiar de turno si no ha ganado
            if (!response.contains("¡Felicidades")) {
                turnManager.nextTurn();
                String siguienteJugadorId = turnManager.getCurrentPlayerId();
                String siguienteJugadorNombre = nombresPorJugador.get(siguienteJugadorId);
                InetAddress siguienteIP = InetAddress.getByName(siguienteJugadorId.split(":")[0]);
                int siguientePort = Integer.parseInt(siguienteJugadorId.split(":")[1]);
                sendMessage("Es tu turno, " + siguienteJugadorNombre + ". Introduce tu intento.", siguienteIP, siguientePort);
            }
        }
    }

    private String processWordleLogic(String input, String clientId, String nombreJugador) {
        input = input.trim(); // Asegurarse de eliminar espacios adicionales

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
            return "¡Felicidades " + nombreJugador + "! Has ganado. La palabra era: " + palabra;
        }

        if (intentosPorJugador.get(clientId) >= intentosRestantes) {
            return "Se acabaron los intentos, " + nombreJugador + ". La palabra era: " + palabra;
        }

        return nombreJugador + ", tu resultado: " + result.toString();
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
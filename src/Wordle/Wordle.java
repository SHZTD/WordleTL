package Wordle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Wordle {
    private List<String> cargarPalabras(String nombreArchivo) {
        List<String> palabras = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(nombreArchivo))) {
            while (scanner.hasNextLine()) {
                palabras.add(scanner.nextLine().trim());
            }
        } catch (FileNotFoundException e) {
            System.err.println("Archivo no encontrado: " + nombreArchivo);
            throw new RuntimeException("No se pudo cargar el archivo de palabras.", e);
        }
        return palabras;
    }

    private String obtenerPalabra(List<String> palabras) {
        Random rnd = new Random();
        int p = rnd.nextInt(palabras.size());
        System.out.println(p);
        return palabras.get(p);
    }

    public void logicaWordle() {
        String path = Wordle.class.getResource("palabras").getFile();
        List<String> palabras = cargarPalabras(path);

        // selecciona una palabra randoms
        String palabra = obtenerPalabra(palabras);
        char[] palabraArray = palabra.toCharArray();
        int longitudPalabra = palabraArray.length;

        System.out.println("La palabra tiene " + longitudPalabra + " letras.");
        System.out.println("_ ".repeat(longitudPalabra));

        // input & logica wordle
        Scanner input = new Scanner(System.in);
        final int INTENTOS = 7;
        for (int intento = 1; intento <= INTENTOS; intento++) {
            System.out.print("Intento " + intento + "/" + INTENTOS + ":\n");
            String entrada = input.nextLine().trim();

            if (entrada.length() != longitudPalabra) {
                System.out.println("La palabra debe tener exactamente " + longitudPalabra + " letras.");
                intento--; // no interesa que se sume el intento
                continue;
            }

            char[] inputArray = entrada.toCharArray();
            boolean palabraEncontrada = true;

            // comparar las letras
            for (int i = 0; i < longitudPalabra; i++) {
                if (inputArray[i] == palabraArray[i]) {
                    System.out.print("[" + inputArray[i] + "] "); // letra correcta y en posición correcta
                } else if (palabra.contains(String.valueOf(inputArray[i]))) {
                    System.out.print("(" + inputArray[i] + ") "); // letra correcta pero en posición incorrecta
                    palabraEncontrada = false;
                } else {
                    System.out.print("_ "); // letra incorrecta
                    palabraEncontrada = false;
                }
            }
            System.out.println();

            if (palabraEncontrada) {
                System.out.println("Has encontrado la palabra.");
                return;
            }
        }
        System.out.println("Se acabaron los intentos. La palabra era: " + palabra);
    }
}

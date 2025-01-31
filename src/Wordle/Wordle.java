package Wordle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Wordle {

    public List<String> cargarPalabras(String nombreArchivo) {
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

    public String obtenerPalabra() {
        String path = Wordle.class.getResource("palabras").getFile();
        List<String> palabras = cargarPalabras(path);
        Random rnd = new Random();
        int p = rnd.nextInt(palabras.size());
        System.out.println(p + 1);
        return palabras.get(p);
    }
}

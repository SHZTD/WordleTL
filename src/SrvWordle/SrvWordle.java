package SrvWordle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class SrvWordle {
    /*
        TODO:
        Implementar la logica entera del servidor UDP
     */

    public static String obtenerPalabra() {
        // setup de linea random
        Random rnd = new Random();
        int index =  rnd.nextInt(0, 100);

        // obten el path para el file
        String path = SrvWordle.class.getResource("palabras").getFile(); // explota si no hay archivo asi que
        File file = new File(path);

        // instancia el scanner
        Scanner srcfile = null;
        try {
            srcfile = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // almacena el archivo (mal optimizado, clase PATH)
        ArrayList<String> palabraRandom = new ArrayList<>();
        int linea = 0;
        while (srcfile.hasNextLine()) {
            palabraRandom.add(srcfile.nextLine());
            linea++;
            if (linea == index) {
                break; // deja de leer el archivo entero
            }
        }

        // retorna la palabra
        return palabraRandom.get(index - 1);
    }
}

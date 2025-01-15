package SrvWordle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class SrvWordle {
    private String obtenerPalabra() {
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

        System.out.println(index);
        // retorna la palabra
        return palabraRandom.get(index < 0 ? index : index - 1);
    }

    public void logicaWordle() {
        /*
            Los souts estan por debug
            TODO:
                Se verifica la posicion de la letra (los verdes en wordle)
                Se tiene que verificar si la letra "existe" en el contexto de la palabra (los naranjas)
         */

        char[] palabraArray = obtenerPalabra().toCharArray(); // almacena la palabra en un array de chars
        int lpArray = palabraArray.length;
        System.out.println("La palabra tiene " + lpArray + " letras.");
        for (int i = 0; i < lpArray; i++) {
            System.out.print("_ ");
        }
        // nueva linea
        System.out.print("\n");

        // instancia un input
        Scanner input = new Scanner(System.in);
        final int INTENTOS = 7;
        int intentoActual = 0;
        while (intentoActual != INTENTOS) {
            char[] inputString = input.nextLine().toCharArray();
            int lpInputString = inputString.length;
            if (lpInputString > lpArray) {
                System.out.println("La palabra no coincide con el tamaño!");
            } else {
                // al ser del mismo lenght, se pueden recorrer ambas
                intentoActual++;
                int c = 0;

                // comprueba todas las posiciones si estan OK o no
                for (int i = 0; i < lpInputString; i++) {
                    if (inputString[i] == palabraArray[i]) {
                        System.out.println("La letra en la posicion " + i + " son correctas.");
                        c++;
                    } else {
                        System.out.println("Nope, la posicion " + i + " estan mal.");
                    }
                }

                // mira si se cumple la condicion
                if (c == palabraArray.length) {
                    System.out.println("Palabra encontrada! Fin del juego.");
                    return;
                } else {
                    System.out.println("Tienes que volver a probar la palabra.");
                }
            }
        }
        System.out.println("Intentos superados, fin del juego");
    }
}

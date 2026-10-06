import java.util.Scanner;

public class Ejercicio1 {
    /*
    Crea una clase Contador con un método incrementar() que haga
    contador++. Lanza 10 hilos que llamen 10.000 veces cada uno
    a incrementar(). Comprueba que el resultado final no es
    100.000. Explica por qué.
     */
    public static int contador = 0;

    public static void incrementar() {
        contador++;
    }

    public static int getContador() {
        return contador;
    }
}


    void main() throws InterruptedException {
    Ejercicio1 ejercicio1 = new Ejercicio1();
        Runnable tarea = () -> {
            for (int i = 0; i < 10000; i++) {
                ejercicio1.incrementar();
            };
        };
        Thread [] hilos = new Thread[10];
        for (int i = 0; i < hilos.length; i++) {
            hilos[i] = new Thread(tarea);
        }
        for (Thread t : hilos){
            t.start();
        }
        for (Thread t : hilos){
            t.join();
        }

        System.out.println(ejercicio1.getContador());
    }


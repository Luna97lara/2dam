import java.util.Scanner;

public class Ejercicio3 {
    /*
    Crea un hilo que ejecute un bucle infinito comprobando
    Thread.currentThread().isInterrupted(). Desde el main, espera 2
    segundos y llama a interrupt(). El hilo debe terminar de forma
    "educada" mostrando un mensaje antes de morir.
     */
    void main() throws InterruptedException {
        Thread infinito = new Thread (() -> {
            while(!Thread.currentThread().isInterrupted()) {
                System.out.println("Qué disen los hippies");
            }
            System.out.println("Interrumpido, cerrando recursos...");
        });
        infinito.start();
        Thread.sleep(5000);
        infinito.interrupt();

    }
}

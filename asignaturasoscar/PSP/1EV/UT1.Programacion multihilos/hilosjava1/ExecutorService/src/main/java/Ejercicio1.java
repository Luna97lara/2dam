import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Ejercicio1 {
    /*
    Crea un ExecutorService con un pool de 4 hilos y envíale
    20 tareas Runnable que simulan procesar un pedido
    (imprimir mensaje + dormir un tiempo aleatorio).
    Cierra el pool correctamente al finalizar.
     */
    public static final ExecutorService executor = Executors.newFixedThreadPool(4);
    public static final Random random = new Random();

    void main() throws InterruptedException {
        for(int i = 0; i < 21; i++){
            int tarea = i;
            executor.submit(() -> {
                IO.println("Procesando pedido "+tarea);
                try {
                    Thread.sleep(300+random.nextInt(100));
                } catch (InterruptedException e) {}
            });
        }
        executor.shutdown();
    }

}
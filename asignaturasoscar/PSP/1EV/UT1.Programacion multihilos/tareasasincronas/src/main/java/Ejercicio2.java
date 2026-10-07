import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Ejercicio2 {
    /*
    Crea una tarea que tarde 5 segundos.
    Mientras no haya terminado, el programa deberá mostrar:
        Esperando...
    Utiliza:
        future.isDone()
    No utilices get() inmediatamente.
    Ampliación
    Muestra también el nombre del hilo que ejecuta la tarea.
     */

    ExecutorService executor = Executors.newSingleThreadExecutor();

    void main() throws InterruptedException, ExecutionException {
        Future<Integer> future = executor.submit(() -> {
            Thread.sleep(5000);
            return 1;
        });
        while(!future.isDone()){
            IO.println("Esperando...");
            Thread.sleep(500);
        }
        IO.println("Resultado: "+future.get());
        executor.shutdown();
    }

}

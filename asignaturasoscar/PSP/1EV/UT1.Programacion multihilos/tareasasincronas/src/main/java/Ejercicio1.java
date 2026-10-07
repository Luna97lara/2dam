import java.util.concurrent.*;

public class Ejercicio1 {
    /*
    Crea un programa que:
        1. Cree un ExecutorService con dos
        hilos.
        2. Envíe una tarea mediante submit().
        3. La tarea debe tardar 2 segundos.
        4. Debe devolver el número 42.
        5. El programa principal debe mostrar:
        6. "Tarea enviada"
        7. "Esperando resultado..."
        8. el resultado.
     */

    ExecutorService executor = Executors.newFixedThreadPool(2);


    void main() throws ExecutionException, InterruptedException {
        Callable<Integer> callable = (() -> {
            try {
                Thread.sleep(2000);
                return 42;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Future<Integer> future = executor.submit(callable);
        IO.println("Tarea enviada");
        IO.println("Esperando resultado...");
        Integer resultado = future.get();
        IO.println("Resultado: " + resultado);
        executor.shutdown();
    }

}

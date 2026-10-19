import java.util.concurrent.*;

public class Ejercicio2 {
    /*
## Ejercicio 2. Entender un programa asíncrono defectuoso (1 puntos)

Una aplicación consulta dos servicios independientes: temperatura y humedad.
Se desea que ambas consultas comiencen sin esperar a que termine la otra y que,
cuando las dos hayan acabado, se muestre el resultado.

 */
    static void main() {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<String> temperatura =() -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return "25 ºC";
                };


        Callable<String> humedad =
                () -> {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return "60 %";
                };

        Future<String> temperaturaFuture = executor.submit(temperatura);
        Future<String> humedadFuture = executor.submit(humedad);

        try {
            IO.println(humedadFuture.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        try {
            IO.println(temperaturaFuture.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        executor.shutdown();
    }
    /*
    Para hacer este ejercicio prácticamente lo he rediseñado:
        1. He sustituído los CompletableFuture por Callable
        2. He añadido un Future para cada tarea, y abajo he printeado sus get()
        de manera que el primer get fuese el de la tarea más larga (en este caso,
        humedad), y el segundo el de la otra; por ello, he eliminado también los
        sout existentes.
     */
}

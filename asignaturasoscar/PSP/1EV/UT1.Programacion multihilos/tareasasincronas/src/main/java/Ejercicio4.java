import java.util.concurrent.*;

public class Ejercicio4 {
    /*
    Modifica la práctica anterior para obtener los resultados haciendo:
        f1.get();
        f2.get();
        f3.get();
    Después cambia el orden:
        f2.get();
        f1.get();
        f3.get();
    Comprueba lo que ocurre
     */
    ExecutorService pool = Executors.newFixedThreadPool(3);

    void main() throws ExecutionException, InterruptedException {
        long initialTime = System.currentTimeMillis();
        IO.println("Iniciando...");
        Callable<Integer> task1 = () -> {
            Thread.sleep(2000);
            return 10;
        };

        Callable<Integer> task2 = () -> {
            Thread.sleep(1000);
            return 20;
        };

        Callable<Integer> task3 = () -> {
            Thread.sleep(3000);
            return 30;
        };

        Future<Integer> future1 = pool.submit(task1);
        Future<Integer> future2 = pool.submit(task2);
        Future<Integer> future3 = pool.submit(task3);

        IO.println(future3.get());
        IO.println(future1.get());
        IO.println(future2.get());

        int resultado = future1.get() + future2.get() + future3.get();
        long finaltime = System.currentTimeMillis();
        long totaltime = finaltime - initialTime;
        double segundos = totaltime / 1000.0;

        IO.println("Resultado: " + resultado);
        IO.println("Tiempo tardado " + segundos + " s");
        pool.shutdown();

    }
}

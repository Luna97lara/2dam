import java.util.concurrent.*;

public class Ejercicio3 {
    /*
    Crea tres tareas:
        Tarea 1 → tarda 2 segundos → devuelve 10
        Tarea 2 → tarda 1 segundo  → devuelve 20
        Tarea 3 → tarda 3 segundos → devuelve 30
    Utiliza un pool de tres hilos.
    Al terminar, muestra:
        Resultado total: 60
    Mide el tiempo total
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

        int resultado = future1.get()+future2.get()+future3.get();
        long finaltime = System.currentTimeMillis();
        long totaltime= finaltime-initialTime;
        double segundos=totaltime/1000.0;

        IO.println("Resultado: " + resultado);
        IO.println("Tiempo tardado " + segundos+" s");
        pool.shutdown();
        /*
        Se ejecuta en 3 segundos y no en 6 porque al usar un pool
        las tareas se hacen simultaneamente, no una detrás de otra
         */
    }
}


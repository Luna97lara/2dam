import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Ejercicio2 {
    /*
    Crea 5 tareas Callable<Integer> que calculen el factorial
    de un número distinto cada una. Envíalas a un ExecutorService,
    recoge los Future<Integer> y muestra los resultados a medida que
    estén disponibles.
     */
    public static final ExecutorService executor = Executors.newFixedThreadPool(5);

    public static final List<Future<Integer>> future = new ArrayList<>();
    void main () throws ExecutionException, InterruptedException {
        Callable<Integer> factorial5 = () -> {
            return 5*4*3*2*1;
        };

        Future<Integer> result = executor.submit(factorial5);
        future.add(result);

        Callable<Integer> factorial4 = () -> {
            return 4*3*2*1;
        };

        Future<Integer> result2 = executor.submit(factorial4);
        future.add(result2);

        Callable<Integer> factorial3 = () -> {
            return 3*2*1;
        };

        Future<Integer> result3 = executor.submit(factorial3);
        future.add(result3);

        Callable<Integer> factorial2 = () -> {
            return 2*1;
        };

        Future<Integer> result4 = executor.submit(factorial2);
        future.add(result4);

        Callable<Integer> factorial1 = () -> {
            return 1*1;
        };

        Future<Integer> result1 = executor.submit(factorial1);
        future.add(result1);

        for (int i = 0; i < future.size(); i++) {
            IO.println(future.get(i).get());
        }



    }
}

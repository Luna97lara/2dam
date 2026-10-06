
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Ejercicio1 {

    /*
    Reescribe el ejercicio 4.2 (productor-consumidor con N
    productores/consumidores) usando ArrayBlockingQueue en lugar de
    wait()/notify() manuales. Compara la cantidad de código necesario
    frente a la solución manual.
     */

    public static final BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);

    void main(){
        Runnable productor = () -> {
            for (int i = 0; i < 10; i++) {
                try {
                    queue.put(i);
                    IO.println(Thread.currentThread().getName() + "produce " + i);
                } catch (InterruptedException e) {}
            }
        };

        Runnable consumidor = () -> {
            for (int i = 0; i < 5; i++) {
                try{
                    int value = queue.take();
                    IO.println(Thread.currentThread().getName() + "consume " + value);
                } catch (InterruptedException e) {}
            }
        };

        new Thread(productor, "Productor 1 ").start();
        new Thread(productor, "Productor 2 ").start();
        new Thread(consumidor, "Consumidor 1 ").start();
        new Thread(consumidor, "Consumidor 2 ").start();

    }

}

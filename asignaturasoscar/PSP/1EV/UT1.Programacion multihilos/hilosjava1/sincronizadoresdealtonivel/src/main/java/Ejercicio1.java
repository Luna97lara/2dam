import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Ejercicio1 {
    /*
    Simula una carrera de 5 corredores (hilos). Cada corredor debe esperar en la
    línea de salida hasta que todos estén listos (CyclicBarrier), y solo entonces
    empezar a "correr" (bucle con sleep aleatorio) hasta cruzar la meta.
     */

    public static CyclicBarrier barrier = new CyclicBarrier(5);
    public static Random random = new Random();

    void main(){
        for (int i = 0; i < barrier.getParties(); i++) {
            int runner=i;
            new Thread(()->{
               IO.println("Corredor "+runner+ " en salida");
                try {
                    barrier.await();
                    int time = 500+random.nextInt(500);
                    Thread.sleep(time);
                    IO.println("Corredor "+runner+ " cruza la meta en "+time+" milisegundos");
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

}

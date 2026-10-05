import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Ejercicio1 {
    /*
    5 corredores (hilos) se preparan (duermen un tiempo aleatorio
    simulando "calentar"). Ninguno puede empezar a correr hasta que todos
    estén listos. Usa un CountDownLatch(5): cada corredor hace countDown()
    al estar listo, y todos esperan un segundo CountDownLatch(1) que el hilo
    "juez" libera con countDown() para dar la salida.
     */

    public static final CountDownLatch latch = new CountDownLatch(5);
    Random random = new Random();

    public static final CountDownLatch salida = new CountDownLatch(1);

    void main() throws InterruptedException {
        for (int i = 0; i < latch.getCount(); i++) {
            int corredor =i;
            new Thread(()->{
                IO.println("Corredor "+ corredor +" calentando...");
                try {
                    Thread.sleep(5000+(random.nextInt(300)));
                } catch (InterruptedException e) {}
                IO.println("Corredor "+ corredor +" listo");
                latch.countDown();
                try {
                    salida.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                IO.println("Corredor "+corredor+" corriendo");
            }).start();
        }
        latch.await();
        IO.println("Todos listos");
        IO.println("Ya!!!!");
        salida.countDown();
    }

}

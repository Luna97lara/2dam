import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class CarreraChapas {
    static void main() {
        int numJugadores = 5;
        Random random = new Random();
        CyclicBarrier ronda = new CyclicBarrier(numJugadores,
                () -> System.out.println("¡Todos listos! Empieza la ronda"));
        CountDownLatch casillasAlcanzadas = new CountDownLatch(100);

        for (int i = 0; i < numJugadores; i++) {
            int distancia = random.nextInt(1, 21);
            int id = i+1;
            System.out.println("Jugador-" + id + " tira su chapa");
            Thread juez = new Thread(()-> {
                while(!Thread.currentThread().isInterrupted()) {
                    boolean rondaTerminada=false;
                    try {
                        ronda.await();
                        Thread.sleep(1000);
                        System.out.println("Jugador-" + id + " se mueve " + distancia + " casillas");
                        rondaTerminada=true;
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }if(rondaTerminada) {
                            casillasAlcanzadas.countDown();
                        }

                }
            });juez.start();
        }
    }
}

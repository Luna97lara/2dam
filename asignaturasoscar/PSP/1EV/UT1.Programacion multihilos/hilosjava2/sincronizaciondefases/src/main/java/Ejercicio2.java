import java.util.Random;
import java.util.concurrent.CyclicBarrier;

public class Ejercicio2 {
    /*
    4 hilos representan a un equipo que debe completar 3 fases de un proyecto.
    Ningún hilo puede pasar a la fase siguiente hasta que todos terminen la fase
    actual. Usa un CyclicBarrier(4, accionAlLlegarTodos) donde la acción imprime
    "Fase completada, todos avanzan".
     */

    public static CyclicBarrier barrier = new CyclicBarrier(4, () -> {
        IO.println("Fase completada, todos avanzan");
    });
    public static Random random = new Random();

    void main(){
        for (int i = 0; i < 4; i++) {
            int hilo=i;
            new Thread(()->{
                try {
                    for (int fase = 0; fase <=3 ; fase++) {
                        IO.println("Hilo "+hilo+" trabajando en fase "+fase);
                        Thread.sleep(500+(random.nextInt(200)));
                        barrier.await();
                    }

                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
}

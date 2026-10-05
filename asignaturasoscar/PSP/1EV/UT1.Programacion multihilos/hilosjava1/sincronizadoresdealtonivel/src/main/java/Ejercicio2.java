import java.util.concurrent.CountDownLatch;

public class Ejercicio2 {
    /*
    Usa un CountDownLatch inicializado a 5 para que el hilo
    principal espere a que 5 hilos trabajadores terminen de
    "cargar datos" antes de imprimir "Sistema listo".
     */

    public static final CountDownLatch latch = new CountDownLatch(5);

    void main() throws InterruptedException {
        for (int i = 0; i < latch.getCount(); i++) {
            int trabajador=i;
            new Thread(()->{
                IO.println("Hilo "+trabajador+" cargando...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {}
                IO.println("Hilo "+trabajador+" terminado");
                latch.countDown();
            }).start();
        }
        latch.await();
        IO.println("Sistema listo");
    }

}

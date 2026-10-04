import java.util.concurrent.CountDownLatch;

public class Ejercicio2 {
    /*
    Usa un CountDownLatch inicializado a 5 para que el hilo
    principal espere a que 5 hilos trabajadores terminen de
    "cargar datos" antes de imprimir "Sistema listo".
     */

    public static final CountDownLatch latch = new CountDownLatch(5);


}

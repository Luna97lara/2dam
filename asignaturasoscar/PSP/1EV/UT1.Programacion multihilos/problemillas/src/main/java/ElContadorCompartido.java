import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ElContadorCompartido {
    /*
    Crea una aplicación que simule un contador de visitas web. Tu
    aplicación debe:

    Funcionalidades requeridas:
    - Una clase ContadorVisitas con un método incrementarVisita()
    - Crear 1000 hilos que representen usuarios visitando la página web
    - Cada hilo debe incrementar el contador una sola vez
    - Cada hilo debe esperar un tiempo aleatorio entre 50-150ms antes de
    incrementar

    Implementaciones requeridas:
    - Sin sincronización - Para mostrar el problema de condiciones de
    carrera
    - Con synchronized - Usando palabra clave synchronized
    - Con AtomicInteger - Usando clases atómicas
     */
        /* Versión sin syncronized:
    public static int contador= 0;
    public void incrementarVisita(){
        contador++;
    }
    public int getContador(){
        return contador;
    }

    Random random = new Random();

    Thread[] hilos = new Thread[10000];

    void main() throws InterruptedException {
        long tiempoInicial = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            hilos [i] = new Thread(() -> {
                try {
                    Thread.sleep(random.nextInt(50, 150));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                incrementarVisita();
            });
            hilos[i].start();
        }

        for (int i = 0; i < 1000; i++) {
            hilos[i].join();
        }
        long tiempoFinal = System.currentTimeMillis();
        long tiempoTotal=tiempoFinal-tiempoInicial;
        IO.println("Visitas esperadas: 1000");
        IO.println("Visitas contadas: "+getContador());
        IO.println("Tiempo total: "+tiempoTotal+" ms");
    }
     */
    /* Versión syncronized:
    public static int contador= 0;
    public syncronized void incrementarVisita(){
        contador++;
    }
    public int getContador(){
        return contador;
    }

    Random random = new Random();

    Thread[] hilos = new Thread[10000];

    void main() throws InterruptedException {
        long tiempoInicial = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            hilos [i] = new Thread(() -> {
                try {
                    Thread.sleep(random.nextInt(50, 150));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                incrementarVisita();
            });
            hilos[i].start();
        }

        for (int i = 0; i < 1000; i++) {
            hilos[i].join();
        }
        long tiempoFinal = System.currentTimeMillis();
        long tiempoTotal=tiempoFinal-tiempoInicial;
        IO.println("Visitas esperadas: 1000");
        IO.println("Visitas contadas: "+getContador());
        IO.println("Tiempo total: "+tiempoTotal+" ms");

    }
     */
    public static AtomicInteger contador= new  AtomicInteger(0);
    public void incrementarVisita(){
        contador.incrementAndGet();
    }
    public int getContador(){
        return contador.get();
    }

    Random random = new Random();

    Thread[] hilos = new Thread[10000];

    void main() throws InterruptedException {
        IO.println("Esperando visitas...");
        long tiempoInicial = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            hilos [i] = new Thread(() -> {
                try {
                    Thread.sleep(random.nextInt(50, 150));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                incrementarVisita();
            });

            hilos[i].start();
        }
        for (int i = 0; i < 1000; i++) {
            hilos[i].join();
        }
        long tiempoFinal = System.currentTimeMillis();
        long tiempoTotal=tiempoFinal-tiempoInicial;
        IO.println("Visitas esperadas: 1000");
        IO.println("Visitas contadas: "+getContador());
        IO.println("Tiempo total: "+tiempoTotal+" ms");
    }

}


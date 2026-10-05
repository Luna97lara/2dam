import java.util.Random;

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

        IO.println("Visitas contadas: "+getContador());
    }

}


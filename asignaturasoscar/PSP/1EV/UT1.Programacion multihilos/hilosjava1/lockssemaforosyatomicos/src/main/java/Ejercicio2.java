public class Ejercicio2 {
    /*
    Reimplementa de nuevo el mismo contador, esta vez usando AtomicInteger
    y su método incrementAndGet(). Compara el código con las dos versiones
    anteriores: ¿cuál es más simple?
     */
    public static final AtomicInteger atomic = new AtomicInteger(0);
    public static void incrementar() {
        atomic.incrementAndGet();
    }

    public static int getContador() {
        return atomic.get();
    }
}

void main() throws InterruptedException {
    Ejercicio2 ejercicio2 = new Ejercicio2();
    Runnable tarea = () -> {
        for (int i = 0; i < 10000; i++) {
            ejercicio2.incrementar();
        };
    };
    Thread [] hilos = new Thread[10];
    for (int i = 0; i < hilos.length; i++) {
        hilos[i] = new Thread(tarea);
    }
    for (Thread t : hilos){
        t.start();
    }
    for (Thread t : hilos){
        t.join();
    }

    System.out.println(ejercicio2.getContador());
}

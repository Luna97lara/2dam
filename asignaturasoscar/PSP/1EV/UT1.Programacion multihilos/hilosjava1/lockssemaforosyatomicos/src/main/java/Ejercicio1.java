public class Ejercicio1 {
   /*
   Reimplementa el "contador roto" del ejercicio 3.1 usando ReentrantLock
   en lugar de synchronized. Recuerda liberar siempre el lock en un bloque
   finally.
    */
   public static int contador = 0;
   public static final ReentrantLock reentrantLock = new ReentrantLock();
    public static void incrementar() {
        reentrantLock.lock();
        try {
            contador++;
        } finally {
            reentrantLock.unlock();
        }
    }

    public static int getContador() {
        reentrantLock.lock();
        try {
            return contador;
        } finally {
            reentrantLock.unlock();
        }
    }
}

void main() throws InterruptedException {
    Ejercicio1 ejercicio1 = new Ejercicio1();
    Runnable tarea = () -> {
        for (int i = 0; i < 10000; i++) {
            ejercicio1.incrementar();
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

    System.out.println(ejercicio1.getContador());
}

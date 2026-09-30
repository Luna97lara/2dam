public class Ejercicio2 {
    /*
    Soluciona el ejercicio 3.1 añadiendo synchronized al método
    incrementar(). Verifica que ahora el resultado siempre es
    100.000.
     */
    public static int contador = 0;

    public synchronized void incrementar() {
        contador++;
    }

    public static int getContador() {
        return contador;
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


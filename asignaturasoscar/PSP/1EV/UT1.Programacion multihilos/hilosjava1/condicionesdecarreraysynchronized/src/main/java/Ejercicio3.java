public class Ejercicio3 {
    /*
    Reescribe el ejercicio 3.2 usando un bloque synchronized(this)
    en lugar de sincronizar el método completo. ¿Cambia el
    resultado? ¿Qué ventaja tiene sincronizar solo una parte del
    código?
     */
    public static int contador = 0;

    public void incrementar() {
        contador++;
    }

    public static int getContador() {
        return contador;
    }
}


void main() throws InterruptedException {
    Ejercicio3 ejercicio3 = new Ejercicio3();
    Runnable tarea = () -> {
        for (int i = 0; i < 10000; i++) {
            synchronized (this) {
                ejercicio3.incrementar();
            }
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

    System.out.println(ejercicio3.getContador());
}


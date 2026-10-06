public class Ejercicio4 {
    /*
    Crea dos objetos recursoA y recursoB. Lanza un hilo que bloquee
    recursoA y luego intente bloquear recursoB, y otro hilo que
    haga lo contrario (bloquear recursoB y luego recursoA).
    Ejecuta el programa varias veces hasta que se produzca un
    deadlock. Después, corrige el código estableciendo un orden
    global de adquisición de bloqueos.
     */
    private static final Object recursoA = new Object();
    private static final Object recursoB = new Object();

}

void main() {
//    Thread bloqueador1 = new Thread(() -> {
//        synchronized (Ejercicio4.recursoA){
//            System.out.println("Hilo 1 bloquea A");
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        } synchronized (Ejercicio4.recursoB){
//            System.out.println("Hilo 1 bloquea B");
//        }
//    });
//
//    Thread bloqueador2 = new Thread(() -> {
//        synchronized (Ejercicio4.recursoB){
//            System.out.println("Hilo 2 bloquea B");
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }  synchronized (Ejercicio4.recursoA){
//            System.out.println("Hilo 2 bloquea A");
//        }
//    });
//    bloqueador1.start();
//    bloqueador2.start();

    Runnable bloqueos = ()  -> {
        synchronized (Ejercicio4.recursoA) {
            System.out.println(Thread.currentThread().getName()+ " bloquea A");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } synchronized (Ejercicio4.recursoB) {
            System.out.println(Thread.currentThread().getName()+ " bloquea B");
        }
    };

    Thread bloqueador1 = new Thread(bloqueos, "bloqueador 1");
    Thread bloqueador2 = new Thread(bloqueos, "bloqueador 2");
    bloqueador1.start();
    bloqueador2.start();

}

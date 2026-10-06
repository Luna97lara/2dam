public class Ejercicio1 {
    /*
    Crea un hilo que duerma 3 segundos y, desde el main, imprime su estado
    (getState()) cada 500 ms mientras el hilo está vivo. Debes ver como mínimo los
    estados RUNNABLE/TIMED_WAITING y TERMINATED.
     */
    Thread hilito = new Thread (() -> {
        System.out.println("Hola");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    });

}

void main() throws InterruptedException {
    Ejercicio1 ej = new Ejercicio1();
    ej.hilito.start();
    while(ej.hilito.isAlive()){
        System.out.println(ej.hilito.getState());
        Thread.sleep(500);
    }
    System.out.println(ej.hilito.getState());
}
public class Ejercicio1 {
    /*
     Crea una clase CuentaBancaria con un saldo (int) y un método depositar(int cantidad)
     que haga saldo = saldo + cantidad (sin sincronizar). Lanza 100 hilos que depositen
     1 euro cada uno, de forma concurrente, y comprueba que el saldo final no es 100
     (condición de carrera).

    Requisitos:
        Primero demuestra el fallo (ejecuta varias veces y observa resultados distintos).
        Corrige el problema declarando depositar como synchronized.
        Repite con un bloque synchronized(this) en vez de sincronizar el método completo.
     */

    public static int saldo=0;
    public  static void depositar(int cantidad){
        saldo=saldo+cantidad;
    }

    void main() throws InterruptedException {

        for (int i = 0; i < 100; i++) {
            synchronized (this) {
                new Thread(()->{
                    depositar(1);
                }).start();
            }
        }
        Thread.sleep(500);
        IO.println(saldo);
    }

}

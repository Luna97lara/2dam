package org.example;

public class Ejercicio2 implements Runnable {
    /*
    Reimplementa el ejercicio 1.1 pero implementando la interfaz
    Runnable en lugar de heredar de Thread. Compara ambas
    soluciones: ¿qué ventajas tiene usar Runnable?
     */
    @Override
    public void run() {
        for (int i = 1; i < 11; i++) {
            System.out.println(Thread.currentThread().getName()+" "+i);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        /*
        Thread es "esta clase es un hilo", mientras que Runnable es
        "esta clase SABE QUÉ HACER cuando la ejecute un hilo"; por
        tanto, no gastas la bala de la herencia.
         */
    }

    void main(){
        Ejercicio2 contadorthread1 = new Ejercicio2();
        Ejercicio2 contadorthread2 = new Ejercicio2();
        Thread thread1 = new Thread(contadorthread1);
        Thread thread2 = new Thread(contadorthread2);
        thread1.start();
        thread2.start();
    }
}

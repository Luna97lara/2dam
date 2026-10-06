package org.example;

public class ContadorThread extends Thread {
    /*
    Crea una clase ContadorThread que extienda Thread y que
    imprima los números del 1 al 10 con una pausa de 500 ms entre
    cada uno. Lánzalo desde el main y observa qué ocurre si lanzas
    dos instancias a la vez.
     */
    @Override
    public void run() {
        for (int i = 1; i < 11; i++) {
            System.out.println(getName()+" "+i);
            try {
                  Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


    }

    static void main() {
        ContadorThread contadorThread1 = new ContadorThread();
        contadorThread1.start();
    // Aquí todos siguen un orden ascendente
        ContadorThread contadorThread2 = new ContadorThread();
        contadorThread2.start();
    // Aquí se ejecutan de manera random
    }
}

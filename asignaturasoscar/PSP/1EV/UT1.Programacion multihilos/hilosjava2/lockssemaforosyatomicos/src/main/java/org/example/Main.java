package org.example;

import java.util.Random;
import java.util.concurrent.Semaphore;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static Semaphore semaphore = new Semaphore(3, true);
    public static Random random = new Random();
    static void main() {
       /*
       Simula un aparcamiento con 3 plazas libres (Semaphore(3)) al que al llegar
       10 coches (hilos) en momentos aleatorios, cada coche debe:
        1. Pedir una plaza (acquire)
        2. "Aparcar" (dormir un tiempo aleatorio)
        3. Liberar la plaza (release())
        Imprime en cada momento cuántas plazas libres quedan
        */
        for (int i = 0; i <=10 ; i++) {
            int coche=i;
            new Thread (() -> {
                try {
                    IO.println("Coche en espera: "+coche);
                    semaphore.acquire();
                    IO.println("Coche aparcado: "+coche);
                    IO.println("Plazas libres: "+semaphore.availablePermits());
                    Thread.sleep(1000+random.nextInt(2500));
                    IO.println("Coche que se pira: "+coche);
                    semaphore.release();
                    IO.println("Plazas tras pirarse coche "+coche+":"+semaphore.availablePermits());

                } catch (InterruptedException e) {
                }
            }).start();
        }
    }
}

package org.example;


/*
1. Crea una clase Contador con un método incrementar() que haga contador++.
Lanza 10 hilos que llamen 10.000 veces cada uno a incrementar().
Comprueba que el resultado final no es 100.000. Explica por qué.
2. Soluciona el ejercicio 3.1 añadiendo synchronized al método incrementar().
Verifica que ahora el resultado siempre es 100.000.
3. Reescribe el ejercicio 3.2 usando un bloque synchronized(this) en lugar de
sincronizar el método completo.
¿Cambia el resultado? ¿Qué ventaja tiene sincronizar solo una parte del código?
4. Crea dos objetos recursoA y recursoB. Lanza un hilo que bloquee recursoA y luego intente bloquear recursoB,
y otro hilo que haga lo contrario (bloquear recursoB y luego recursoA).
Ejecuta el programa varias veces hasta que se produzca un deadlock.
Después, corrige el código estableciendo un orden global de adquisición de bloqueos.
 */

public class Contador {
    int contador=0;
    /*
    public void incrementar(){
            contador++;
    }
     */
    /*
    El resultado no es 100.000 porque, al haber 10 hilos accediendo al mismo
    dato, se están pisando entre ellos
     */
    /*
    public synchronized void incrementar(){
            contador++;
    }
    //Ahora, el resultado siempre es 100.000
     */
    public void incrementar(){
        synchronized (this) {
            contador++;
        }
    }
    /*
    El resultado es el mismo, pero como ventaja tiene que podemos dividir el código
    en lo que necesita protección y lo que no
     */
    public int getContador(){
        return contador;
    }

}

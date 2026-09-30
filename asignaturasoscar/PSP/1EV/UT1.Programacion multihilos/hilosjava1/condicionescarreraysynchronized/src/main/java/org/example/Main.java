package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() throws InterruptedException {
        Contador contador = new Contador();
        Runnable tarea = () -> {
            for (int i = 0; i < 10000; i++) {
                contador.incrementar();
            }
        };

        Thread hilo1=new Thread(tarea);
        Thread hilo2=new Thread(tarea);
        Thread hilo3=new Thread(tarea);
        Thread hilo4= new Thread(tarea);
        Thread hilo5=new Thread(tarea);
        Thread hilo6=new Thread(tarea);
        Thread hilo7=new Thread(tarea);
        Thread hilo8=new Thread(tarea);
        Thread hilo9=new Thread(tarea);
        Thread hilo10=new Thread(tarea);

        hilo1.start();
        hilo2.start();
        hilo3.start();
        hilo4.start();
        hilo5.start();
        hilo6.start();
        hilo7.start();
        hilo8.start();
        hilo9.start();
        hilo10.start();

        hilo1.join();
        hilo2.join();
        hilo3.join();
        hilo4.join();
        hilo5.join();
        hilo6.join();
        hilo7.join();
        hilo8.join();
        hilo9.join();
        hilo10.join();

         IO.println("Contador: "+contador.getContador());


         Object recursoA=new Object();
         Object recursoB=new Object();
         /*
         Primera parte del último ejercicio (falla)
         Thread h1 = new Thread(() ->{
             synchronized (recursoA){
                 IO.println("Hilo 1 tiene A");
                 try {
                     Thread.sleep(100);
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
                 synchronized (recursoB){
                     IO.println("Hilo 1 tiene A y B");
                 }
             }
         });

         Thread h2 =new Thread(() ->{
             synchronized (recursoB){
                 IO.println("Hilo 2 tiene B");
             }
             try {
                 Thread.sleep(100);
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
             synchronized (recursoA){
                 IO.println("Hilo 2 tiene B y A");
             }
         });
         h1.start();
         h2.start();
          */
        //Orden global de adquisición de bloqueos:
         Thread h1 = new Thread(() ->{
             synchronized (recursoA){
                 IO.println("Hilo 1 tiene A");
                 try {
                     Thread.sleep(100);
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
                 synchronized (recursoB){
                     IO.println("Hilo 1 tiene A y B");
                 }
             }
         });

         Thread h2 =new Thread(() ->{
             synchronized (recursoA){
                 IO.println("Hilo 2 tiene B");
             }
             try {
                 Thread.sleep(100);
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
             synchronized (recursoB){
                 IO.println("Hilo 2 tiene B y A");
             }
         });
         h1.start();
         h2.start();
    }
}

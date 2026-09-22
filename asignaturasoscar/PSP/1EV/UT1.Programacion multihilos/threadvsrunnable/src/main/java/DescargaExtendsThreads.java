public class DescargaExtendsThreads extends Thread { //Mi clase Descarga ES un hilo
    /*
    1. Thread, Runnable — Creación de hilos
    Ejercicio 1.1 — "Descarga simultánea de archivos"
    Simula la descarga de 4 archivos en paralelo. Cada archivo debe descargarse en
    su propio hilo, imprimiendo su progreso cada 200 ms hasta llegar al 100%.

    Requisitos:

    Implementa una versión usando una clase que extienda Thread.
    Implementa otra versión usando una clase que implemente Runnable,
    lanzada con new Thread(runnable).start().
    Cada hilo debe imprimir su nombre (Thread.currentThread().getName()) y
    el progreso.
    Compara ambos enfoques en un comentario: ¿cuándo conviene cada uno?
    Pista: usa Thread.ofPlatform().name("descarga-" + i).start(runnable)
    (API de hilos con nombre de Java 21+, disponible en 25) como alternativa
    moderna a new Thread(...).
     */
    //Cuando este hilo se ejecute, quiero que se haga una descarga:
    @Override
    public void run() { //método que contiene el trabajo que realizará el hilo
        for (int progreso = 0; progreso <=100 ; progreso+=20) {
            System.out.println(
                    //"Dame el hilo que está ejecutando esta instrucción ahora mismo+nombre del hilo"
                    Thread.currentThread().getName() + ": " + progreso + "%"
            );
            try {
                //"El hilo que está ejecutando esto duerme 200 milisegundos."
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}

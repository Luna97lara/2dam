import java.util.Random;

public class Ejercicio2 {
    /*
    Ejercicio 2.2 — join() en acción
    Lanza tres hilos que simulan descargas de archivos (con Thread.sleep de
    duración aleatoria). El hilo principal debe esperar a que todos terminen
    (usando join()) antes de imprimir "Todas las descargas han finalizado".
     */
    void main(){
        Random aleatorio = new Random();
        Thread [] descargas = new Thread[3];
        for(int i=0;i<descargas.length;i++){
           descargas[i]=new Thread(()->{
               try {
                   Thread.sleep(aleatorio.nextInt(500, 1000));
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           });
           System.out.println(descargas[i].getName());
           descargas[i].start();
        }
        for(Thread t : descargas){
            try {
                t.join();
            } catch (InterruptedException e) {}
        }
        System.out.println("Todas las descargas han finalizado");
    }


}

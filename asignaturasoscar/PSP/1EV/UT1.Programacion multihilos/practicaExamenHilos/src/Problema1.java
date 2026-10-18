import java.util.concurrent.atomic.AtomicInteger;

public class Problema1 {
    /*
    Crea una aplicación que simule un contador de visitas web. Tu aplicación debe:

    Funcionalidades requeridas:
        Una clase ContadorVisitas con un método incrementarVisita()
        Crear 1000 hilos que representen usuarios visitando la página web
        Cada hilo debe incrementar el contador una sola vez
        Cada hilo debe esperar un tiempo aleatorio entre 50-150ms antes de incrementar
    Implementaciones requeridas:
        Sin sincronización - Para mostrar el problema de condiciones de carrera
        Con synchronized - Usando palabra clave synchronized
        Con AtomicInteger - Usando clases atómicas

                            === CONTADOR DE VISITAS WEB ===
                            Esperando 1000 visitantes...

                            --- SIN SINCRONIZACIÓN ---
                            Visitas esperadas: 1000
                            Visitas contadas: 847  ❌ INCORRECTO
                            Tiempo: 152ms

                            --- CON SYNCHRONIZED ---
                            Visitas esperadas: 1000
                            Visitas contadas: 1000 ✅ CORRECTO
                            Tiempo: 167ms

                            --- CON ATOMICINTEGER ---
                            Visitas esperadas: 1000
                            Visitas contadas: 1000 ✅ CORRECTO
                            Tiempo: 149ms
     */

    private long tiempoInicial;
    private long tiempoFinal;

    public long tiempototal(){
        return tiempoFinal-tiempoInicial;
    }
//    Con sincronyzed:
//    private int contador;
//    public synchronized void incrementarVisita(){
//        contador++;
//    }
//  Con AtomicInteger:
    public static AtomicInteger contador = new AtomicInteger(0);

    public void incrementarVisita(){
        contador.incrementAndGet();
    }

    Thread [] usuarios = new Thread[1000];

    void main() throws InterruptedException {
        tiempoInicial = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            usuarios[i]=new Thread(()->{
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                incrementarVisita();
            });
            usuarios[i].start();
        }
        for (int i = 0; i < 1000; i++) {
            usuarios[i].join();
        }

        tiempoFinal = System.currentTimeMillis();
        tiempototal();
        IO.println("Visitas esperadas: 1000");
        IO.println("Visitas contadas: "+contador);
        IO.println("Tiempo: "+tiempototal()+"ms");

    }



}

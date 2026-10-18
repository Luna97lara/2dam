import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Problema3 {
    /*
    Simula un restaurante completo con el patrón Productor-Consumidor, donde camareros toman pedidos y cocineros los preparan.
    Restaurante con los clientes esperando en una mesa a comer, con cola de espera de clientes para sentarse en la mesa, y conteo de ganancias en el resturante por día, así como media de tiempo de espera de los clientes.
    Los camareros piden pedidos y sirven platos a los clientes.
    Actores del sistema:
        👨‍🍳 Cocineros: 3 hilos que preparan platos
        🍽️ Mesa de pedidos: Buffer limitado de máximo 10 pedidos
        👨‍💼 Camareros: 5 hilos que toman pedidos
        👥 Clientes: 100 clientes que llegan aleatoriamente
    Reglas de negocio:
        Tiempo de cocina: Entre 2-5 segundos por plato
        Tiempo por pedido: 1 segundo por camarero
        Mesa llena: Los camareros deben esperar
        Sin pedidos: Los cocineros deben esperar
        Llegada de clientes: Cada 500ms llega un cliente nuevo
    Implementación requerida:
        Usar BlockingQueue para la mesa de pedidos
        Usar hilos virtuales para los clientes
        Generar estadísticas en tiempo real
                === RESTAURANTE CONCURRENTE ===
                    Iniciando servicio con 3 cocineros y 5 camareros...

                    [14:32:15] Cliente-001 pide PIZZA
                    [14:32:15] Camarero-2 toma pedido PIZZA
                    [14:32:16] Cliente-002 pide ENSALADA
                    [14:32:19] Cocinero-1 termina PIZZA para Cliente-001
                    [14:32:20] Cliente-003 pide CARNE

                    --- ESTADÍSTICAS FINALES ---
                    Clientes atendidos: 100/100 ✅
                    Platos servidos: 100
                    Tiempo promedio de espera: 3.2s
                    Mesa llena (veces): 12
                    Cocineros esperando (tiempo): 45s total
                    Eficiencia: 85%
     */

    Thread[] cocineros = new Thread[3];
    Thread[] camareros = new Thread[5];
    Thread[] clientes = new Thread[100];
    Semaphore mesas = new Semaphore(20);
    BlockingQueue<TipoPlato> paseDeCocinaASala = new ArrayBlockingQueue<>(10);
    BlockingQueue<TipoPlato> paseDeSalaACocina= new ArrayBlockingQueue<>(10);
    BlockingQueue<TipoPlato> apuntarComandas= new ArrayBlockingQueue<>(10);
    boolean abierto = true;
    public static Random random = new Random();

    void main() throws InterruptedException {
        for (int i = 0; i < cocineros.length; i++) {
            cocineros[i]=new Thread(()->{
                while(abierto){
                    try {
                        TipoPlato comanda = paseDeSalaACocina.take();
                        IO.println(Thread.currentThread().getName()+" empieza a cocinar "+comanda);
                        Thread.sleep(comanda.getTiempoMs());
                        IO.println(Thread.currentThread().getName()+" termina de cocinar "+comanda);
                        paseDeCocinaASala.put(comanda);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            cocineros[i].setName("Cocinero "+i);
        }
        Arrays.stream(cocineros).forEach(Thread::start);

        for (int i = 0; i < camareros.length; i++) {
            camareros[i]=new Thread(()->{
                while(abierto){
                    try {
                        TipoPlato servir = paseDeCocinaASala.poll(100, TimeUnit.MILLISECONDS);
                        if(servir!=null){
                            IO.println(Thread.currentThread().getName()+" sirve "+servir);
                        }else {
                            TipoPlato comandaApuntada = apuntarComandas.poll(100, TimeUnit.MILLISECONDS);
                            if (comandaApuntada != null) {
                                IO.println(Thread.currentThread().getName() + " anota " + comandaApuntada);
                                paseDeSalaACocina.put(comandaApuntada);
                            }
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            camareros[i].setName("Camarero "+i);
        }
        Arrays.stream(camareros).forEach(Thread::start);

        for (int i = 0; i < clientes.length; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            clientes[i]=Thread.startVirtualThread(()->{
                try {
                    mesas.acquire();
                    IO.println(Thread.currentThread().getName()+" se sienta");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                TipoPlato pedido = TipoPlato.obtenerPlato(random.nextInt(0, 4));
                try {
                    IO.println(Thread.currentThread().getName()+" pide "+pedido);
                    apuntarComandas.put(pedido);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                mesas.release();
                IO.println(Thread.currentThread().getName()+" se va");
            });
            clientes[i].setName("Cliente "+i);
        }

        Arrays.stream(clientes).forEach(cliente -> {
            try {
                cliente.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        while (!apuntarComandas.isEmpty()) {
            Thread.sleep(100);
        }

        while (!paseDeSalaACocina.isEmpty()) {
            Thread.sleep(100);
        }

        while (!paseDeCocinaASala.isEmpty()) {
            Thread.sleep(100);
        }

        abierto = false;

        Arrays.stream(cocineros).forEach(hilo ->{
            try {
                hilo.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Arrays.stream(camareros).forEach(hilo ->{
            try {
                hilo.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });



        IO.println("Cerrado");

    }
}

    enum TipoPlato {
        ENSALADA(2000),    // 2 segundos
        PASTA(3000),       // 3 segundos
        PIZZA(4000),       // 4 segundos
        CARNE(5000);       // 5 segundos

        private final int tiempoMs;


        TipoPlato(int tiempoMs) {
            this.tiempoMs = tiempoMs;
        }

        public int getTiempoMs() {
            return tiempoMs;
        }

        public static TipoPlato obtenerPlato(int i) {
            return TipoPlato.values()[i];
        }
    }




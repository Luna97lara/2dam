import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ElRestauranteConcurrente {
    /*
    Simula un restaurante completo con el patrón Productor-Consumidor, donde
    camareros toman pedidos y cocineros los preparan.

    Actores del sistema:
        👨‍🍳 Cocineros: 3 hilos que preparan platos
        🍽️ Mesa de pedidos: Buffer limitado de máximo 10 pedidos
        👨‍💼 Camareros: 5 hilos que toman pedidos
        👥 Clientes: 100 clientes que llegan aleatoriamente

    Reglas de negocio:
        - Tiempo de cocina: Entre 2-5 segundos por plato
        - Tiempo por pedido: 1 segundo por camarero
        - Mesa llena: Los camareros deben esperar
        - Sin pedidos: Los cocineros deben esperar
        - Llegada de clientes: Cada 500ms llega un cliente nuevo

    Implementación requerida:
        - Usar BlockingQueue para la mesa de pedidos
        - Usar hilos virtuales para los clientes
        - Generar estadísticas en tiempo real
     */
    Thread[] cocineros = new Thread[3];
    Thread[] camareros = new Thread[5];
    Thread[] clientes = new Thread[100];
    Random random = new Random();
    BlockingQueue<TipoPlato> mesaPedidos = new ArrayBlockingQueue<>(10);
    BlockingQueue<TipoPlato> pedidosClientes = new ArrayBlockingQueue<>(100);
    AtomicInteger pedidosCocinados= new AtomicInteger(0);

    void main() throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            cocineros[i] = new Thread(() -> {
                try{
                    boolean cocinando=true;
                    while(cocinando) {
                        TipoPlato plato = mesaPedidos.take();
                        if(plato==TipoPlato.FIN){
                            cocinando=false;
                        } else {
                            IO.println("Cocinando: " + plato);
                            Thread.sleep(plato.getTiempoMs());
                            int cocinados=pedidosCocinados.incrementAndGet();
                            IO.println("Platos cocinados: "+cocinados);
                        }

                    }
                } catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
            });
            cocineros[i].start();
        }


        for (int i = 0; i < 5; i++) {
            camareros[i] = new Thread(() -> {
                try {
                    boolean trabajando=true;
                    while(trabajando) {
                        TipoPlato plato = pedidosClientes.take();
                        if(plato == TipoPlato.FIN){
                            trabajando=false;
                        } else {
                            mesaPedidos.put(plato);
                            Thread.sleep(1000);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            camareros[i].start();
        }

        for (int i = 0; i < 100; i++) {
            int numeroCliente=i+1;
            clientes[i]=Thread.startVirtualThread(() -> {
                try {
                    IO.println("Cliente nº"+numeroCliente+" sentado");
                    TipoPlato plato=TipoPlato.values()[random.nextInt(4)];
                    pedidosClientes.put(plato);
                } catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }

            });
            Thread.sleep(500);
        }

        for (int i = 0; i < 100; i++) {
           clientes[i].join();
        }

        for (int i = 0; i < 5; i++) {
            pedidosClientes.put(TipoPlato.FIN);
        }

        for (int i = 0; i < 5; i++) {
            camareros[i].join();
        }

        for (int i = 0; i < 3; i++) {
            mesaPedidos.put(TipoPlato.FIN);
        }

        for (int i = 0; i < 3; i++) {
            cocineros[i].join();
        }

    }



}

enum TipoPlato {
    ENSALADA(2000),    // 2 segundos
    PASTA(3000),       // 3 segundos
    PIZZA(4000),       // 4 segundos
    CARNE(5000),     // 5 segundos
    FIN(0);

    private final int tiempoMs;

    TipoPlato(int tiempoMs) {
        this.tiempoMs = tiempoMs;
    }

    public int getTiempoMs(){
        return tiempoMs;
    }
}

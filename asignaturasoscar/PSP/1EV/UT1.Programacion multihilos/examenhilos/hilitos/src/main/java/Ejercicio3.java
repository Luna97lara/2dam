/*
 PROBLEMA 3 - El Restaurante Concurrente de clase dado como solucion
 Patron Productor-Consumidor con 5 camareros (productores), 3 cocineros
 (consumidores) y 100 clientes (hilos virtuales).
 con los siguientes cambios
 1Pto El cierre del resturante deberá de ser más controlado, y no salir hasta parar a los cocineros y camareros.
 0,5 Pto  Tb pon precio a los platos y acumula cuanto ha ganado el restaurente en el servicio.
 2Pto Flujo de un cliente (hilo virtual):
   llega -> espera mesa libre durante un tiempo(elige el tiempo para que no se vayan todos) -> se sienta si la consigue y si no se va-> pide -> espera el plato
         -> se lo come -> libera la mesa -> se va
 2Pto Los camareros tienen PRIORIDAD para servir platos: antes de tomar un pedido nuevo
     o de meter uno en cocina, siempre miran si hay platos listos para entregarlos a los clientes.
 2Pto Cambia los clientes por un grupo de clientes.  Flujo de un grupo:
       la primera parte hasta que se sientan es igual que un cliente solo -> el camarero toma la comanda (un plato por persona)
             -> espera a que le sirvan TODOS los platos -> comen -> liberan la mesa -> se van

*/
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Ejercicio3 {

    enum TipoPlato {
        ENSALADA(2000, 850), PASTA(3000,1050), PIZZA(4000, 1000), CARNE(5000, 2000);
        final int tiempoMs;
        final int precioCentimos;
        TipoPlato(int tiempoMs, int precioCentimos) {
            this.tiempoMs = tiempoMs;
            this.precioCentimos = precioCentimos;
        }
        static TipoPlato aleatorio(Random r) {
            return values()[r.nextInt(values().length)];
        }
    }

    record Pedido(int cliente, TipoPlato plato, long horaCreacionMs, BlockingQueue<Pedido>destino) {}

    // --- Constantes del enunciado ---
    static final int COCINEROS = 3;
    static final int CAMAREROS = 5;
    static final int CLIENTES = 100;
    static final int CAPACIDAD_MESA = 10;
    static final long LLEGADA_CLIENTE_MS = 500;
    static final long TIEMPO_CAMARERO_MS = 1000;
    static final int MESAS = 30;

    // --- Recursos compartidos ---
    static final BlockingQueue<Pedido> colaCocina = new ArrayBlockingQueue<>(CAPACIDAD_MESA);
    static final CountDownLatch clientesAtendidos = new CountDownLatch(CLIENTES);
    // Cola intermedia clientes -> camareros (ilimitada: los clientes nunca se quedan sin atender)
    static final BlockingQueue<Pedido> colaClientes = new ArrayBlockingQueue<>(CLIENTES);
    static final Semaphore mesas = new Semaphore(MESAS);
    static final BlockingQueue<Pedido> platosListos = new ArrayBlockingQueue<>(CLIENTES);
    static final CountDownLatch clientesFinalizados = new CountDownLatch(CLIENTES);
    // --- Estadisticas ---
    static final AtomicInteger platosServidos = new AtomicInteger();
    static final AtomicLong esperaTotalMs = new AtomicLong();
    static final AtomicInteger paseCocinaLlenoCount = new AtomicInteger();
    static final AtomicLong cocinerosEsperandoMs = new AtomicLong();
    static final AtomicLong ventasTotales = new AtomicLong();

    static String ahora() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }



    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== RESTAURANTE CONCURRENTE ===");
        System.out.println("Iniciando servicio con " + COCINEROS + " cocineros y "
                + CAMAREROS + " camareros...\n");

        Thread[] cocineros = new Thread[COCINEROS];
        Thread[] camareros = new Thread[CAMAREROS];

        // --- Cocineros (consumidores) ---
        for (int i = 0; i < COCINEROS; i++) {
            final int id = i+1;
            cocineros[i]=Thread.ofPlatform().name("cocinero-" + id).start(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        long t0 = System.currentTimeMillis();
                        Pedido p = colaCocina.take(); // espera si no hay pedidos
                        cocinerosEsperandoMs.addAndGet(System.currentTimeMillis() - t0);

                        Thread.sleep(p.plato().tiempoMs); // cocina el plato
                        platosListos.put(p); //cuando termina de cocinar el pedido pasa a la cola de platos listos
                        esperaTotalMs.addAndGet(System.currentTimeMillis() - p.horaCreacionMs());
                        platosServidos.incrementAndGet();
                        ventasTotales.addAndGet(p.plato().precioCentimos);


                        System.out.println("[" + ahora() + "] Cocinero-" + id + " termina "
                                + p.plato() + " para Cliente-" + p.cliente());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // cierre del restaurante
                }
            });
        }

        // --- Camareros (productores) ---
        for (int i = 0; i < CAMAREROS; i++) {
            final int id = i+1;
            camareros[i]=Thread.ofPlatform().name("camarero-" + id).start(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        Pedido p = platosListos.poll();
                        if (p != null) {
                            p.destino().put(p);
                            System.out.println("[" + ahora() + "] Camarero-" + id
                                    + " entrega " + p.plato() + " a Cliente-" + p.cliente());
                        } else {
                            p = colaClientes.poll(100, TimeUnit.MILLISECONDS);
                            if (p != null) {
                                Thread.sleep(TIEMPO_CAMARERO_MS); // toma el pedido
                                System.out.println("[" + ahora() + "] Camarero-" + id
                                        + " toma pedido " + p.plato());
                                System.out.println("Capacidad libre en cocina: "
                                        + colaCocina.remainingCapacity());
                                if (colaCocina.remainingCapacity() == 0) {
                                    paseCocinaLlenoCount.incrementAndGet();
                                }
                                colaCocina.put(p); // espera si la mesa esta llena
                            }
//                            } else {
//                                System.out.println("[" + ahora() + "] Camarero-" + id
//                                        + " está esperando tareas ");
//                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // --- Clientes (hilos virtuales, llegan cada 500 ms) ---
        Thread generadorClientes = Thread.ofPlatform().name("puerta").start(() -> {
            for (int i = 1; i <= CLIENTES; i++) {
                final int id = i;
                Thread.ofVirtual().start(() -> {
                    boolean tieneMesa=false;
                    try {
                        if(mesas.tryAcquire(5, TimeUnit.SECONDS)) {
                            tieneMesa=true;
                            BlockingQueue<Pedido> destino = new ArrayBlockingQueue<>(1);
                            TipoPlato plato = TipoPlato.aleatorio(new Random());
                            System.out.println("[" + ahora() + "] Cliente-" + String.format("%03d", id)
                                    + " pide " + plato);
                            colaClientes.put(new Pedido(id, plato, System.currentTimeMillis(), destino));
                            Pedido platoRecibido = destino.take();
                            System.out.println("[" + ahora() + "] Cliente-" + String.format("%03d", id)
                                    + " recibe su " + platoRecibido.plato());
                            Thread.sleep(3000);
                            System.out.println("[" + ahora() + "] Cliente-" + String.format("%03d", id)
                                    + " termina de comer y libera su mesa.");

                        } else {
                            System.out.println("[" + ahora() + "] Cliente-" + String.format("%03d", id)
                                    + " se marcha: no hay mesas disponibles.");
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        if(tieneMesa) {
                            mesas.release();
                        }
                        clientesFinalizados.countDown();
                    }
                });
                try {
                    Thread.sleep(LLEGADA_CLIENTE_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        generadorClientes.join(); //Espera a que el hilo que va creando clientes termine de generar los 100
        clientesFinalizados.await(); //Espera a que los 100 clientes hayan terminado su recorrido

        //Detener camareros y cocineros:
        for(Thread camarero : camareros){
            camarero.interrupt();
        }

        for(Thread cocinero : cocineros){
            cocinero.interrupt();
        }

        //Esperar a que todos terminen
        for(Thread camarero : camareros){
            camarero.join();
        }

        for(Thread cocinero : cocineros){
            cocinero.join();
        }

        System.out.println("\n--- ESTADISTICAS FINALES ---");
        System.out.println("Clientes atendidos: " + CLIENTES + "/" + CLIENTES + " ✅");
        System.out.println("Platos servidos: " + platosServidos.get());
        System.out.println("Tiempo promedio de espera: "
                + (esperaTotalMs.get() / 1000.0 / CLIENTES) + "s");
        System.out.println("Pase de cocina lleno (veces): " + paseCocinaLlenoCount.get());
        System.out.println("Cocineros esperando (tiempo acumulado): "
                + (cocinerosEsperandoMs.get() / 1000) + "s total");
        System.out.println("Eficiencia: el sistema soporta " + CAMAREROS + " pedidos/s en entrada"
                + " y " + COCINEROS + " platos en paralelo en cocina.");
        System.out.println("Ganancias: " + ventasTotales.get() / 100.0 + " €");
    }

}

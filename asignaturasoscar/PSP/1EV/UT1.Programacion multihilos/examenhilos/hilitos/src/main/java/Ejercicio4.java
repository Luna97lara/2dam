import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Ejercicio4 {

    enum Departamento {
        VENTAS,
        MARKETING,
        INFORMATICA,
        RRHH;

        static Departamento aleatorio (Random r){ return values()[r.nextInt(values().length)];}
    }

    record Trabajo(int id, Departamento departamento, int numPaginas, String horaCreacion) {}

    static final int IMPRESORAS=4;
    static final int TRABAJADORES = 6;
    static final int TRABAJOS_POR_DIA=80;
    static final int DISTRIBUIDORES = 3;
    static final int CAPACIDAD_COLA_IMPRESION = 10;
    static final int CAPACIDAD_COLA_DESTINO = 30;
    static final int CAPACIDAD_COLA_DEMANDA = 20;
    static final long TIEMPO_DE_ESPERA = 500;

    static String ahora() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    static boolean estaTrabajando=true;

    static final BlockingQueue<Trabajo> colaImpresion = new ArrayBlockingQueue<>(CAPACIDAD_COLA_IMPRESION);
    static final BlockingQueue<Trabajo> colaDestino = new ArrayBlockingQueue<>(CAPACIDAD_COLA_DESTINO);
    static final BlockingQueue<Trabajo> colaGeneracion = new ArrayBlockingQueue<>(CAPACIDAD_COLA_DEMANDA);
    static final CountDownLatch trabajosTerminados = new CountDownLatch(TRABAJOS_POR_DIA);
    static final AtomicInteger numeroTrabajosFinalizados = new AtomicInteger();

    static void main() {
        System.out.println("=== IMPRENTA ===");

        long tiempoInicial = System.currentTimeMillis();


        Thread [] trabajadores = new Thread[TRABAJADORES];
        Thread [] impresoras  = new Thread [IMPRESORAS];
        Thread [] distribuidores = new Thread [DISTRIBUIDORES];
        Thread [] demandantes = new Thread[TRABAJOS_POR_DIA];

        for (int i = 0; i < DISTRIBUIDORES; i++) {
            final int id=i+1;
            distribuidores[i]=Thread.ofPlatform().name("Distribuidor-" + id).start(() -> {
                while (estaTrabajando) {
                    Trabajo t = colaDestino.poll();
                    if (t != null) {
                        System.out.println("[" + ahora() + "] Distribuidor-" + id + " ha entregado el trabajo "+t.id+" a "+t.departamento);
                        numeroTrabajosFinalizados.incrementAndGet();
                        trabajosTerminados.countDown();
                    }
                }
            });
        }

        for (int i = 0; i < IMPRESORAS; i++) {
            final int id=i+1;
            impresoras[i]=Thread.ofPlatform().name("Impresora-" + id).start(() -> {
                while(estaTrabajando) {
                    Trabajo t = colaImpresion.poll();
                    if (t != null) {
                        System.out.println("[" + ahora() + "]Impresora-" + id + " empieza a imprimir el trabajo "+t.id);
                        try {
                            Thread.sleep(200L *t.numPaginas);
                            System.out.println("[" + ahora() + "]Impresora-" + id + " termina de imprimir el trabajo "+t.id);
                            colaDestino.put(t);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }

        for (int i = 0; i < TRABAJADORES; i++) {
            final int id=i+1;
            trabajadores[i] =Thread.ofPlatform().name("Trabajador-" + id).start(() -> {
                while(estaTrabajando) {
                    Trabajo t = colaGeneracion.poll();
                    if (t != null) {
                        System.out.println("[" + ahora() + "]Trabajador-" + id + " recoge "+t.id);
                        try {
                            colaImpresion.put(t);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }

        Thread clientes = Thread.ofPlatform().name("Clientes").start(() -> {
            Random r = new Random();
            for (int i = 0; i < TRABAJOS_POR_DIA; i++) {
                final int id=i+1;
                demandantes[i] = Thread.ofPlatform().name("Demandante-" + i).start(() -> {
                    Trabajo t = new Trabajo(id, Departamento.aleatorio(r),r.nextInt(1,21), ahora());
                    try {
                        colaGeneracion.put(t);
                        System.out.println("[" + ahora() + "] Cliente-" +  id + " solicita trabajo");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                });
                try {
                    Thread.sleep(TIEMPO_DE_ESPERA);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        try {
            clientes.join();
            trabajosTerminados.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        estaTrabajando=false;

        Arrays.stream(impresoras).forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Arrays.stream(trabajadores).forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Arrays.stream(distribuidores).forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });



        System.out.println("\n--- ESTADISTICAS  ---");
        System.out.println("Trabajos realizados: "+numeroTrabajosFinalizados.get());
        System.out.println("Tiempo total: "+(System.currentTimeMillis()-tiempoInicial)/1000.0+"s");

    }


}

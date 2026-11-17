# PSP -- Problema 3: El restaurante concurrente

## 1. Objetivo

Simular un restaurante mediante hilos, colas bloqueantes y un semáforo.
La configuración trabajada incluye 3 cocineros, 5 camareros y 100
clientes.

## 2. Recursos compartidos

### Mesas: `Semaphore`

El semáforo limita cuántos clientes pueden estar sentados al mismo
tiempo:

``` java
static final int MESAS = 30;
static final Semaphore mesas = new Semaphore(MESAS);
```

Un cliente intenta conseguir mesa durante un máximo de 5 segundos:

``` java
if (mesas.tryAcquire(5, TimeUnit.SECONDS)) {
    // Ha conseguido mesa.
} else {
    // Se marcha.
}
```

Cuando termina, libera el permiso en `finally`:

``` java
finally {
    if (tieneMesa) {
        mesas.release();
    }
    clientesFinalizados.countDown();
}
```

`tieneMesa` evita liberar un permiso que nunca se adquirió.

### Cola de cocina: `BlockingQueue`

``` java
static final BlockingQueue<Pedido> colaCocina =
        new ArrayBlockingQueue<>(CAPACIDAD_MESA);
```

-   `put(p)`: introduce un pedido; espera si la cola está llena.
-   `take()`: extrae un pedido; espera si la cola está vacía.
-   `remainingCapacity()`: indica cuántos elementos caben todavía.

**Mesas y cola de cocina son recursos diferentes.** No deben compartir
una constante solo porque ambos tengan una capacidad limitada. Se
separaron `MESAS` y `CAPACIDAD_MESA`. Si se renombra esta última como
`CAPACIDAD_COLA_COCINA`, su significado será más claro.

### Cola de platos listos y cola privada

Los cocineros depositan los pedidos terminados en `platosListos`. Cada
cliente tiene una cola privada de capacidad 1:

``` java
BlockingQueue<Pedido> destino = new ArrayBlockingQueue<>(1);
```

El cliente espera su pedido con `destino.take()`. El camarero utiliza
`p.destino().put(p)` para entregarlo al cliente correcto.

## 3. El pedido

``` java
record Pedido(
        int cliente,
        Ejercicio3.TipoPlato plato,
        long horaCreacionMs,
        BlockingQueue<Pedido> destino
) {}
```

El campo `destino` permite que cada pedido se entregue a quien lo
realizó.

## 4. Funcionamiento

### Clientes

1.  Intentan conseguir mesa durante 5 segundos.
2.  Si no la consiguen, se marchan.
3.  Si la consiguen, crean su cola privada y eligen un plato.
4.  Envían el pedido y esperan su plato con `destino.take()`.
5.  Comen y liberan la mesa en `finally`.

### Camareros

La lógica trabajada comprueba primero los platos listos:

``` java
Pedido p = platosListos.poll();

if (p != null) {
    p.destino().put(p);
} else {
    p = colaClientes.poll(100, TimeUnit.MILLISECONDS);

    if (p != null) {
        Thread.sleep(TIEMPO_CAMARERO_MS);
        colaCocina.put(p);
    }
}
```

Diferencias entre métodos: - `poll()`: consulta inmediatamente; si no
hay elemento, devuelve `null`. - `poll(100, TimeUnit.MILLISECONDS)`:
espera hasta 100 ms; si no llega nada, devuelve `null`. - `take()`:
espera hasta que haya un elemento.

Si el `poll` devuelve `null`, el `if (p != null)` evita utilizar una
referencia nula. Al terminar el bloque, el `while` comienza
automáticamente otra iteración. No hace falta `continue`.

**Matiz:** se comprueban los platos listos al principio de cada vuelta.
Si el camarero está durmiendo o bloqueado en un `put`, no puede atender
otra tarea hasta que termine esa operación. Por tanto, se da prioridad a
las entregas cuando vuelve a comprobar las colas, pero no se interrumpe
una tarea en curso.

### Cocineros

``` java
long t0 = System.currentTimeMillis();
Pedido p = colaCocina.take();
cocinerosEsperandoMs.addAndGet(System.currentTimeMillis() - t0);

Thread.sleep(p.plato().tiempoMs);
platosListos.put(p);
```

El cocinero espera un pedido, lo cocina durante el tiempo indicado por
el plato y lo deposita en la cola de platos listos.

## 5. Interrupción y cierre controlado

Los trabajadores comprueban el estado de interrupción:

``` java
while (!Thread.currentThread().isInterrupted()) {
    // Trabajo.
}
```

Si una operación bloqueante lanza `InterruptedException`, se restaura la
interrupción:

``` java
catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}
```

-   `interrupt()` solicita la interrupción.
-   `join()` espera a que el hilo termine.
-   Guardar las referencias de los hilos permite interrumpirlos y
    esperarlos desde el hilo principal.

## 6. Precios y estadísticas

Los precios se guardan en céntimos:

``` java
enum TipoPlato {
    ENSALADA(2000, 850),
    PASTA(3000, 1050),
    PIZZA(4000, 1000),
    CARNE(5000, 2000);

    final int tiempoMs;
    final int precioCentimos;

    TipoPlato(int tiempoMs, int precioCentimos) {
        this.tiempoMs = tiempoMs;
        this.precioCentimos = precioCentimos;
    }
}
```

Para acumular dinero desde varios hilos se usa `AtomicLong`:

``` java
static final AtomicLong ventasTotales = new AtomicLong();
ventasTotales.addAndGet(p.plato().precioCentimos);
```

Los contadores compartidos utilizan `AtomicInteger` o `AtomicLong`.

Hay que actualizar cada estadística en el momento que representa. Si
`platosServidos` significa platos entregados, debe incrementarse al
entregar el plato, no cuando termina de cocinarse. Si significa platos
cocinados, corresponde incrementarlo en el cocinero.

## 7. El contador de cola llena

``` java
if (colaCocina.remainingCapacity() == 0) {
    paseCocinaLlenoCount.incrementAndGet();
}
colaCocina.put(p);
```

Cuenta las veces que la cola ya está llena justo antes de intentar
introducir un pedido. No cuenta necesariamente todas las esperas de
`put()`: puede haber espacio al consultar y llenarse antes de ejecutar
el `put`.

### El error encontrado

Se había utilizado la misma constante para el semáforo de mesas y la
cola de cocina. Eso acoplaba dos recursos independientes. Se solucionó
creando `MESAS` para el semáforo y manteniendo una constante propia para
la capacidad de la cola.

La lección es que el número de mesas y la capacidad de la cola de cocina
son límites independientes.

## 8. Conceptos para el examen

  -----------------------------------------------------------------------
  Concepto                            Función
  ----------------------------------- -----------------------------------
  `Thread.ofPlatform().start(...)`    Crea e inicia un hilo de
                                      plataforma.

  `Thread.ofVirtual().start(...)`     Crea e inicia un hilo virtual.

  `interrupt()`                       Solicita interrumpir un hilo.

  `isInterrupted()`                   Consulta el estado de interrupción.

  `join()`                            Espera a que termine otro hilo.

  `Semaphore`                         Limita el acceso concurrente a un
                                      recurso.

  `tryAcquire(tiempo, unidad)`        Intenta adquirir un permiso durante
                                      un tiempo.

  `release()`                         Devuelve un permiso.

  `BlockingQueue`                     Cola segura para varios hilos.

  `put()`                             Inserta; espera si está llena.

  `take()`                            Extrae; espera si está vacía.

  `poll()`                            Extrae inmediatamente o devuelve
                                      `null`.

  `poll(tiempo, unidad)`              Espera un tiempo máximo para
                                      extraer.

  `AtomicInteger` / `AtomicLong`      Actualizan contadores compartidos
                                      de forma segura.

  `CountDownLatch`                    Permite esperar a que se completen
                                      varias señales.

  `finally`                           Ejecuta limpieza aunque haya una
                                      excepción.
  -----------------------------------------------------------------------

## 9. Comprobaciones pendientes

En una de las ejecuciones se registraron 100 clientes atendidos, 51
platos servidos y finalización con `exit code 0`. También se comprobó
que los clientes reciben sus platos y liberan sus mesas.

Antes de dar el ejercicio por cerrado, conviene contrastar la
implementación final con todos los apartados del enunciado original,
especialmente el comportamiento de grupos de clientes si forma parte de
la entrega. También conviene revisar que las estadísticas se actualicen
en el momento correcto y que el cierre no interrumpa trabajadores
mientras quedan clientes esperando pedidos.

# PSP -- El Restaurante Concurrente

## Guía de estudio: comprensión del código original

Esta guía explica la estructura del ejercicio original antes de empezar
a modificarlo. El objetivo es entender qué hace cada elemento y cómo se
comunican los hilos.

------------------------------------------------------------------------

## 1. ¿Qué estamos simulando?

El restaurante tiene tres tipos de participantes:

-   **5 camareros (productores):** recogen los pedidos de los clientes y
    los introducen en la cola de cocina.
-   **3 cocineros (consumidores):** recogen pedidos de la cola y
    preparan los platos.
-   **100 clientes (hilos virtuales):** llegan al restaurante y generan
    pedidos.

El programa utiliza el patrón **Productor-Consumidor**.

### Funcionamiento básico

1.  Los clientes generan pedidos.
2.  Los pedidos entran en `colaClientes`.
3.  Los camareros recogen pedidos de `colaClientes`.
4.  Los camareros introducen los pedidos en `mesa`.
5.  Los cocineros recogen pedidos de `mesa`.
6.  Los cocineros preparan los platos.

Los camareros son productores porque introducen elementos en la cola de
cocina. Los cocineros son consumidores porque extraen elementos de ella.

------------------------------------------------------------------------

## 2. Importaciones

``` java
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
```

  -----------------------------------------------------------------------
  Importación                         Para qué sirve
  ----------------------------------- -----------------------------------
  `LocalTime`                         Obtener la hora actual.

  `DateTimeFormatter`                 Dar formato a la hora, por ejemplo
                                      `12:30:45`.

  `Random`                            Generar valores aleatorios.

  `ArrayBlockingQueue`                Crear una cola de capacidad
                                      limitada.

  `BlockingQueue`                     Interfaz para trabajar con colas
                                      bloqueantes.

  `CountDownLatch`                    Esperar a que termine un número
                                      determinado de tareas o señales.

  `AtomicInteger`                     Manejar contadores enteros
                                      compartidos de forma segura entre
                                      hilos.

  `AtomicLong`                        Lo mismo, pero con valores `long`.
  -----------------------------------------------------------------------

### Conceptos importantes

-   Una `BlockingQueue` puede bloquear un hilo si la cola está vacía o
    llena.
-   Las variables `Atomic` permiten que varios hilos modifiquen un
    contador de forma segura, sin perder actualizaciones.

Por ejemplo:

``` java
platosServidos.incrementAndGet();
```

Incrementa el contador de forma atómica, aunque varios cocineros lo
ejecuten al mismo tiempo.

------------------------------------------------------------------------

## 3. El enum `TipoPlato`

``` java
enum TipoPlato {
    ENSALADA(2000),
    PASTA(3000),
    PIZZA(4000),
    CARNE(5000);

    final int tiempoMs;

    TipoPlato(int tiempoMs) {
        this.tiempoMs = tiempoMs;
    }

    static TipoPlato aleatorio(Random r) {
        return values()[r.nextInt(values().length)];
    }
}
```

Un `enum` representa un conjunto cerrado de valores posibles. En este
caso, los cuatro platos que puede preparar el restaurante.

  Plato        Tiempo de preparación
  ---------- -----------------------
  ENSALADA     2.000 ms (2 segundos)
  PASTA        3.000 ms (3 segundos)
  PIZZA        4.000 ms (4 segundos)
  CARNE        5.000 ms (5 segundos)

### ¿Qué significa cada parte?

**`final int tiempoMs;`**

Es un atributo que almacena el tiempo de preparación de cada plato. Es
`final` porque no queremos modificarlo después de asignarlo.

**Constructor del enum:**

``` java
TipoPlato(int tiempoMs) {
    this.tiempoMs = tiempoMs;
}
```

Cuando se declara `ENSALADA(2000)`, el constructor guarda `2000` en el
atributo `tiempoMs` de ese valor.

**Método para elegir un plato aleatorio:**

``` java
static TipoPlato aleatorio(Random r) {
    return values()[r.nextInt(values().length)];
}
```

-   `values()` devuelve un array con todos los valores del enum.
-   `values().length` indica cuántos valores hay: 4.
-   `r.nextInt(4)` genera un número entre 0 y 3.
-   `values()[...]` devuelve el elemento que está en esa posición.

Por ejemplo, si sale el índice `2`, se devuelve `PIZZA`.

------------------------------------------------------------------------

## 4. El `record Pedido`

``` java
record Pedido(int cliente, TipoPlato plato, long horaCreacionMs) {}
```

Un `record` es una forma sencilla de crear una estructura para almacenar
datos.

Cada pedido guarda:

-   `cliente`: identificador del cliente.
-   `plato`: tipo de plato solicitado.
-   `horaCreacionMs`: momento en el que se creó el pedido, expresado en
    milisegundos.

Ejemplo:

``` java
Pedido pedido = new Pedido(
    15,
    TipoPlato.PIZZA,
    System.currentTimeMillis()
);
```

Para recuperar los datos se utilizan los métodos que Java genera
automáticamente:

``` java
pedido.cliente();
pedido.plato();
pedido.horaCreacionMs();
```

No es necesario escribir manualmente el constructor ni los getters.

------------------------------------------------------------------------

## 5. Las constantes

``` java
static final int COCINEROS = 3;
static final int CAMAREROS = 5;
static final int CLIENTES = 100;
static final int CAPACIDAD_MESA = 10;

static final long LLEGADA_CLIENTE_MS = 500;
static final long TIEMPO_CAMARERO_MS = 1000;
```

Son valores que utiliza todo el programa y que no queremos modificar
durante la ejecución.

-   `static`: el atributo pertenece a la clase, no a un objeto concreto.
-   `final`: no se puede reasignar una vez inicializado.

**Importante:** aunque se llame `CAPACIDAD_MESA`, en el código original
el valor se utiliza como capacidad de la cola de pedidos que esperan a
los cocineros. No representa necesariamente diez mesas físicas.

------------------------------------------------------------------------

## 6. Los recursos compartidos

Esta es una de las partes más importantes del ejercicio:

``` java
static final BlockingQueue<Pedido> mesa =
        new ArrayBlockingQueue<>(CAPACIDAD_MESA);

static final CountDownLatch clientesAtendidos =
        new CountDownLatch(CLIENTES);

static final BlockingQueue<Pedido> colaClientes =
        new ArrayBlockingQueue<>(CLIENTES);
```

### A) `mesa`

``` java
new ArrayBlockingQueue<>(CAPACIDAD_MESA);
```

Crea una cola que puede almacenar hasta 10 pedidos.

Los camareros introducen pedidos mediante:

``` java
mesa.put(p);
```

Los cocineros los recogen mediante:

``` java
mesa.take();
```

  -----------------------------------------------------------------------
  Método                              Funcionamiento
  ----------------------------------- -----------------------------------
  `put()`                             Introduce un elemento. Si la cola
                                      está llena, el hilo espera hasta
                                      que haya espacio.

  `take()`                            Extrae un elemento. Si la cola está
                                      vacía, el hilo espera hasta que
                                      haya un elemento.
  -----------------------------------------------------------------------

### B) `clientesAtendidos`

``` java
new CountDownLatch(CLIENTES);
```

Crea un contador que comienza en 100.

Cada vez que un cocinero termina un plato, ejecuta:

``` java
clientesAtendidos.countDown();
```

Esto resta uno al contador.

Cuando llega a cero, los hilos que estén esperando mediante `await()`
pueden continuar.

**Ojo:** en el programa original se llama `clientesAtendidos`, pero
realmente está contando platos terminados. Como cada cliente pide un
plato, coincide. Cuando se cambie el ejercicio para trabajar con grupos,
habrá que revisar esta lógica.

### C) `colaClientes`

``` java
new ArrayBlockingQueue<>(CLIENTES);
```

Crea otra cola, con capacidad para 100 pedidos.

-   Los clientes introducen sus pedidos.
-   Los camareros recogen los pedidos de esta cola.

------------------------------------------------------------------------

## 7. Las estadísticas

``` java
static final AtomicInteger platosServidos = new AtomicInteger();
static final AtomicLong esperaTotalMs = new AtomicLong();
static final AtomicInteger mesaLlenaCount = new AtomicInteger();
static final AtomicLong cocinerosEsperandoMs = new AtomicLong();
```

Son contadores compartidos por varios hilos.

  -----------------------------------------------------------------------
  Variable                            Qué contabiliza
  ----------------------------------- -----------------------------------
  `platosServidos`                    Número de platos terminados.

  `esperaTotalMs`                     Tiempo acumulado desde que se crea
                                      cada pedido hasta que termina de
                                      cocinarse.

  `paseCocinaLlenoCount`                    Número de veces que se detecta la
                                      cola de cocina llena.

  `cocinerosEsperandoMs`              Tiempo acumulado que los cocineros
                                      pasan esperando pedidos.
  -----------------------------------------------------------------------

Ejemplos:

``` java
platosServidos.incrementAndGet();
```

Suma uno al contador.

``` java
esperaTotalMs.addAndGet(5000);
```

Añade 5.000 milisegundos al tiempo acumulado.

------------------------------------------------------------------------

## 8. El método `ahora()`

``` java
static String ahora() {
    return LocalTime.now().format(
        DateTimeFormatter.ofPattern("HH:mm:ss")
    );
}
```

Es un método auxiliar que devuelve la hora actual con el formato
`HH:mm:ss`.

Por ejemplo:

``` text
12:35:08
```

Se utiliza para que los mensajes de la consola indiquen cuándo ocurre
cada acción. No tiene una función especial de concurrencia.

------------------------------------------------------------------------

## 9. Resumen: qué hace cada recurso

  -----------------------------------------------------------------------
  Elemento                            Función principal
  ----------------------------------- -----------------------------------
  `TipoPlato`                         Representa los platos y su tiempo
                                      de preparación.

  `Pedido`                            Guarda los datos de cada pedido.

  `colaClientes`                      Cola donde los clientes dejan sus
                                      pedidos para los camareros.

  `mesa`                              Cola donde los camareros dejan los
                                      pedidos para los cocineros.

  `CountDownLatch`                    Permite esperar a que se completen
                                      las 100 señales previstas.

  `AtomicInteger`                     Contador entero seguro para
                                      concurrencia.

  `AtomicLong`                        Contador largo seguro para
                                      concurrencia.

  `put()`                             Introduce un elemento en una cola
                                      bloqueante.

  `take()`                            Extrae un elemento de una cola
                                      bloqueante.

  `countDown()`                       Resta una unidad al contador del
                                      latch.

  `await()`                           Espera hasta que el contador del
                                      latch llegue a cero.
  -----------------------------------------------------------------------

## 10. Comprobación rápida

**Pregunta 1.** ¿Qué ocurre si un camarero ejecuta `mesa.put(p)` cuando
la cola ya tiene 10 pedidos?

-   El pedido se pierde.
-   El camarero espera hasta que haya espacio.
-   Se elimina el pedido más antiguo.

**Respuesta:** el camarero espera hasta que haya espacio, porque `put()`
bloquea cuando la cola está llena.

**Pregunta 2.** ¿Qué hace `clientesAtendidos.countDown()`?

-   Suma uno al contador.
-   Resta uno al contador.
-   Detiene todos los hilos.

**Respuesta:** resta uno al contador. Cuando llega a cero, los hilos que
esperan con `await()` pueden continuar.

**Pregunta 3.** ¿Por qué utilizamos `AtomicInteger` para
`platosServidos`?

-   Para que varios hilos puedan modificarlo de forma segura.
-   Para convertirlo en una cola.
-   Para que solo lo pueda modificar el hilo principal.

**Respuesta:** porque varios cocineros pueden terminar platos
simultáneamente y actualizar el contador sin perder incrementos.

------------------------------------------------------------------------

## Próximas modificaciones del examen

Una vez comprendido el código original, el ejercicio pide cinco cambios:

1.  **Cierre controlado:** conseguir que el restaurante no termine hasta
    detener ordenadamente a camareros y cocineros.
2.  **Precios y ganancias:** asignar un precio a cada plato y acumular
    los ingresos del servicio.
3.  **Flujo de un cliente:** llegada, espera por una mesa durante un
    tiempo limitado, pedido, espera del plato, comida, liberación de
    mesa y salida.
4.  **Prioridad de los camareros:** antes de tomar un pedido nuevo o
    introducirlo en cocina, comprobar si hay platos preparados para
    entregar.
5.  **Grupos de clientes:** sustituir clientes individuales por grupos;
    cada grupo pide un plato por persona y espera a recibir todos los
    platos antes de comer.

**Consejo de estudio:** no memorices el programa entero. Aprende qué
responsabilidad tiene cada hilo, qué cola utiliza y qué método
bloqueante emplea. Así podrás adaptar la solución si cambian los números
o las condiciones del enunciado.

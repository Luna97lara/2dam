# Modelo de examen PSP -- Programación multihilos

**Tiempo orientativo:** 90 minutos\
**Lenguaje:** Java

## Problema 1. El centro de impresión

Una empresa tiene un centro de impresión compartido por varios
departamentos. Hay 4 impresoras y 6 trabajadores. Durante la jornada se
reciben 80 trabajos de impresión.

Cada trabajo tiene un identificador, el departamento que lo solicita, el
número de páginas y la hora en que se registra. El número de páginas
será aleatorio entre 1 y 20.

Los trabajadores van solicitando trabajos. Cuando uno consigue un
trabajo, lo envía a la cola de impresión. La cola tiene una capacidad
limitada. Si no puede introducirlo porque está llena, tendrá que
esperar.

Las impresoras recogen los trabajos de la cola y los procesan. El tiempo
de impresión depende del número de páginas: 200 milisegundos por página.
Cuando termina un trabajo, queda disponible para su recogida.

Hay 3 empleados encargados de entregar los trabajos terminados a los
departamentos correspondientes. Cada departamento debe recibir
exclusivamente sus propios trabajos.

El programa debe mostrar los mensajes necesarios para seguir lo que
sucede durante la ejecución.

Se pide:

1.  Crear las estructuras necesarias para representar los trabajos.
2.  Simular la actividad de trabajadores, impresoras y empleados de
    entrega.
3.  Evitar que dos impresoras procesen el mismo trabajo.
4.  Conseguir que cada trabajo llegue al departamento que lo solicitó.
5.  Controlar el acceso a las impresoras, teniendo en cuenta que solo
    hay 4 disponibles.
6.  Registrar el número total de trabajos finalizados y el tiempo total
    transcurrido desde que se registraron hasta que se entregaron.
7.  Cuando hayan terminado todos los trabajos, finalizar los hilos y
    mostrar las estadísticas.

## Problema 2. El aparcamiento

Un aparcamiento dispone de 12 plazas. Durante la simulación llegan 60
vehículos. Cada vehículo tiene una matrícula identificativa y permanece
estacionado un tiempo aleatorio entre 2 y 6 segundos.

Los vehículos llegan de forma progresiva. Si al llegar no hay plazas
libres, esperan un máximo de 3 segundos. Si transcurrido ese tiempo no
consiguen aparcar, abandonan el aparcamiento.

Al salir, cada vehículo libera su plaza. El aparcamiento cobra 2 euros
por cada segundo de estancia.

Se pide:

1.  Realizar la simulación utilizando hilos.
2.  Controlar las plazas disponibles.
3.  Gestionar el tiempo máximo de espera de los vehículos.
4.  Calcular la recaudación total.
5.  Mostrar cuántos vehículos han aparcado y cuántos han abandonado el
    aparcamiento.
6.  Asegurar que las plazas se liberan correctamente.
7.  Finalizar la ejecución de forma controlada.

## Problema 3. Modificación

Modifica el problema 1 para que los trabajos urgentes tengan preferencia
sobre los trabajos normales.

No se permite que un trabajo normal que ya esté siendo procesado se
interrumpa.

**Fin del examen.**

# Modelo 2 – Sistema de reservas de cine

## Objetivo

Desarrollar una aplicación web utilizando Java Servlets que permita gestionar las reservas de un cine.

Cada usuario tendrá su propio cine y podrá gestionar sus películas y reservas. Toda la información se almacenará en memoria, utilizando sesiones HTTP (`HttpSession`). No se utilizará ninguna base de datos.

## 1. Clases del modelo

Se deberán implementar las siguientes clases:

### Pelicula

Representa una película disponible en el cine.

Atributos:
- `titulo` (`String`): título de la película.
- `horario` (`String`): horario de la sesión.
- `aforo` (`int`): capacidad máxima de la sala.

### Reserva

Representa una reserva realizada por un cliente.

Atributos:
- `nombreCliente` (`String`): nombre de la persona que realiza la reserva.
- `pelicula` (`Pelicula`): película para la que se realiza la reserva.
- `numEntradas` (`int`): número de entradas reservadas.

### Cine

Representa el cine gestionado por cada usuario.

Atributos:
- `nombre` (`String`): nombre del cine.
- Una colección de películas disponibles.
- Una colección de reservas realizadas.

La clase deberá proporcionar los métodos necesarios para añadir películas, realizar reservas, cancelar reservas y consultar la información almacenada.

## 2. Servlets

Se deberán implementar los siguientes servlets:

### 2.1. ConfigurarCine – `/setup`

Recibe los siguientes parámetros:
- `nombre`: nombre del cine.

Debe crear un objeto `Cine` y almacenarlo en la sesión HTTP del usuario.

Inicialmente, el cine no tendrá películas ni reservas.

### 2.2. Reservar – `/reservar`

Recibe los siguientes parámetros:
- `cliente`: nombre del cliente.
- `titulo`: título de la película.
- `entradas`: número de entradas que desea reservar.

Requisitos:
- Comprobar que existe una sesión y que el cine está configurado.
- Comprobar que todos los parámetros son válidos.
- Verificar que la película existe.
- Comprobar que quedan suficientes plazas disponibles.
- Si todo es correcto, crear y almacenar la reserva.

### 2.3. CancelarReserva – `/cancelar`

Recibe los siguientes parámetros:
- `cliente`: nombre del cliente.
- `titulo`: título de la película.

Debe localizar y cancelar la reserva correspondiente.

Si no existe ninguna reserva que coincida con esos datos, deberá devolver un error HTTP apropiado.

### 2.4. ConsultarReserva – `/consultar`

Recibe los siguientes parámetros:
- `cliente`: nombre del cliente.
- `titulo`: título de la película.

Debe buscar la reserva correspondiente y mostrar:
- Nombre del cliente.
- Película reservada.
- Número de entradas.

Si no existe, deberá devolver un error HTTP 404.

### 2.5. ConsultarCine – `/cine`

No recibe parámetros.

Debe mostrar:
- Nombre del cine.
- Número de películas disponibles.
- Número total de reservas.
- Número total de entradas reservadas.

## 3. Validaciones y restricciones

- Todos los servlets deberán comprobar que existe una sesión y que el cine ha sido configurado previamente.
- No se permitirán parámetros nulos ni vacíos.
- El número de entradas deberá ser un entero positivo.
- No se podrá superar el aforo máximo de una película.
- No se podrán cancelar reservas inexistentes.
- Las búsquedas de películas y reservas deberán realizarse correctamente.
- Se utilizarán códigos de estado HTTP apropiados para comunicar errores.
- Cada usuario tendrá sus propios datos, sin compartir información con otras sesiones.
- No se utilizarán variables estáticas para almacenar información de usuarios.

## 4. Pruebas recomendadas

Una vez implementada la aplicación, comprobar los siguientes casos:

1. Intentar reservar sin haber configurado el cine.
2. Configurar un cine correctamente.
3. Intentar reservar una película que no existe.
4. Intentar reservar un número de entradas negativo o igual a cero.
5. Realizar una reserva válida.
6. Intentar superar el aforo disponible.
7. Consultar una reserva existente.
8. Consultar una reserva inexistente.
9. Cancelar una reserva existente.
10. Intentar cancelar una reserva inexistente.
11. Consultar la información general del cine.
12. Comprobar que dos sesiones diferentes mantienen sus propios datos.

---

**Objetivo final:** practicar el uso de Servlets, sesiones HTTP, gestión de colecciones de objetos, validación de parámetros y comunicación mediante códigos de estado HTTP.
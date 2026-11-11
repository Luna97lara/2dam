MODELO 1 – Biblioteca personal

Dificultad: similar a la práctica de la caja fuerte.

Objetivos

Al finalizar esta actividad, el alumno deberá ser capaz de:

Crear y gestionar sesiones HTTP.

Compartir objetos entre distintos servlets mediante sesiones.

Recibir y validar parámetros de peticiones.

Implementar operaciones de almacenamiento y consulta.

Gestionar errores mediante códigos HTTP.

Enunciado

Se desea desarrollar una aplicación web que permita a cada usuario gestionar su propia biblioteca personal.

Cada usuario tendrá una biblioteca independiente asociada a su sesión.

La aplicación deberá disponer de los siguientes servlets:

1. ConfigurarBiblioteca (/config)

Recibe los siguientes parámetros:

nombre: nombre de la biblioteca.

capacidad: número máximo de libros que puede almacenar.

Se deberá validar que ambos parámetros sean correctos y que la capacidad sea un número entero positivo.

Se creará una biblioteca y se almacenará en la sesión del usuario.

2. AnadirLibro (/anadir)

Permite añadir un libro a la biblioteca.

Recibe:

titulo: título del libro.

autor: autor del libro.

Condiciones:

Debe existir una sesión y una biblioteca configurada.

No se podrán añadir libros si se ha alcanzado la capacidad máxima.

El título y el autor no pueden ser nulos ni estar vacíos.

Si todo es correcto, el libro se almacenará en la biblioteca.

3. ConsultarLibro (/consultar)

Recibe el parámetro titulo.

Busca el libro correspondiente y devuelve su autor.

Si no existe, deberá responder con un error HTTP adecuado.

4. ConsultarBiblioteca (/biblioteca)

Devuelve el nombre de la biblioteca, su capacidad y el número actual de libros almacenados.

Requisitos adicionales

Cada sesión debe tener su propia biblioteca.

No se debe permitir añadir ni consultar libros antes de configurar la biblioteca.

Se deberán validar todos los parámetros recibidos.

Se utilizarán respuestas HTTP para comunicar los errores.
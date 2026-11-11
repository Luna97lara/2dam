# Modelo 1 – Biblioteca personal

## Objetivo

Desarrollar una aplicación web mediante Java Servlets que permita a cada usuario gestionar su propia biblioteca personal.

No se utilizará base de datos. Toda la información se almacenará en memoria, utilizando sesiones HTTP (`HttpSession`).

## 1. Clases del modelo

### Libro
Atributos:
- `titulo` (`String`).
- `autor` (`String`).

### Biblioteca
Atributos:
- `nombre` (`String`).
- `capacidad` (`int`): número máximo de libros.
- Una colección de objetos `Libro`.

Implementar métodos para:
- Añadir un libro, siempre que exista espacio.
- Buscar un libro por su título.
- Obtener el número de libros almacenados.

## 2. Servlets

### 2.1. SetupBiblioteca – `/setup`

Recibe:
- `nombre`: nombre de la biblioteca.
- `capacidad`: capacidad máxima.

Requisitos:
- Validar los parámetros.
- Comprobar que la capacidad sea un entero positivo.
- Crear una biblioteca y almacenarla en la sesión.

### 2.2. AddLibro – `/add`

Recibe:
- `titulo`: título del libro.
- `autor`: autor del libro.

Requisitos:
- Comprobar que existe una sesión y una biblioteca configurada.
- Validar los parámetros.
- Comprobar que no se ha alcanzado la capacidad máxima.
- Añadir el libro a la biblioteca.

### 2.3. ConsultarLibro – `/consultar`

Recibe:
- `titulo`: título del libro.

Requisitos:
- Com
package com.example.listascompose.data


import com.example.listascompose.domain.model.Persona
import com.example.listascompose.domain.model.Libro

object Personas {
    private val personas = mutableListOf(
        Persona(
            "Lucía Álvarez", true, mutableListOf(
                Libro("El nombre del viento", "Patrick Rothfuss", "Fantasía", true),
                Libro("Dune", "Frank Herbert", "Ciencia ficción", false),
                Libro("Fundación", "Isaac Asimov", "Aventura", true),
                Libro("El Hobit", "J.R.R. Tolkien", "Fantasía", false),
                Libro("Neuromante", "William Gibson", "Aventura", true),
                )
        ),
        Persona(
            "Marco Castillo",true, mutableListOf(
                Libro("El nombre del viento", "Patrick Rothfuss", "Fantasía", true),
                Libro("Dune", "Frank Herbert", "Ciencia ficción", false),
            )
        ),
        Persona(
            "Sofía Pérez", true,mutableListOf(
                Libro("El nombre del viento", "Patrick Rothfuss", "Fantasía", true),
                Libro("Dune", "Frank Herbert", "Ciencia ficción", false),
                Libro("Fundación", "Isaac Asimov", "Aventura", true),
                Libro("El Hobit", "J.R.R. Tolkien", "Fantasía", false),
                Libro("Crepúsculo", "Stephanie Meyer", "Fantasía", true),
                Libro("Luna Nueva", "Stephanie Meyer", "Fantasía", true),
                Libro("Eclipse", "Stephanie Meyer", "Fantasía", true),
                )
        ),
        Persona("Daniel Rodríguez",  true, mutableListOf(
            Libro("El nombre del viento", "Patrick Rothfuss", "Fantasía", true),
            )),
    )

    fun dameLista() = personas.toList()

    fun deletePersona(persona: Persona): Unit {
        personas.removeAll { it.id == persona.id }
    }

    fun addLibro(persona: Persona): Unit {
        personas.add(persona)
    }

}
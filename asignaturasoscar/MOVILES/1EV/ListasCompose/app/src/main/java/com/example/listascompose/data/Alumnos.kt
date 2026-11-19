package com.example.listascompose.data


import com.example.listascompose.domain.model.Alumno
import com.example.listascompose.domain.model.Boletin

object Alumnos {
    private val alumnos = mutableListOf(
        Alumno(
            "Lucía", "Álvarez García", "1ºDAM", "18", "lucia.alvarez@iesquevedo.es", true, mutableListOf(
                Boletin("Programación", 3),
                Boletin("Bases de Datos", 7),
                Boletin("Lenguaje de Marcas", 8),
                Boletin("Sistemas Informáticos", 5),
            )
        ),
        Alumno(
            "Marco", "Castillo Ruiz", "2ºASIR", "19", "marco.castillo@iesquevedo.es", true,mutableListOf(
                Boletin("Seguridad", 3),
                Boletin("Empresa", 7),
                Boletin("Lenguaje de Marcas", 8),
                Boletin("Sistemas Informáticos", 5)
            )
        ),
        Alumno(
            "Sofía", "Pérez Martín", "1ºSMR", "17", "sofia.perez@iesquevedo.es", false,mutableListOf(
                Boletin("FOL", 3),
                Boletin("Inglés", 7),
                Boletin("Redes", 8),
                Boletin("Sistemas Informáticos", 5)
            )
        ),
        Alumno("Daniel", "Rodríguez López", "2ºDAM", "20", "daniel.rodriguez@iesquevedo.es", true, mutableListOf(
            Boletin("PSP", 3),
            Boletin("Acceso a Datos", 2),
            Boletin("Móviles", 8),
            Boletin("SGE", 7)
        )),
        Alumno("Elena", "Moreno Sánchez", "1ºASIR", "18", "elena.moreno@iesquevedo.es", true,mutableListOf(
            Boletin("Redes", 3),
            Boletin("Sistemas Informáticos", 7),
            Boletin("FOL", 8),
            Boletin("Bases de Datos", 5)
        )),
    )

    fun dameLista() = alumnos.toList()

    fun deleteAlumno(alumno: Alumno): Unit {
        alumnos.removeAll { it.id == alumno.id }
    }

    fun addAlumno(alumno: Alumno): Unit {
        alumnos.add(alumno)
    }

}
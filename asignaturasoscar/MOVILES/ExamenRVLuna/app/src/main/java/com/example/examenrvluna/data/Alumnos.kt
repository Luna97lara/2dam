package com.example.examenrvluna.data

import com.example.examenrvluna.domain.model.Alumno

object Alumnos {
    private val alumnos = mutableListOf(
        Alumno("Lucía", "Álvarez García", "1ºDAM", 18, "lucia.alvarez@iesquevedo.es"),
        Alumno("Marco", "Castillo Ruiz", "2ºASIR", 19, "marco.castillo@iesquevedo.es"),
        Alumno("Sofía", "Pérez Martín", "1ºSMR", 17, "sofia.perez@iesquevedo.es"),
        Alumno("Daniel", "Rodríguez López", "2ºDAM", 20, "daniel.rodriguez@iesquevedo.es"),
        Alumno("Elena", "Moreno Sánchez", "1ºASIR", 18, "elena.moreno@iesquevedo.es")
    )

    fun dameLista() = alumnos.toList()

    fun deleteAlumno(alumno: Alumno): Unit {
        alumnos.removeAll { it.id==alumno.id}
    }

    fun addAlumno(alumno: Alumno) : Unit {
        alumnos.add(alumno)
    }

}
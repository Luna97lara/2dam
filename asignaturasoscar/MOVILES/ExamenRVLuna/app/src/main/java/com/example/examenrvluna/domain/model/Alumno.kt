package com.example.examenrvluna.domain.model

import java.util.UUID

data class Alumno(
    val nombre: String,
    val apellidos: String,
    val curso: String,
    val edad: Int,
    val correo: String,
    val id: String = UUID.randomUUID().toString()
) {
    override fun toString(): String {
        return "Nombre\n$nombre\nApellidos=$apellidos \nCurso=$curso\nEdad=$edad \nCorreo=$correo)"
    }
}

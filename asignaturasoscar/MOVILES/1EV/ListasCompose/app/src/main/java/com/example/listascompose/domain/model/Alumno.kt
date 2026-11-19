package com.example.listascompose.domain.model

import java.util.UUID

data class Alumno(
    val nombre: String,
    val apellidos: String,
    val curso: String,
    val edad: String,
    val correo: String,
    val activo: Boolean,
    val notas: List<Boletin>,
    val id: String = UUID.randomUUID().toString(),
)

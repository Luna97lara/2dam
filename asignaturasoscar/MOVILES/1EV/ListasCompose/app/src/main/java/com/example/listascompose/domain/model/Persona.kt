package com.example.listascompose.domain.model

import java.util.UUID

data class Persona(
    val nombre: String,
    val tieneLibros: Boolean,
    val libro: List<Libro>,
    val id: String = UUID.randomUUID().toString(),
)

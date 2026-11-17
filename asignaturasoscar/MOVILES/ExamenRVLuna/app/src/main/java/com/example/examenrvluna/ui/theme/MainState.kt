package com.example.examenrvluna.ui.theme

import com.example.examenrvluna.domain.model.Alumno

data class MainState(
    val texto: String = "",
    val alumno: Alumno,
    val alumnos: List<Alumno> = emptyList(),
    val alumnoSeleccionado: Alumno? = null,
    val error: String? = null
)

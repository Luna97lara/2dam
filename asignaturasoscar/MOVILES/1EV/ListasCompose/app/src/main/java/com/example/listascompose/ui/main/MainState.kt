package com.example.listascompose.ui.main

import com.example.listascompose.domain.model.Alumno

data class MainState (
    val alumno: Alumno,
    val alumnos: List<Alumno>,
    val alumnoSeleccionado: Alumno?,
    val selected: Boolean,
    val error: String?
)
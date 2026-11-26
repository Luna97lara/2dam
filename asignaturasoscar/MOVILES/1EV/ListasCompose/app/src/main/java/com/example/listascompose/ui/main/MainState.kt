package com.example.listascompose.ui.main

import com.example.listascompose.domain.model.Persona

data class MainState (
    val persona: Persona,
    val personas: List<Persona>,
    val personaSeleccionado: Persona?,
    val selected: Boolean,
    val error: String?
)
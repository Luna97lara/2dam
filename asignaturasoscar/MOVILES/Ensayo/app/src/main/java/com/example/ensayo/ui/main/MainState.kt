package com.example.ensayo.ui.main

import com.example.ensayo.domain.model.Amigo

data class MainState(
    val delegado: String,
    val subdelegado: String,
    val amiguitos: List<Amigo>,
)

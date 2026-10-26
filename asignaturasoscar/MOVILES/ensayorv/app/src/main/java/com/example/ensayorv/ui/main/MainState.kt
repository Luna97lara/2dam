package com.example.ensayorv.ui.main

import com.example.ensayorv.domain.model.Gatito

data class MainState(
    val favorito: String,
    val gatitos : List<Gatito>,
    val error : String? = null
)

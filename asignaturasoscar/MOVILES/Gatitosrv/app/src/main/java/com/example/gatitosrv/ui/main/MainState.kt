package com.example.gatitosrv.ui.main

import com.example.gatitosrv.domain.model.Gatito

data class MainState(
    val favorito:  String,
    val gatitos: List<Gatito>,
    val error: String? = null
)

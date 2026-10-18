package com.example.ejemploxml.ui.main

import com.example.ejemploxml.domain.model.Politico

data class MainState(
    val presidente: String,
    val politicos: List<Politico>,
    val error: String?=null,
)

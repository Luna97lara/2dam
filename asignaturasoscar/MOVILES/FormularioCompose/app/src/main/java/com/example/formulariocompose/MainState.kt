package com.example.formulariocompose

data class MainState(
    val nombre: String,
    val apellidos: String,
    val email: String,
    val direccion: String,
    val contador: Int,
    val textoContador: String
)

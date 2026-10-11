package com.example.ensayo.data

import com.example.ensayo.domain.model.Amigo

object Amiguitos {

    private val amigos = mutableListOf(
        Amigo("Luni", "gitanilla", 1),
        Amigo("Mike", "panchitillo", 1),
        Amigo("Dani", "payico", 1),
        Amigo("Cristopher", "panchitillo", 2),
        Amigo("Alehandro", "amarillo", 2),
        Amigo("Yeibi", "rumanillo", 3),
        Amigo("Samut", "rumanillo", 3),
        Amigo("Dani M","payico",2)
    )

    fun dameDelegado() = amigos[0]

    fun dameSubdelegado() = amigos[3]

    fun dameLista() = amigos.toList()
}
package com.example.ejemploxml.data

import com.example.ejemploxml.domain.model.Politico
import kotlin.random.Random

object Politicos {
    private val politicos = mutableListOf(
        Politico("Perro","PSOE",0),
        Politico("Ayuso","PP", 1000)
    )

    fun damePresidente() = politicos[0]

    fun dameLista() = politicos.toList()

    fun add(p: Politico) {
        if (Random.nextDouble()>0.60)
            throw RuntimeException ("Error al añadir")
        politicos.add(p)
    }

    fun del(p: Politico) {
        if (Random.nextDouble()>0.40)
            throw RuntimeException("Error al borrar")
        politicos.remove(p)
    }

}
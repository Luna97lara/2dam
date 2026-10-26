package com.example.ensayorv.data

import com.example.ensayorv.domain.model.Gatito
import kotlin.random.Random

object Gatitos {

    private val gatitos = mutableListOf(
        Gatito("Lucifer", "Luna", 0),
        Gatito("Groucho", "Susan", 2),
        Gatito("Jojo", "Susan", 2),
        Gatito("Hermes", "Vane", 0),
        Gatito("Cangrejo", "Vane", 0),
        Gatito("Kyo", "Luna y Dani", 1),
        Gatito("Kuina", "Luna y Dani", 1),
        Gatito("Buffy", "Oscar", 2)
    )

    fun dameFavorito() = gatitos [0]

    fun dameLista() = gatitos.toList()

    fun add(g: Gatito) {
        if (Random.nextDouble()>0.6)
            throw RuntimeException("Error al añadir gatito")
        gatitos.add(g)
    }

    fun del(g: Gatito) {
        if (Random.nextDouble()>0.4)
            throw RuntimeException("Error al borrar gatito")
        gatitos.remove(g)
    }
}
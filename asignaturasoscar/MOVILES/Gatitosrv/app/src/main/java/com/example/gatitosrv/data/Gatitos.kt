package com.example.gatitosrv.data

import com.example.gatitosrv.domain.model.Gatito

object Gatitos {

    private val gatitos = mutableListOf(
        Gatito("Kyo", "Luna y Dani", 2),
        Gatito("Kuina", "Luna y Dani", 2),
        Gatito("Groucho", "Dani", 4),
        Gatito("Jojo", "Susan", 4),
        Gatito("Buffy", "Oscar y Pili", 1),
        Gatito("Roger", null, 0),
        Gatito("Hermes", "Vane", 0),
        Gatito("Calatea", "Viqui", 2),
        Gatito("Rio", "Cande", 0),
        Gatito("Lucifer", "Luna", 0)
    )

    fun dameFavorito(): Gatito {
        for (item in gatitos){
            if(item.nombre.equals("Lucifer"))
                return item
        }
        return gatitos[0]
    }

    fun dameLista() = gatitos.toList()

    fun add(g: Gatito){
        gatitos.add(g)
    }

    fun del(g: Gatito){
        gatitos.remove(g)
    }

}
package com.example.ejemplillooscar.data

import com.example.ejemplillooscar.domain.model.Politico

object Politicos {

    private val politicos = mutableListOf(
        Politico("Almeida", "PP",1000000),
        Politico("Julio Anguita", "PCE", 0)
    )

    fun damePresidente() = politicos[1]

}
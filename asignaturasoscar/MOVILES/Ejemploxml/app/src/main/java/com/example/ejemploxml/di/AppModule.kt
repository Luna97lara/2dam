package com.example.ejemploxml.di

import com.example.ejemploxml.domain.useCases.AddPoliticoUseCase
import com.example.ejemploxml.domain.useCases.CargarPoliticosUseCase
import com.example.ejemploxml.domain.useCases.DamePresidenteUseCase
import com.example.ejemploxml.domain.useCases.DelPoliticoUseCase

object AppModule {

    val damePresidenteUseCase: DamePresidenteUseCase = DamePresidenteUseCase()
    val addPoliticoUseCase: AddPoliticoUseCase = AddPoliticoUseCase()
    val cargarPoliticosUseCase: CargarPoliticosUseCase = CargarPoliticosUseCase()

    val delPoliticoUseCase: DelPoliticoUseCase = DelPoliticoUseCase()

}
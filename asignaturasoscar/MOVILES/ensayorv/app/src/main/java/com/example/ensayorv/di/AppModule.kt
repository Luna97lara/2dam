package com.example.ensayorv.di

import com.example.ensayorv.domain.useCases.AddGatitoUseCase
import com.example.ensayorv.domain.useCases.DameFavoritoUseCase
import com.example.ensayorv.domain.useCases.DameListaUseCase
import com.example.ensayorv.domain.useCases.DelGatitoUseCase

object AppModule {

    val dameFavoritoUseCase : DameFavoritoUseCase = DameFavoritoUseCase()

    val dameListaUseCase : DameListaUseCase = DameListaUseCase()

    val addGatitoUseCase : AddGatitoUseCase = AddGatitoUseCase()

    val delGatitoUseCase : DelGatitoUseCase = DelGatitoUseCase()

}
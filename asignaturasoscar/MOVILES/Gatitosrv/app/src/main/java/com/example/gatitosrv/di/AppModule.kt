package com.example.gatitosrv.di

import com.example.gatitosrv.domain.useCases.AddGatitoUseCase
import com.example.gatitosrv.domain.useCases.DameFavoritoUseCase
import com.example.gatitosrv.domain.useCases.DelGatitoUseCase
import com.example.gatitosrv.domain.useCases.ListaGatitosUseCase

object AppModule {

    val dameFavoritoUseCase: DameFavoritoUseCase = DameFavoritoUseCase()

    val addGatitoUseCase: AddGatitoUseCase = AddGatitoUseCase()

    val delGatitoUseCase: DelGatitoUseCase = DelGatitoUseCase()

    val ListaGatitosUseCase: ListaGatitosUseCase = ListaGatitosUseCase()

}
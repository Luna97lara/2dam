package com.example.ensayo.di

import com.example.ensayo.domain.useCases.DameDelegadoUseCase
import com.example.ensayo.domain.useCases.DameSubdelegadoUseCase

object AppModule {

    val dameDelegadoUseCase: DameDelegadoUseCase = DameDelegadoUseCase()

    val dameSubdelegadoUseCase: DameSubdelegadoUseCase = DameSubdelegadoUseCase()

}
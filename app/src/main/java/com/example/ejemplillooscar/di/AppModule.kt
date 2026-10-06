package com.example.ejemplillooscar.di

import com.example.ejemplillooscar.domain.useCases.DamePresidenteUseCase
import com.example.ejemplillooscar.ui.main.MainViewModel
import java.sql.DatabaseMetaData

object AppModule {
    fun provideMainViewModel() :  MainViewModel = MainViewModel(damePresidenteUseCase)

    val damePresidenteUseCase: DamePresidenteUseCase = DamePresidenteUseCase()
}
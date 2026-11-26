package com.example.listascompose.di

import com.example.listascompose.domain.useCases.AddLibroUseCase
import com.example.listascompose.domain.useCases.DameListaUseCase
import com.example.listascompose.domain.useCases.DeletePersonaUseCase


object AppModule {
    val dameListaUseCase: DameListaUseCase = DameListaUseCase()
    val addLibroUseCase: AddLibroUseCase = AddLibroUseCase()
    val deletePersonaUseCase: DeletePersonaUseCase = DeletePersonaUseCase()

}
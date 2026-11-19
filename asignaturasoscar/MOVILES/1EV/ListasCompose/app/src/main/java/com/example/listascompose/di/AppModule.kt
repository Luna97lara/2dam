package com.example.listascompose.di

import com.example.listascompose.domain.useCases.AddAlumnoUseCase
import com.example.listascompose.domain.useCases.DameListaUseCase
import com.example.listascompose.domain.useCases.DeleteAlumnoUseCase


object AppModule {
    val dameListaUseCase: DameListaUseCase = DameListaUseCase()
    val addAlumnoUseCase: AddAlumnoUseCase = AddAlumnoUseCase()
    val deleteAlumnoUseCase: DeleteAlumnoUseCase = DeleteAlumnoUseCase()

}
package com.example.examenrvluna.di

import com.example.examenrvluna.domain.useCases.AddAlumnoUseCase
import com.example.examenrvluna.domain.useCases.DameListaUseCase
import com.example.examenrvluna.domain.useCases.DeleteAlumnoUseCase

object AppModule {
    val dameListaUseCase: DameListaUseCase = DameListaUseCase()
    val addAlumnoUseCase: AddAlumnoUseCase = AddAlumnoUseCase()
    val deleteAlumnoUseCase: DeleteAlumnoUseCase = DeleteAlumnoUseCase()

}
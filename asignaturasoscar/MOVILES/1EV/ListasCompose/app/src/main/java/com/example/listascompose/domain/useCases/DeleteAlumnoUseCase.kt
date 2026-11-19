package com.example.listascompose.domain.useCases

import com.example.listascompose.data.Alumnos
import com.example.listascompose.domain.model.Alumno


class DeleteAlumnoUseCase {
    fun deleteAlumno(alumno: Alumno) = Alumnos.deleteAlumno(alumno)
}
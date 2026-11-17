package com.example.examenrvluna.domain.useCases

import com.example.examenrvluna.data.Alumnos
import com.example.examenrvluna.domain.model.Alumno

class DeleteAlumnoUseCase {
    fun deleteAlumno(alumno: Alumno) = Alumnos.deleteAlumno(alumno)
}
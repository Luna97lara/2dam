package com.example.listascompose.domain.useCases

import com.example.listascompose.data.Alumnos
import com.example.listascompose.domain.model.Alumno

class AddAlumnoUseCase {
    fun addAlumno(alumno: Alumno) = Alumnos.addAlumno(alumno)
}
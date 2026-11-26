package com.example.listascompose.domain.useCases

import com.example.listascompose.data.Personas
import com.example.listascompose.domain.model.Persona


class DeletePersonaUseCase {
    fun deleteAlumno(persona: Persona) = Personas.deletePersona(persona)
}
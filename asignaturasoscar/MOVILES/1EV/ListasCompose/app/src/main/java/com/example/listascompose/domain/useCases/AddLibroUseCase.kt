package com.example.listascompose.domain.useCases

import com.example.listascompose.data.Personas
import com.example.listascompose.domain.model.Persona

class AddLibroUseCase {
    fun addLibro(persona: Persona) = Personas.addLibro(persona)
}
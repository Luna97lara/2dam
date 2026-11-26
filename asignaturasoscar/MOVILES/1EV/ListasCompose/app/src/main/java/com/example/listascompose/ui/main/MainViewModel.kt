package com.example.listascompose.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.listascompose.domain.model.Persona
import com.example.listascompose.domain.useCases.AddLibroUseCase
import com.example.listascompose.domain.useCases.DameListaUseCase
import com.example.listascompose.domain.useCases.DeletePersonaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel(
    private val dameListaUseCase: DameListaUseCase,
    private val addLibroUseCase: AddLibroUseCase,
    private val deletePersonaUseCase: DeletePersonaUseCase
) : ViewModel(

) {
    private val _state = MutableStateFlow(
        MainState(
            persona = Persona("", true, listOf(), "0"),
            personas = emptyList(),
            personaSeleccionado = null,
            selected = false,
            error = null
        )
    )

    fun onPersonaChange(persona: Persona) {
        _state.update { it.copy(persona = persona) }
    }

    val state: StateFlow<MainState> = _state.asStateFlow()

    init {
        onCargarLista()
    }

    fun onCargarLista() {
        _state.update { it.copy(personas = dameListaUseCase.dameLista()) }
    }

    fun onSeleccionarPersona(persona: Persona) {
        _state.update { currentState ->
            val nuevoSeleccionado = if (currentState.personaSeleccionado == persona) null else persona
            currentState.copy(personaSeleccionado = nuevoSeleccionado)
        }
    }

    fun onDeletePersonaSeleccionada() {
        val alumnoDelete = _state.value.personaSeleccionado ?: return
        deletePersonaUseCase.deleteAlumno(alumnoDelete)
        _state.update {
            it.copy(
                personas = dameListaUseCase.dameLista(),
                personaSeleccionado = null
            )
        }
    }

    fun onAddLibro() {
        val currentAlumno = _state.value.persona
        if (currentAlumno.nombre.isBlank()
//            || currentAlumno.apellidos.isBlank()
//            || currentAlumno.curso.isBlank()
//            || currentAlumno.edad.isBlank()
//            || currentAlumno.correo.isBlank()
        ) {
            _state.update {
                it.copy(error = "Faltan datos por introducir")
            }
        } else {
            try {
                addLibroUseCase.addLibro(currentAlumno)
                _state.update {
                    it.copy(
                        personas = dameListaUseCase.dameLista(),
                        persona = Persona("", true, mutableListOf(), "0"),
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = e.message ?: "Error al guardar el alumno")
                }
            }
        }
    }

    fun limpiarError() {
        _state.update {
            it.copy(error = null)
        }
    }
}

class MainViewModelFactory(
    private val dameListaUseCase: DameListaUseCase,
    private val addLibroUseCase: AddLibroUseCase,
    private val deletePersonaUseCase: DeletePersonaUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return MainViewModel(
                dameListaUseCase,
                addLibroUseCase,
                deletePersonaUseCase,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

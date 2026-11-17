package com.example.examenrvluna.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.examenrvluna.domain.model.Alumno
import com.example.examenrvluna.domain.useCases.AddAlumnoUseCase
import com.example.examenrvluna.domain.useCases.DameListaUseCase
import com.example.examenrvluna.domain.useCases.DeleteAlumnoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel(
    private val dameListaUseCase: DameListaUseCase,
    private val addAlumnoUseCase: AddAlumnoUseCase,
    private val deleteAlumnoUseCase: DeleteAlumnoUseCase
) : ViewModel (

) {
    private val _state = MutableStateFlow(
        MainState(
            alumno = Alumno("", "", "", 0, "")
        )
    )

    fun onNombreChange(nuevoNombre: String) {
        _state.update { it.copy(alumno = it.alumno.copy(nombre = nuevoNombre)) }
    }

    fun onApellidoChange(nuevosApellidos: String) {
        _state.update { it.copy(alumno = it.alumno.copy(apellidos = nuevosApellidos)) }
    }

    fun onCursoChange(nuevoCurso: String) {
        _state.update { it.copy(alumno = it.alumno.copy(curso = nuevoCurso)) }
    }

    fun onCorreoChange(nuevoCorreo: String) {
        _state.update { it.copy(alumno = it.alumno.copy(correo = nuevoCorreo)) }
    }

    fun onEdadChange(nuevaEdad: Int) {
        _state.update { it.copy(alumno = it.alumno.copy(edad = nuevaEdad)) }
    }
    val state: StateFlow<MainState> = _state.asStateFlow()

    init {
        onCargarLista()
    }

    fun onCargarLista() {
        _state.update { it.copy(alumnos = dameListaUseCase.dameLista()) }
    }

    fun onSeleccionarAlumno(alumno: Alumno) {
        _state.update { currentState ->
            val nuevoSeleccionado = if (currentState.alumnoSeleccionado == alumno) null else alumno
            currentState.copy(alumnoSeleccionado = nuevoSeleccionado)
        }
    }

    fun onDeleteAlumnoSeleccionado() {
        val alumnoDelete = _state.value.alumnoSeleccionado ?: return
        deleteAlumnoUseCase.deleteAlumno(alumnoDelete)
        _state.update {
            it.copy(
                alumnos = dameListaUseCase.dameLista(),
                alumnoSeleccionado = null
            )
        }
    }

    fun onAddAlumno() {
        val currentAlumno = _state.value.alumno
        if (currentAlumno.nombre.isBlank()
            || currentAlumno.apellidos.isBlank()
            || currentAlumno.curso.isBlank()
            || currentAlumno.edad.toString().isBlank()
            || currentAlumno.correo.isBlank()
        ) {
            _state.update {
                it.copy(error = "Faltan datos por introducir")
                return
            }
            try {
                addAlumnoUseCase.addAlumno(currentAlumno)
                _state.update {
                    it.copy(
                        alumnos = dameListaUseCase.dameLista(),
                        alumno = Alumno("", "", "", 0, ""),
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = e.message ?: "Error al guardar el alumno")
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
        private val addAlumnoUseCase: AddAlumnoUseCase,
        private val deleteAlumnoUseCase: DeleteAlumnoUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return MainViewModel(
                    dameListaUseCase,
                    addAlumnoUseCase,
                    deleteAlumnoUseCase,
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}






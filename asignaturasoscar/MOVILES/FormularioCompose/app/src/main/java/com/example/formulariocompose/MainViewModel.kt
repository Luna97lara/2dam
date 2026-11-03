package com.example.formulariocompose

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    fun onChangeNombre(nombre: String) {
        _state.update { it.copy(nombre = nombre) }
    }

    fun onChangeApellidos(apellidos: String) {
        _state.update { it.copy(apellidos = apellidos) }

    }

    fun onChangeEmail(email: String) {
        _state.update { it.copy(email = email) }

    }

    fun onChangeDireccion(direccion: String) {
        _state.update { it.copy(direccion = direccion) }

    }

    fun onClickButton(){
        _state.update { it.copy(contador = it.contador+1) }
    }

    private val _state = MutableStateFlow(
        MainState(
            nombre = "Daniel",
            apellidos = "Gómez Teruel",
            email = "danigt@nigg.com",
            direccion = "Calle Gandhi, 71",
            contador = 0,
            textoContador = "Nº de envíos: "
        )
    )

    val state: StateFlow<MainState> = _state.asStateFlow()



    class MainViewModelFactory(

    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(com.example.formulariocompose.MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return MainViewModel(
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
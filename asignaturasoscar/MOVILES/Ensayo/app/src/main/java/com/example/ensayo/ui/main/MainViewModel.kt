package com.example.ensayo.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ensayo.domain.useCases.DameDelegadoUseCase
import com.example.ensayo.domain.useCases.DameSubdelegadoUseCase

class MainViewModel (
    private val dameDelegadoUseCase: DameDelegadoUseCase,
    private val dameSubdelegadoUseCase: DameSubdelegadoUseCase,
    ) : ViewModel() {

    private val _state = MutableLiveData<MainState>(MainState("inicio", "continuacion", listOf()))

    val state : LiveData<MainState> = _state

    fun handleDameDelegado(){
        var delegado = dameDelegadoUseCase.dameDelegado()
        _state.value = _state.value?.copy(delegado = delegado.toString())
    }

    fun handleDameSubdelegado(){
        var subdelegado = dameSubdelegadoUseCase.dameSubdelegado()
        _state.value = _state.value?.copy(subdelegado = subdelegado.toString())
    }


    class MainViewModelFactory(
        private val dameDelegadoUseCase: DameDelegadoUseCase,
        private val dameSubdelegadoUseCase: DameSubdelegadoUseCase,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return MainViewModel(
                    dameDelegadoUseCase,
                    dameSubdelegadoUseCase,
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

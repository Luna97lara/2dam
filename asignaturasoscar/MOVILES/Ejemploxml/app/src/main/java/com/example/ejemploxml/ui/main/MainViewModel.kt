package com.example.ejemploxml.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ejemploxml.domain.model.Politico
import com.example.ejemploxml.domain.useCases.AddPoliticoUseCase
import com.example.ejemploxml.domain.useCases.CargarPoliticosUseCase
import com.example.ejemploxml.domain.useCases.DamePresidenteUseCase
import com.example.ejemploxml.domain.useCases.DelPoliticoUseCase

class MainViewModel (
    private val damePresidenteUseCase: DamePresidenteUseCase,
    private val cargarPoliticosUseCase: CargarPoliticosUseCase,
    private val addPoliticoUseCase: AddPoliticoUseCase,
    private val delPoliticoUseCase: DelPoliticoUseCase,
) : ViewModel(){

    private val _state = MutableLiveData<MainState>(MainState("inicio", listOf()))

    val state : LiveData<MainState> = _state

    init {
        handleCargaLista()
    }


    fun handleDamePresidente(){
        var presi = damePresidenteUseCase.damePresidente()
        _state.value = _state.value?.copy(presidente = presi.toString())
    }

    fun handleCargaLista(){
        _state.value = _state.value?.copy(politicos = cargarPoliticosUseCase.cargarLista())
    }

    fun handleAdd(p: Politico){
        try {
            addPoliticoUseCase.addPolitico(p)
            _state.value = _state.value?.copy(politicos = cargarPoliticosUseCase.cargarLista())
        } catch (e: Exception) {
            _state.value = _state.value?.copy(error = e.message)
        }
    }

    fun handleDelete(p: Politico) {
        try {
            delPoliticoUseCase.delPolitico(p)
            _state.value = _state.value?.copy(politicos = cargarPoliticosUseCase.cargarLista())
        } catch (e: Exception) {
            _state.value = _state.value?.copy(error = e.message)
        }
    }

    fun handleLimpiarError(){
        _state.value = _state.value?.copy(error = null)
    }

    class MainViewModelFactory(
        private val damePresidenteUseCase: DamePresidenteUseCase,
        val cargarPoliticiosUseCase: CargarPoliticosUseCase,
        val addPoliticoUseCase: AddPoliticoUseCase,
        val delPoliticoUseCase: DelPoliticoUseCase,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return MainViewModel(
                    damePresidenteUseCase,
                    cargarPoliticiosUseCase,
                    addPoliticoUseCase,
                    delPoliticoUseCase,
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }

    }
}
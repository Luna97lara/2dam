package com.example.gatitosrv.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gatitosrv.domain.model.Gatito
import com.example.gatitosrv.domain.useCases.AddGatitoUseCase
import com.example.gatitosrv.domain.useCases.DameFavoritoUseCase
import com.example.gatitosrv.domain.useCases.DelGatitoUseCase
import com.example.gatitosrv.domain.useCases.ListaGatitosUseCase

class MainViewModel (
    private val dameFavoritoUseCase: DameFavoritoUseCase,
    private val addGatitoUseCase: AddGatitoUseCase,
    private val delGatitoUseCase: DelGatitoUseCase,
    private val listaGatitosUseCase: ListaGatitosUseCase
) : ViewModel() {

    private val _state = MutableLiveData<MainState>(MainState("Inicio", listOf()))

    val state: LiveData<MainState> = _state

    init {
        handleCargarLista()
    }

    fun handleDameFavorito() {

        var fav = dameFavoritoUseCase.dameFav()

        _state.value = _state.value?.copy(favorito = fav.toString())

    }

    fun handleCargarLista() {

        _state.value = _state.value?.copy(gatitos = listaGatitosUseCase.cargarLista())

    }

    fun handleAdd(g: Gatito) {

        try {
            addGatitoUseCase.addGatito(g)
            _state.value = _state.value?.copy(gatitos = listaGatitosUseCase.cargarLista())
        } catch (e: Exception) {
            _state.value = _state.value?.copy(error = e.message)
        }

    }

    fun handleDelete(g: Gatito) {
        try {
            delGatitoUseCase.delGatito(g)
            _state.value = _state.value?.copy(gatitos = listaGatitosUseCase.cargarLista())
        } catch (e: Exception) {
            _state.value = _state.value?.copy(error = e.message)
        }
    }

    fun handleLimpiarError(){
        _state.value = _state.value?.copy(error = null)
    }

}

class MainViewModelFactory(
    private val dameFavoritoUseCase: DameFavoritoUseCase,
    val listaGatitosUseCase: ListaGatitosUseCase,
    val addGatitoUseCase: AddGatitoUseCase,
    val delGatitoUseCase: DelGatitoUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return MainViewModel(
                dameFavoritoUseCase,
                addGatitoUseCase,
                delGatitoUseCase,
                listaGatitosUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
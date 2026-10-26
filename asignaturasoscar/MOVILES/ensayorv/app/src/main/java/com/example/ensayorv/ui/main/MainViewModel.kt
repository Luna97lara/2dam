package com.example.ensayorv.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ensayorv.domain.model.Gatito
import com.example.ensayorv.domain.useCases.AddGatitoUseCase
import com.example.ensayorv.domain.useCases.DameFavoritoUseCase
import com.example.ensayorv.domain.useCases.DameListaUseCase
import com.example.ensayorv.domain.useCases.DelGatitoUseCase

class MainViewModel(
    private val dameFavoritoUseCase: DameFavoritoUseCase,
    private val dameListaUseCase: DameListaUseCase,
    private val addGatitoUseCase: AddGatitoUseCase,
    private val delGatitoUseCase: DelGatitoUseCase
) : ViewModel() {

    private val _state = MutableLiveData<MainState>(MainState("Inicio", listOf()))

    val state : LiveData<MainState> = _state

    init {

    }

    fun handleDameFavorito() {

        var fav = dameFavoritoUseCase.dameFavorito()

        _state.value = _state.value?.copy(favorito = fav.toString())

    }

    fun handleDameLista() {

        _state.value = _state.value?.copy(gatitos = dameListaUseCase.dameLista())

    }

    fun handleAdd(g: Gatito) {

        try {
            addGatitoUseCase.addGatito(g)
            _state.value = _state.value?.copy(gatitos = dameListaUseCase.dameLista())
        }
        catch (e: Exception){
            _state.value = _state.value?.copy(error = e.message)
        }
    }

    fun handleDelete(g: Gatito) {

        try{
            delGatitoUseCase.delGatito(g)
            _state.value = _state.value?.copy(gatitos = dameListaUseCase.dameLista())
        }
        catch (e: Exception) {
            _state.value = _state.value?.copy(error = e.message)
        }
    }

    fun handleLimpiarError() {
        _state.value = _state.value?.copy(error = null)
    }

}

class MainViewModelFactory(
    private val dameFavoritoUseCase: DameFavoritoUseCase,
    val dameListaUseCase: DameListaUseCase,
    val addGatitoUseCase: AddGatitoUseCase,
    val delGatitoUseCase: DelGatitoUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return MainViewModel(
                dameFavoritoUseCase,
                dameListaUseCase,
                addGatitoUseCase,
                delGatitoUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

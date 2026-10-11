package com.example.ejemploxml.domain.useCases

import com.example.ejemploxml.data.Politicos

class CargarPoliticosUseCase {

    fun cargarLista() = Politicos.dameLista()

}
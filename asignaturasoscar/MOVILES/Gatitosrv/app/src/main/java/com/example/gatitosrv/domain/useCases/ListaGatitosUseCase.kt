package com.example.gatitosrv.domain.useCases

import com.example.gatitosrv.data.Gatitos

class ListaGatitosUseCase {

    fun cargarLista() = Gatitos.dameLista()

}
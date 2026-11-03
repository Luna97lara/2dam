package com.example.gatitosrv.domain.useCases

import com.example.gatitosrv.data.Gatitos
import com.example.gatitosrv.domain.model.Gatito

class AddGatitoUseCase {

    fun addGatito(g: Gatito) = Gatitos.add(g)

}
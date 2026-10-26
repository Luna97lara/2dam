package com.example.ensayorv.domain.useCases

import com.example.ensayorv.data.Gatitos
import com.example.ensayorv.domain.model.Gatito

class AddGatitoUseCase {

    fun addGatito(g: Gatito) = Gatitos.add(g)

}
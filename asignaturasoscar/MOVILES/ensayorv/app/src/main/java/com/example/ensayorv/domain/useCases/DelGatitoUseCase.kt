package com.example.ensayorv.domain.useCases

import com.example.ensayorv.data.Gatitos
import com.example.ensayorv.domain.model.Gatito

class DelGatitoUseCase {

    fun delGatito(g: Gatito) = Gatitos.del(g)

}
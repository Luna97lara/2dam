package com.example.gatitosrv.domain.useCases

import com.example.gatitosrv.data.Gatitos
import com.example.gatitosrv.domain.model.Gatito

class DelGatitoUseCase {

    fun delGatito(g: Gatito) = Gatitos.del(g)

}
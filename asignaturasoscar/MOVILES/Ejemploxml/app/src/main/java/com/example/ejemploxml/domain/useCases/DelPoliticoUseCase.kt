package com.example.ejemploxml.domain.useCases

import com.example.ejemploxml.data.Politicos
import com.example.ejemploxml.domain.model.Politico

class DelPoliticoUseCase {

    fun delPolitico(p: Politico) = Politicos.del(p)
}
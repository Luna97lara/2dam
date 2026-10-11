package com.example.ejemploxml.domain.useCases

import com.example.ejemploxml.data.Politicos
import com.example.ejemploxml.domain.model.Politico

class AddPoliticoUseCase {
    fun addPolitico(p: Politico) = Politicos.add(p)

}
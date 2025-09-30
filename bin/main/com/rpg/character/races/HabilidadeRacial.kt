package com.rpg.character.races

interface HabilidadeRacial {
    fun getNome(): String
    fun getDescricao(): String
    fun aplicarEfeito(personagem: Any): Any
}

enum class Alinhamento {
    LEAL_E_BOM,
    NEUTRO_E_BOM,
    CAOTICO_E_BOM,
    LEAL_E_NEUTRO,
    NEUTRO,
    CAOTICO_E_NEUTRO,
    LEAL_E_MAL,
    NEUTRO_E_MAL,
    CAOTICO_E_MAL
}

abstract class HabilidadeRacialBase : HabilidadeRacial {
    abstract override fun getNome(): String
    abstract override fun getDescricao(): String
    
    override fun aplicarEfeito(personagem: Any): Any {
        return personagem
    }
}


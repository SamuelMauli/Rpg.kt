package com.rpg.character.classes

interface HabilidadeDeClasse {
    fun getNome(): String
    fun getDescricao(): String
    fun getNivelRequerido(): Int
    fun aplicarEfeito(personagem: Any): Any
}

abstract class HabilidadeDeClasseBase : HabilidadeDeClasse {
    abstract override fun getNome(): String
    abstract override fun getDescricao(): String
    abstract override fun getNivelRequerido(): Int
    
    override fun aplicarEfeito(personagem: Any): Any {
        return personagem
    }
}

enum class TipoArma {
    PEQUENA,
    MEDIA,
    GRANDE
}

enum class TipoArmadura {
    LEVE,
    MEDIA,
    PESADA
}

enum class TipoItem {
    ARMA,
    ARMADURA,
    ESCUDO,
    ITEM_MAGICO,
    CAJADO,
    VARINHA,
    PERGAMINHO
}

data class ProgressaoExperiencia(
    val nivel: Int,
    val xpNecessario: Int,
    val xpEspecialista: Int
)

data class ProgressaoAtributos(
    val nivel: Int,
    val pontosDeVida: String,
    val baseAtaque: Int,
    val jogadaProtecao: Int
)


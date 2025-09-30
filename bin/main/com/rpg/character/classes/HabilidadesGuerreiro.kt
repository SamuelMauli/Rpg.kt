package com.rpg.character.classes

import kotlin.random.Random

class Aparar : HabilidadeDeClasseBase() {
    override fun getNome(): String = "Aparar"
    
    override fun getDescricao(): String = 
        "Pode sacrificar escudo ou arma para absorver todo o dano de um ataque físico"
    
    override fun getNivelRequerido(): Int = 1
    
    fun executarAparar(danoRecebido: Int, itemUsado: String, isMagico: Boolean, bonusItem: Int): ResultadoAparar {
        if (isMagico) {
            val chanceDano = Random.nextInt(1, 7)
            if (chanceDano <= 2) {
                val novoBonusItem = maxOf(0, bonusItem - 1)
                return if (novoBonusItem == 0) {
                    ResultadoAparar(danoAbsorvido = danoRecebido, itemDestruido = true, novoBonus = 0)
                } else {
                    ResultadoAparar(danoAbsorvido = danoRecebido, itemDanificado = true, novoBonus = novoBonusItem)
                }
            }
        }
        
        return ResultadoAparar(danoAbsorvido = danoRecebido, itemDestruido = !isMagico)
    }
    
    data class ResultadoAparar(
        val danoAbsorvido: Int,
        val itemDestruido: Boolean = false,
        val itemDanificado: Boolean = false,
        val novoBonus: Int = 0
    )
}

class MaestriaEmArma : HabilidadeDeClasseBase() {
    override fun getNome(): String = "Maestria em Arma"
    
    override fun getDescricao(): String = 
        "Torna-se mestre em armas específicas, recebendo bônus de dano que evolui com o nível"
    
    override fun getNivelRequerido(): Int = 1
    
    fun getBonusDano(nivel: Int, numeroArmasComMaestria: Int): Int {
        return when {
            nivel >= 10 -> 3 // Todas as armas do grupo
            nivel >= 3 -> 2  // Duas armas específicas
            else -> 1        // Uma arma específica
        }
    }
    
    fun getNumeroArmasComMaestria(nivel: Int): Int {
        return when {
            nivel >= 10 -> Int.MAX_VALUE // Todas as armas do grupo
            nivel >= 3 -> 2
            else -> 1
        }
    }
    
    fun getGruposArmasDisponiveis(): List<String> {
        return listOf("cortantes", "perfurantes", "impactantes", "disparos", "hastes", "arremesso")
    }
}

class AtaqueExtra : HabilidadeDeClasseBase() {
    override fun getNome(): String = "Ataque Extra"
    
    override fun getDescricao(): String = 
        "Adquire um segundo ataque com arma na qual possui maestria"
    
    override fun getNivelRequerido(): Int = 6
    
    fun podeUsarAtaqueExtra(armaUsada: String, armasComMaestria: List<String>): Boolean {
        return armasComMaestria.contains(armaUsada)
    }
    
    fun executarAtaqueExtra(baseAtaque: Int): Int {
        return baseAtaque // Usa a mesma BA do primeiro ataque
    }
}


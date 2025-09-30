package com.rpg.character.races

import kotlin.random.Random

class Mineradores : HabilidadeRacialBase() {
    override fun getNome(): String = "Mineradores"
    
    override fun getDescricao(): String = 
        "Detectam anomalias em pedras, armadilhas em cavernas ou fossos escondidos " +
        "(1 em 1d6 automaticamente, 1-2 em 1d6 quando procurando)"
    
    fun detectarAnomaliaAutomatica(): Boolean {
        return Random.nextInt(1, 7) == 1
    }
    
    fun detectarAnomaliaProcurando(): Boolean {
        return Random.nextInt(1, 7) <= 2
    }
}

class Vigoroso : HabilidadeRacialBase() {
    override fun getNome(): String = "Vigoroso"
    
    override fun getDescricao(): String = 
        "Muito resistentes a efeitos corporais, recebem +1 em Jogadas de Proteção contra Constituição"
    
    fun getBonusJPC(): Int = 1
}

class RestricaoArmasGrandes : HabilidadeRacialBase() {
    override fun getNome(): String = "Restrição de Armas Grandes"
    
    override fun getDescricao(): String = 
        "Podem usar apenas armas médias e pequenas. Armas grandes anãs são consideradas médias para eles"
    
    fun podeUsarArma(tamanhoArma: String): Boolean {
        return when (tamanhoArma.lowercase()) {
            "pequena", "média" -> true
            "grande" -> false
            "grande_ana" -> true // Arma grande forjada por anões
            else -> false
        }
    }
}

class Inimigos : HabilidadeRacialBase() {
    override fun getNome(): String = "Inimigos Naturais"
    
    override fun getDescricao(): String = 
        "Inimigos naturais de orcs, ogros e hobgoblins. Ataques contra essas criaturas são considerados fáceis"
    
    private val inimigosNaturais = listOf("orc", "ogro", "hobgoblin")
    
    fun isInimigoNatural(tipoMonstro: String): Boolean {
        return inimigosNaturais.contains(tipoMonstro.lowercase())
    }
    
    fun getBonusAtaqueContraInimigos(): Int = 2 // Ataque fácil = +2
}


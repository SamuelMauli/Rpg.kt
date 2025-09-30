package com.rpg.character.races

import kotlin.random.Random

class Furtivos : HabilidadeRacialBase() {
    override fun getNome(): String = "Furtivos"
    
    override fun getDescricao(): String = 
        "Especialistas em se esconder e passar despercebidos (1-2 em 1d6). " +
        "Ladrões halflings recebem +1 no talento Furtividade"
    
    fun tentarSeEsconder(): Boolean {
        return Random.nextInt(1, 7) <= 2
    }
    
    fun getBonusFurtividadeLadrao(): Int = 1
}

class Destemidos : HabilidadeRacialBase() {
    override fun getNome(): String = "Destemidos"
    
    override fun getDescricao(): String = 
        "Muito resistentes a efeitos que afetem força de vontade, recebem +1 em JPS"
    
    fun getBonusJPS(): Int = 1
}

class BonsDeMira : HabilidadeRacialBase() {
    override fun getNome(): String = "Bons de Mira"
    
    override fun getDescricao(): String = 
        "Tradição em jogos de arremesso torna ataques à distância com armas de arremesso fáceis"
    
    fun getBonusAtaqueArremesso(): Int = 2 // Ataque fácil = +2
}

class Pequenos : HabilidadeRacialBase() {
    override fun getNome(): String = "Pequenos"
    
    override fun getDescricao(): String = 
        "Baixa estatura e agilidade tornam ataques de criaturas grandes ou maiores difíceis"
    
    fun isAtaqueDificilContraHalfling(tamanhoInimigo: String): Boolean {
        return when (tamanhoInimigo.lowercase()) {
            "grande", "enorme", "colossal" -> true
            else -> false
        }
    }
    
    fun getPenalidadeInimigoGrande(): Int = -2 // Ataque difícil = -2
}

class RestricoesHalfling : HabilidadeRacialBase() {
    override fun getNome(): String = "Restrições de Equipamento"
    
    override fun getDescricao(): String = 
        "Usam apenas armaduras de couro (exceto se feitas especialmente). " +
        "Armas médias são usadas como duas mãos. Não podem usar armas grandes"
    
    fun podeUsarArmadura(tipoArmadura: String): Boolean {
        return when (tipoArmadura.lowercase()) {
            "couro", "couro_batido" -> true
            "cota_de_malha", "brunea", "loriga", "cota_de_placas", "armadura_de_placas" -> false
            "especial_halfling" -> true // Armadura feita especialmente
            else -> false
        }
    }
    
    fun podeUsarArma(tamanhoArma: String): Boolean {
        return when (tamanhoArma.lowercase()) {
            "pequena" -> true
            "média" -> true // Mas usada como duas mãos
            "grande" -> false
            else -> false
        }
    }
    
    fun isArmaDuasMaos(tamanhoArma: String): Boolean {
        return tamanhoArma.lowercase() == "média"
    }
}


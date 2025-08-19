package com.rpg.character.classes

import kotlin.random.Random

class AtaqueFurtivo : HabilidadeDeClasseBase() {
    override fun getNome(): String = "Ataque Furtivo"
    
    override fun getDescricao(): String = 
        "Ataque após aproximação furtiva causa dano multiplicado (x2 no 1º, x3 no 6º, x4 no 10º nível)"
    
    override fun getNivelRequerido(): Int = 1
    
    fun getMultiplicadorDano(nivel: Int): Int {
        return when {
            nivel >= 10 -> 4
            nivel >= 6 -> 3
            else -> 2
        }
    }
    
    fun isAtaqueMuitoFacil(): Boolean = true // Ataque furtivo é sempre muito fácil
}

class OuvirRuidos : HabilidadeDeClasseBase() {
    override fun getNome(): String = "Ouvir Ruídos"
    
    override fun getDescricao(): String = 
        "Consegue ouvir ruídos como conversas ou monstros se aproximando em ambiente silencioso"
    
    override fun getNivelRequerido(): Int = 1
    
    fun getChanceOuvirRuidos(nivel: Int): Int {
        return when {
            nivel >= 10 -> 5 // 1-5 em 1d6
            nivel >= 6 -> 4  // 1-4 em 1d6
            nivel >= 3 -> 3  // 1-3 em 1d6
            else -> 2        // 1-2 em 1d6
        }
    }
    
    fun tentarOuvirRuidos(nivel: Int): Boolean {
        val chance = getChanceOuvirRuidos(nivel)
        return Random.nextInt(1, 7) <= chance
    }
}

class TalentosLadrao : HabilidadeDeClasseBase() {
    override fun getNome(): String = "Talentos de Ladrão"
    
    override fun getDescricao(): String = 
        "Cinco talentos especializados: Armadilha, Arrombar, Escalar, Furtividade, Punga"
    
    override fun getNivelRequerido(): Int = 1
    
    data class Talentos(
        var armadilha: Int = 2,
        var arrombar: Int = 2,
        var escalar: Int = 2,
        var furtividade: Int = 2,
        var punga: Int = 2
    ) {
        fun getTalento(nome: String): Int {
            return when (nome.lowercase()) {
                "armadilha" -> armadilha
                "arrombar" -> arrombar
                "escalar" -> escalar
                "furtividade" -> furtividade
                "punga" -> punga
                else -> 0
            }
        }
        
        fun setTalento(nome: String, valor: Int) {
            when (nome.lowercase()) {
                "armadilha" -> armadilha = valor
                "arrombar" -> arrombar = valor
                "escalar" -> escalar = valor
                "furtividade" -> furtividade = valor
                "punga" -> punga = valor
            }
        }
    }
    
    fun getPontosDistribuicao(nivel: Int, modificadorDestreza: Int): Int {
        val pontosBase = when {
            nivel >= 10 -> 2
            nivel >= 6 -> 2
            nivel >= 3 -> 2
            else -> 2
        }
        
        val pontosIniciais = if (nivel == 1) 2 + modificadorDestreza else 0
        return pontosBase + pontosIniciais
    }
    
    fun testarTalento(valorTalento: Int): Boolean {
        return Random.nextInt(1, 7) <= valorTalento
    }
    
    fun getTempoAcao(talento: String, valorTalento: Int): String {
        return when (valorTalento) {
            5 -> "1 turno"
            4 -> "1d3 turnos"
            3 -> "1d4 turnos"
            2 -> "1d6 turnos"
            else -> "1d6 turnos"
        }
    }
}


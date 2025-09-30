package com.rpg.character.attributes

import kotlin.random.Random

interface MetodoRolagemAtributos {
    fun rolarAtributos(): List<Int>
    fun getNome(): String
    fun getDescricao(): String
}

class RolagemClassica : MetodoRolagemAtributos {
    override fun rolarAtributos(): List<Int> {
        return (1..6).map { rolar3d6() }
    }
    
    override fun getNome(): String = "Clássico"
    
    override fun getDescricao(): String = 
        "Role 3d6 seis vezes na ordem: Força, Destreza, Constituição, Inteligência, Sabedoria, Carisma"
    
    private fun rolar3d6(): Int {
        return (1..3).sumOf { Random.nextInt(1, 7) }
    }
}

class RolagemAventureiro : MetodoRolagemAtributos {
    override fun rolarAtributos(): List<Int> {
        return (1..6).map { rolar3d6() }
    }
    
    override fun getNome(): String = "Aventureiro"
    
    override fun getDescricao(): String = 
        "Role 3d6 seis vezes e distribua os resultados como desejar nos atributos"
    
    private fun rolar3d6(): Int {
        return (1..3).sumOf { Random.nextInt(1, 7) }
    }
}

class RolagemHeroica : MetodoRolagemAtributos {
    override fun rolarAtributos(): List<Int> {
        return (1..6).map { rolar4d6DescarteMenor() }
    }
    
    override fun getNome(): String = "Heróico"
    
    override fun getDescricao(): String = 
        "Role 4d6 eliminando o menor dado seis vezes e distribua os resultados como desejar"
    
    private fun rolar4d6DescarteMenor(): Int {
        val dados = (1..4).map { Random.nextInt(1, 7) }.sorted()
        return dados.drop(1).sum()
    }
}


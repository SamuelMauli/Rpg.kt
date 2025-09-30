package com.rpg.character.races

import kotlin.random.Random

class PercepcaoNatural : HabilidadeRacialBase() {
    override fun getNome(): String = "Percepção Natural"
    
    override fun getDescricao(): String = 
        "Detectam portas secretas automaticamente (1 em 1d6) ao passar a até 6 metros, " +
        "ou 1-2 em 1d6 quando procurando ativamente"
    
    fun detectarPortaSecretaAutomatica(): Boolean {
        return Random.nextInt(1, 7) == 1
    }
    
    fun detectarPortaSecretaProcurando(): Boolean {
        return Random.nextInt(1, 7) <= 2
    }
}

class Graciosos : HabilidadeRacialBase() {
    override fun getNome(): String = "Graciosos"
    
    override fun getDescricao(): String = 
        "Movimentos precisos e graciosos concedem +1 em Jogadas de Proteção contra Destreza"
    
    fun getBonusJPD(): Int = 1
}

class ArmaRacialElfo : HabilidadeRacialBase() {
    override fun getNome(): String = "Maestria com Arcos"
    
    override fun getDescricao(): String = 
        "Familiaridade com arqueirismo concede +1 de dano em ataques à distância com arcos"
    
    fun getBonusDanoArco(): Int = 1
}

class ImunidadesElficas : HabilidadeRacialBase() {
    override fun getNome(): String = "Imunidades Élficas"
    
    override fun getDescricao(): String = 
        "Imunes a efeitos de sono e paralisia causada por Ghouls"
    
    fun isImuneSono(): Boolean = true
    fun isImuneParalisiaGhoul(): Boolean = true
}


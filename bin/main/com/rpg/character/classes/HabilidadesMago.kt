package com.rpg.character.classes

import kotlin.random.Random

class MagiasArcanas : HabilidadeDeClasseBase() {
    override fun getNome(): String = "Magias Arcanas"
    
    override fun getDescricao(): String = 
        "Capaz de lançar magias arcanas diariamente estudando seu grimório"
    
    override fun getNivelRequerido(): Int = 1
    
    fun getMagiasPorDia(nivel: Int): Map<Int, Int> {
        return when (nivel) {
            1 -> mapOf(1 to 1)
            2 -> mapOf(1 to 2)
            3 -> mapOf(1 to 2, 2 to 1)
            4 -> mapOf(1 to 2, 2 to 2)
            5 -> mapOf(1 to 2, 2 to 2, 3 to 1)
            6 -> mapOf(1 to 3, 2 to 2, 3 to 2)
            7 -> mapOf(1 to 3, 2 to 2, 3 to 2, 4 to 1)
            8 -> mapOf(1 to 3, 2 to 3, 3 to 2, 4 to 2)
            9 -> mapOf(1 to 3, 2 to 3, 3 to 2, 4 to 2, 5 to 1)
            10 -> mapOf(1 to 3, 2 to 3, 3 to 3, 4 to 2, 5 to 2)
            else -> emptyMap()
        }
    }
    
    fun getMagiasIniciais(): List<String> {
        return listOf(
            "Magia escolhida 1",
            "Magia escolhida 2", 
            "Magia escolhida 3",
            "Magia aleatória"
        )
    }
}

class DetectarMagias : HabilidadeDeClasseBase() {
    override fun getNome(): String = "Detectar Magias"
    
    override fun getDescricao(): String = 
        "Percebe presença de magia em área de 9m + 3m por nível, uma vez por dia por nível"
    
    override fun getNivelRequerido(): Int = 1
    
    fun getAlcance(nivel: Int): Int {
        return 9 + (3 * nivel)
    }
    
    fun getUsosporDia(nivel: Int): Int {
        return nivel
    }
    
    fun getTempoConcentracao(nivel: Int): String {
        return when {
            nivel >= 10 -> "1 rodada"
            nivel >= 6 -> "1d4 rodadas"
            else -> "1d8 rodadas"
        }
    }
    
    fun rolarTempoConcentracao(nivel: Int): Int {
        return when {
            nivel >= 10 -> 1
            nivel >= 6 -> Random.nextInt(1, 5)
            else -> Random.nextInt(1, 9)
        }
    }
}

class LerMagias : HabilidadeDeClasseBase() {
    override fun getNome(): String = "Ler Magias"
    
    override fun getDescricao(): String = 
        "Decifra e lê inscrições mágicas, uma vez por dia por nível"
    
    override fun getNivelRequerido(): Int = 1
    
    fun getUsosporDia(nivel: Int): Int {
        return nivel
    }
    
    fun podeIdentificarMagia(inscricaoMagica: String): Boolean {
        return true // Sempre pode identificar qual magia está escrita
    }
    
    fun podeIdentificarPropriedadesItem(): Boolean {
        return false // Não identifica propriedades de itens mágicos
    }
}

class Grimorio : HabilidadeDeClasseBase() {
    override fun getNome(): String = "Grimório"
    
    override fun getDescricao(): String = 
        "Livro de magias essencial para memorizar novas magias"
    
    override fun getNivelRequerido(): Int = 1
    
    data class EstadoGrimorio(
        val magias: MutableList<String> = mutableListOf(),
        var perdido: Boolean = false // CORREÇÃO: Alterado de val para var
    )
    
    fun adicionarMagia(grimorio: EstadoGrimorio, nomeMagia: String, circulo: Int) {
        if (!grimorio.perdido) {
            grimorio.magias.add("$nomeMagia (${circulo}º círculo)")
        }
    }
    
    fun podeMemorizarMagia(grimorio: EstadoGrimorio, nomeMagia: String): Boolean {
        return !grimorio.perdido && grimorio.magias.any { it.contains(nomeMagia) }
    }
    
    fun perderGrimorio(grimorio: EstadoGrimorio) {
        grimorio.perdido = true
    }
    
    fun recuperarGrimorio(grimorio: EstadoGrimorio) {
        grimorio.perdido = false
    }
}
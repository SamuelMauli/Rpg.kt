package com.rpg.character.classes

import kotlin.random.Random

class MagiasDivinas : HabilidadeDeClasseBase() {
    override fun getNome(): String = "Magias Divinas"
    
    override fun getDescricao(): String = 
        "Capaz de lançar magias divinas diariamente, rezando para o Panteão"
    
    override fun getNivelRequerido(): Int = 1
    
    fun getMagiasPorDia(nivel: Int): Map<Int, Int> {
        return when (nivel) {
            1 -> mapOf(1 to 1)
            2 -> mapOf(1 to 1)
            3 -> mapOf(1 to 2, 2 to 1)
            4 -> mapOf(1 to 2, 2 to 2)
            5 -> mapOf(1 to 2, 2 to 2, 3 to 1)
            6 -> mapOf(1 to 3, 2 to 2, 3 to 1)
            7 -> mapOf(1 to 3, 2 to 2, 3 to 2, 4 to 1)
            8 -> mapOf(1 to 3, 2 to 3, 3 to 2, 4 to 1)
            9 -> mapOf(1 to 3, 2 to 3, 3 to 2, 4 to 2, 5 to 1)
            10 -> mapOf(1 to 4, 2 to 3, 3 to 3, 4 to 2, 5 to 1)
            else -> emptyMap()
        }
    }
}

class AfastarMortosVivos : HabilidadeDeClasseBase() {
    override fun getNome(): String = "Afastar Mortos-Vivos"
    
    override fun getDescricao(): String = 
        "Invoca o poder divino para fazer mortos-vivos fugirem desesperadamente"
    
    override fun getNivelRequerido(): Int = 1
    
    fun getNumeroTentativas(nivel: Int): Int {
        return when {
            nivel >= 10 -> 3
            nivel >= 3 -> 2
            else -> 1
        }
    }
    
    fun getBonusMoral(nivel: Int): Int {
        return when {
            nivel >= 10 -> 2
            nivel >= 3 -> 1
            else -> 0
        }
    }
    
    fun tentarAfastar(moralMortoVivo: Int, bonusNivel: Int): ResultadoAfastar {
        val dados = Random.nextInt(2, 13) // 2d6
        val resultado = dados + bonusNivel
        
        val sucesso = resultado > moralMortoVivo
        val reduzirAPo = sucesso && (dados == 8 || dados == 10 || dados == 12) // Dois dados iguais
        
        return ResultadoAfastar(sucesso, reduzirAPo, resultado)
    }
    
    data class ResultadoAfastar(
        val sucesso: Boolean,
        val reduzirAPo: Boolean,
        val valorRolado: Int
    )
}

class CuraMilagrosa : HabilidadeDeClasseBase() {
    override fun getNome(): String = "Cura Milagrosa"
    
    override fun getDescricao(): String = 
        "Pode trocar magia memorizada por Curar Ferimentos (evolui com o nível)"
    
    override fun getNivelRequerido(): Int = 1
    
    fun getTipoCura(nivel: Int): String {
        return when {
            nivel >= 10 -> "Curar Ferimentos 5º círculo (3d8 PV)"
            nivel >= 6 -> "Curar Ferimentos 3º círculo (2d8 PV)"
            else -> "Curar Ferimentos 1º círculo (1d8 PV)"
        }
    }
    
    fun getCuraMinima(nivel: Int): Int {
        return when {
            nivel >= 10 -> 3
            nivel >= 6 -> 2
            else -> 1
        }
    }
    
    fun getCuraMaxima(nivel: Int): Int {
        return when {
            nivel >= 10 -> 24 // 3d8
            nivel >= 6 -> 16  // 2d8
            else -> 8         // 1d8
        }
    }
    
    fun rolarCura(nivel: Int): Int {
        val numDados = when {
            nivel >= 10 -> 3
            nivel >= 6 -> 2
            else -> 1
        }
        
        return (1..numDados).sumOf { Random.nextInt(1, 9) }
    }
}


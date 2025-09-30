package com.rpg.core.factories

import kotlin.random.Random

data class Monstro(
    val nome: String,
    val tipo: String,
    val dadosDeVida: Int,
    var pontosDeVida: Int,
    val classeArmadura: Int,
    val baseAtaque: Int,
    val dano: String,
    val movimento: Int,
    val moral: Int,
    val tesouro: String,
    val habilidadesEspeciais: List<String> = emptyList(),
    val experiencia: Int,
    val tamanho: String = "médio"
) {
    fun estaMorto(): Boolean = pontosDeVida <= 0
    
    fun receberDano(dano: Int) {
        pontosDeVida = maxOf(0, pontosDeVida - dano)
    }
    
    fun rolarDano(): Int {
        return when (dano) {
            "1d4" -> Random.nextInt(1, 5)
            "1d6" -> Random.nextInt(1, 7)
            "1d8" -> Random.nextInt(1, 9)
            "2d4" -> Random.nextInt(2, 9)
            "2d6" -> Random.nextInt(2, 13)
            "3d6" -> Random.nextInt(3, 19)
            else -> 1
        }
    }
    
    fun testarMoral(): Boolean {
        return Random.nextInt(1, 13) <= moral // 2d6 vs moral
    }
}

class MonstroFactory {
    
    private val bestiario = mapOf(
        // Animais
        "rato_gigante" to EstatisticasMonstro(
            nome = "Rato Gigante",
            tipo = "animal",
            dadosVida = 1,
            ca = 7,
            baseAtaque = 1,
            dano = "1d4",
            movimento = 12,
            moral = 5,
            tesouro = "nenhum",
            xp = 10
        ),
        
        // Humanoides
        "kobold" to EstatisticasMonstro(
            nome = "Kobold",
            tipo = "humanoide",
            dadosVida = 1,
            ca = 7,
            baseAtaque = 1,
            dano = "1d6",
            movimento = 6,
            moral = 6,
            tesouro = "individual",
            xp = 10
        ),
        
        "goblin" to EstatisticasMonstro(
            nome = "Goblin",
            tipo = "humanoide",
            dadosVida = 1,
            ca = 6,
            baseAtaque = 1,
            dano = "1d6",
            movimento = 6,
            moral = 7,
            tesouro = "individual",
            xp = 10
        ),
        
        "orc" to EstatisticasMonstro(
            nome = "Orc",
            tipo = "humanoide",
            dadosVida = 1,
            ca = 6,
            baseAtaque = 1,
            dano = "1d8",
            movimento = 9,
            moral = 8,
            tesouro = "individual",
            xp = 10
        ),
        
        "bugbear" to EstatisticasMonstro(
            nome = "Bugbear",
            tipo = "humanoide",
            dadosVida = 3,
            ca = 5,
            baseAtaque = 3,
            dano = "2d4",
            movimento = 9,
            moral = 9,
            tesouro = "individual",
            xp = 35,
            tamanho = "grande"
        ),
        
        // Mortos-vivos
        "esqueleto" to EstatisticasMonstro(
            nome = "Esqueleto",
            tipo = "morto-vivo",
            dadosVida = 1,
            ca = 7,
            baseAtaque = 1,
            dano = "1d6",
            movimento = 6,
            moral = 12,
            tesouro = "nenhum",
            xp = 10,
            habilidades = listOf("imune_sono", "imune_encantamento")
        ),
        
        "zumbi" to EstatisticasMonstro(
            nome = "Zumbi",
            tipo = "morto-vivo",
            dadosVida = 2,
            ca = 8,
            baseAtaque = 2,
            dano = "1d8",
            movimento = 6,
            moral = 12,
            tesouro = "nenhum",
            xp = 20,
            habilidades = listOf("imune_sono", "imune_encantamento")
        ),
        
        // Chefe da aventura
        "necromante" to EstatisticasMonstro(
            nome = "Necromante",
            tipo = "humanoide",
            dadosVida = 3,
            ca = 9,
            baseAtaque = 2,
            dano = "1d4",
            movimento = 9,
            moral = 9,
            tesouro = "magico",
            xp = 65,
            habilidades = listOf("magias_3_nivel", "afastar_vivos")
        )
    )
    
    fun criarMonstro(tipo: String, quantidade: Int = 1): List<Monstro> {
        val estatisticas = bestiario[tipo.lowercase()] 
            ?: throw IllegalArgumentException("Monstro desconhecido: $tipo")
        
        return (1..quantidade).map { criarMonstroIndividual(estatisticas) }
    }
    
    fun criarGrupoAleatorio(nivelDesafio: Int): List<Monstro> {
        val monstrosDisponiveis = when (nivelDesafio) {
            1 -> listOf("rato_gigante", "kobold")
            2 -> listOf("goblin", "esqueleto")
            3 -> listOf("orc", "zumbi")
            4 -> listOf("bugbear")
            5 -> listOf("necromante")
            else -> listOf("goblin")
        }
        
        val tipoEscolhido = monstrosDisponiveis.random()
        val quantidade = when (nivelDesafio) {
            1 -> Random.nextInt(2, 5)
            2 -> Random.nextInt(1, 4)
            3 -> Random.nextInt(1, 3)
            4 -> 1
            5 -> 1
            else -> 1
        }
        
        return criarMonstro(tipoEscolhido, quantidade)
    }
    
    fun getMonstrosDisponiveis(): List<String> {
        return bestiario.keys.toList()
    }
    
    fun getEstatisticas(tipo: String): EstatisticasMonstro? {
        return bestiario[tipo.lowercase()]
    }
    
    private fun criarMonstroIndividual(stats: EstatisticasMonstro): Monstro {
        val pontosVida = rolarPontosVida(stats.dadosVida)
        
        return Monstro(
            nome = stats.nome,
            tipo = stats.tipo,
            dadosDeVida = stats.dadosVida,
            pontosDeVida = pontosVida,
            classeArmadura = stats.ca,
            baseAtaque = stats.baseAtaque,
            dano = stats.dano,
            movimento = stats.movimento,
            moral = stats.moral,
            tesouro = stats.tesouro,
            habilidadesEspeciais = stats.habilidades,
            experiencia = stats.xp,
            tamanho = stats.tamanho
        )
    }
    
    private fun rolarPontosVida(dadosVida: Int): Int {
        return (1..dadosVida).sumOf { Random.nextInt(1, 9) } // d8 por DV
    }
}

data class EstatisticasMonstro(
    val nome: String,
    val tipo: String,
    val dadosVida: Int,
    val ca: Int,
    val baseAtaque: Int,
    val dano: String,
    val movimento: Int,
    val moral: Int,
    val tesouro: String,
    val xp: Int,
    val habilidades: List<String> = emptyList(),
    val tamanho: String = "médio"
)


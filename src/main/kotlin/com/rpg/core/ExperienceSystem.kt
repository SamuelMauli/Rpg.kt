package com.rpg.core

import com.rpg.character.Personagem
import kotlin.random.Random

class ExperienceSystem {
    
    companion object {
        // Tabela de XP necessário por nível
        private val XP_POR_NIVEL = mapOf(
            1 to 0,
            2 to 2000,
            3 to 4000,
            4 to 8000,
            5 to 16000,
            6 to 32000,
            7 to 64000,
            8 to 125000,
            9 to 250000,
            10 to 500000,
            11 to 750000,
            12 to 1000000,
            13 to 1250000,
            14 to 1500000,
            15 to 1750000,
            16 to 2000000,
            17 to 2250000,
            18 to 2500000,
            19 to 2750000,
            20 to 3000000
        )
        
        fun getXpNecessario(nivel: Int): Int {
            return XP_POR_NIVEL[nivel] ?: 3000000
        }
        
        fun getXpProximoNivel(nivel: Int): Int {
            return getXpNecessario(nivel + 1)
        }
    }
    
    fun adicionarExperiencia(personagem: Personagem, xp: Int): ResultadoExperiencia {
        val xpAnterior = personagem.experiencia
        personagem.experiencia += xp
        
        val niveisGanhos = mutableListOf<Int>()
        var pontosVidaGanhos = 0
        var pontosAtributoGanhos = 0
        
        // Verificar se subiu de nível
        while (podeSubirNivel(personagem)) {
            val nivelAnterior = personagem.nivel
            val pvAdicionais = subirNivel(personagem)
            
            niveisGanhos.add(personagem.nivel)
            pontosVidaGanhos += pvAdicionais
            
            // A cada 4 níveis, ganha 1 ponto de atributo
            if (personagem.nivel % 4 == 0) {
                pontosAtributoGanhos++
            }
        }
        
        return ResultadoExperiencia(
            xpGanho = xp,
            xpTotal = personagem.experiencia,
            xpAnterior = xpAnterior,
            niveisGanhos = niveisGanhos,
            pontosVidaGanhos = pontosVidaGanhos,
            pontosAtributoGanhos = pontosAtributoGanhos,
            nivelAtual = personagem.nivel,
            xpProximoNivel = getXpProximoNivel(personagem.nivel),
            xpRestante = getXpProximoNivel(personagem.nivel) - personagem.experiencia
        )
    }
    
    private fun podeSubirNivel(personagem: Personagem): Boolean {
        return personagem.experiencia >= getXpProximoNivel(personagem.nivel)
    }
    
    private fun subirNivel(personagem: Personagem): Int {
        personagem.nivel++
        
        // Calcular pontos de vida adicionais
        val dadoVida = personagem.classe.getDadoDeVida()
        val modificadorConstituicao = personagem.atributos.getModificadorConstituicao()
        
        val pvAdicionais = if (personagem.nivel <= 10) {
            // Até nível 10, rola o dado
            Random.nextInt(1, dadoVida + 1) + modificadorConstituicao
        } else {
            // Após nível 10, ganha valor fixo
            2 + modificadorConstituicao
        }
        
        val pvGanhos = maxOf(1, pvAdicionais) // Mínimo 1 PV por nível
        
        personagem.pontosDeVida += pvGanhos
        
        return pvGanhos
    }
    
    fun calcularXpPorMonstro(nivelMonstro: Int, nivelPersonagem: Int): Int {
        val xpBase = when (nivelMonstro) {
            1 -> 50
            2 -> 100
            3 -> 150
            4 -> 250
            5 -> 350
            6 -> 500
            7 -> 700
            8 -> 900
            9 -> 1200
            10 -> 2000
            else -> 2000 + (nivelMonstro - 10) * 500
        }
        
        // Ajustar XP baseado na diferença de nível
        val diferencaNivel = nivelMonstro - nivelPersonagem
        val multiplicador = when {
            diferencaNivel >= 5 -> 1.5
            diferencaNivel >= 3 -> 1.25
            diferencaNivel >= 1 -> 1.0
            diferencaNivel >= -1 -> 0.9
            diferencaNivel >= -3 -> 0.75
            else -> 0.5
        }
        
        return (xpBase * multiplicador).toInt()
    }
    
    fun getProgressoNivel(personagem: Personagem): ProgressoNivel {
        val xpAtual = personagem.experiencia
        val xpNivelAtual = getXpNecessario(personagem.nivel)
        val xpProximoNivel = getXpProximoNivel(personagem.nivel)
        
        val xpNecessarioNivel = xpProximoNivel - xpNivelAtual
        val xpGanhoNivel = xpAtual - xpNivelAtual
        val porcentagem = if (xpNecessarioNivel > 0) {
            (xpGanhoNivel.toDouble() / xpNecessarioNivel.toDouble() * 100).toInt()
        } else {
            100
        }
        
        return ProgressoNivel(
            nivel = personagem.nivel,
            xpAtual = xpAtual,
            xpNivelAtual = xpNivelAtual,
            xpProximoNivel = xpProximoNivel,
            xpNecessario = xpNecessarioNivel,
            xpGanho = xpGanhoNivel,
            porcentagem = porcentagem
        )
    }
    
    fun gerarBarraProgresso(progresso: ProgressoNivel, largura: Int = 30): String {
        val preenchido = (progresso.porcentagem * largura) / 100
        val vazio = largura - preenchido
        
        return buildString {
            append("[")
            repeat(preenchido) { append("█") }
            repeat(vazio) { append("░") }
            append("] ${progresso.porcentagem}%")
        }
    }
}

data class ResultadoExperiencia(
    val xpGanho: Int,
    val xpTotal: Int,
    val xpAnterior: Int,
    val niveisGanhos: List<Int>,
    val pontosVidaGanhos: Int,
    val pontosAtributoGanhos: Int,
    val nivelAtual: Int,
    val xpProximoNivel: Int,
    val xpRestante: Int
) {
    fun temNivelGanho(): Boolean = niveisGanhos.isNotEmpty()
    
    fun getMensagem(): String {
        return buildString {
            appendLine("✨ XP Ganho: +$xpGanho")
            appendLine("   XP Total: $xpTotal")
            
            if (temNivelGanho()) {
                appendLine()
                appendLine("╔═══════════════════════════════════════╗")
                appendLine("║          SUBIU DE NÍVEL!              ║")
                appendLine("╚═══════════════════════════════════════╝")
                
                niveisGanhos.forEach { nivel ->
                    appendLine("🎉 Nível $nivel alcançado!")
                }
                
                if (pontosVidaGanhos > 0) {
                    appendLine("❤️  +$pontosVidaGanhos Pontos de Vida")
                }
                
                if (pontosAtributoGanhos > 0) {
                    appendLine("⭐ +$pontosAtributoGanhos Ponto(s) de Atributo disponível(is)")
                    appendLine("   Use o menu de personagem para distribuir!")
                }
            } else {
                appendLine("   Faltam $xpRestante XP para o nível ${nivelAtual + 1}")
            }
        }
    }
}

data class ProgressoNivel(
    val nivel: Int,
    val xpAtual: Int,
    val xpNivelAtual: Int,
    val xpProximoNivel: Int,
    val xpNecessario: Int,
    val xpGanho: Int,
    val porcentagem: Int
)

class GerenciadorAtributos {
    
    private val pontosDisponiveis = mutableMapOf<String, Int>()
    
    fun adicionarPontosAtributo(personagemNome: String, pontos: Int) {
        pontosDisponiveis[personagemNome] = pontosDisponiveis.getOrDefault(personagemNome, 0) + pontos
    }
    
    fun getPontosDisponiveis(personagemNome: String): Int {
        return pontosDisponiveis.getOrDefault(personagemNome, 0)
    }
    
    fun distribuirPonto(personagem: Personagem, atributo: String): ResultadoDistribuicao {
        val pontosAtuais = getPontosDisponiveis(personagem.nome)
        
        if (pontosAtuais <= 0) {
            return ResultadoDistribuicao(false, "Você não tem pontos de atributo disponíveis!")
        }
        
        val sucesso = when (atributo.uppercase()) {
            "FORCA", "FOR", "F" -> {
                personagem.atributos.forca++
                true
            }
            "DESTREZA", "DES", "D" -> {
                personagem.atributos.destreza++
                true
            }
            "CONSTITUICAO", "CON", "C" -> {
                val pvAdicionais = personagem.atributos.getModificadorConstituicao()
                personagem.atributos.constituicao++
                val novoModificador = personagem.atributos.getModificadorConstituicao()
                if (novoModificador > pvAdicionais) {
                    personagem.pontosDeVida += (novoModificador - pvAdicionais) * personagem.nivel
                }
                true
            }
            "INTELIGENCIA", "INT", "I" -> {
                personagem.atributos.inteligencia++
                true
            }
            "SABEDORIA", "SAB", "S" -> {
                personagem.atributos.sabedoria++
                true
            }
            "CARISMA", "CAR" -> {
                personagem.atributos.carisma++
                true
            }
            else -> false
        }
        
        return if (sucesso) {
            pontosDisponiveis[personagem.nome] = pontosAtuais - 1
            ResultadoDistribuicao(true, "Ponto de atributo distribuído em $atributo!")
        } else {
            ResultadoDistribuicao(false, "Atributo inválido!")
        }
    }
    
    fun mostrarMenuDistribuicao(personagem: Personagem): String {
        val pontos = getPontosDisponiveis(personagem.nome)
        
        return buildString {
            appendLine("╔═══════════════════════════════════════╗")
            appendLine("║     DISTRIBUIÇÃO DE ATRIBUTOS         ║")
            appendLine("╚═══════════════════════════════════════╝")
            appendLine()
            appendLine("Pontos disponíveis: $pontos")
            appendLine()
            appendLine("Atributos atuais:")
            appendLine("1. Força:        ${personagem.atributos.forca} (${personagem.atributos.getModificadorForca().let { if (it >= 0) "+$it" else "$it" }})")
            appendLine("2. Destreza:     ${personagem.atributos.destreza} (${personagem.atributos.getModificadorDestreza().let { if (it >= 0) "+$it" else "$it" }})")
            appendLine("3. Constituição: ${personagem.atributos.constituicao} (${personagem.atributos.getModificadorConstituicao().let { if (it >= 0) "+$it" else "$it" }})")
            appendLine("4. Inteligência: ${personagem.atributos.inteligencia} (${personagem.atributos.getModificadorInteligencia().let { if (it >= 0) "+$it" else "$it" }})")
            appendLine("5. Sabedoria:    ${personagem.atributos.sabedoria} (${personagem.atributos.getModificadorSabedoria().let { if (it >= 0) "+$it" else "$it" }})")
            appendLine("6. Carisma:      ${personagem.atributos.carisma} (${personagem.atributos.getModificadorCarisma().let { if (it >= 0) "+$it" else "$it" }})")
            appendLine()
            appendLine("Digite o número do atributo para adicionar 1 ponto")
            appendLine("Digite 0 para voltar")
        }
    }
}

data class ResultadoDistribuicao(
    val sucesso: Boolean,
    val mensagem: String
)


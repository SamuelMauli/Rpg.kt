package com.rpg.core

import com.rpg.character.Personagem
import com.rpg.core.factories.Monstro
import kotlin.random.Random

enum class FonteExperiencia {
    COMBATE,
    TESOURO,
    MISSAO,
    EXPLORACAO,
    ROLEPLAY,
    BONUS
}

data class GanhoExperiencia(
    val fonte: FonteExperiencia,
    val quantidade: Int,
    val descricao: String
)

class GerenciadorExperiencia {
    
    fun calcularXpCombate(monstros: List<Monstro>): Int {
        return monstros.sumOf { it.experiencia }
    }
    
    fun calcularXpTesouro(valorOuro: Int): Int {
        return valorOuro // 1 PO = 1 XP
    }
    
    fun distribuirExperiencia(
        personagens: List<Personagem>,
        ganhosXp: List<GanhoExperiencia>
    ): Map<String, List<GanhoExperiencia>> {
        val totalXp = ganhosXp.sumOf { it.quantidade }
        val xpPorPersonagem = totalXp / personagens.size
        
        val distribuicao = mutableMapOf<String, List<GanhoExperiencia>>()
        
        personagens.forEach { personagem ->
            val ganhosIndividuais = ganhosXp.map { ganho ->
                val xpIndividual = ganho.quantidade / personagens.size
                ganho.copy(quantidade = xpIndividual)
            }
            
            distribuicao[personagem.nome] = ganhosIndividuais
            personagem.experiencia += xpPorPersonagem
        }
        
        return distribuicao
    }
    
    fun verificarSubidaNivel(personagem: Personagem): Boolean {
        val xpNecessario = personagem.classe.getXpNecessario(personagem.nivel + 1)
        return personagem.experiencia >= xpNecessario
    }
    
    fun subirNivel(personagem: Personagem): String {
        if (!verificarSubidaNivel(personagem)) {
            return "${personagem.nome} não possui experiência suficiente para subir de nível."
        }
        
        val nivelAnterior = personagem.nivel
        personagem.nivel++
        
        // Rolar pontos de vida adicionais
        val dadoVida = personagem.classe.getDadoDeVida()
        val modificadorConstituicao = personagem.atributos.getModificadorConstituicao()
        val pontosVidaAdicionais = maxOf(1, Random.nextInt(1, dadoVida + 1) + modificadorConstituicao)
        
        personagem.pontosDeVida += pontosVidaAdicionais
        // Não atualizar pontosDeVidaMaximos aqui pois é val - seria necessário refatorar
        
        return buildString {
            appendLine("🎉 ${personagem.nome} subiu para o nível ${personagem.nivel}!")
            appendLine("Ganhou $pontosVidaAdicionais pontos de vida.")
            appendLine("Pontos de vida atuais: ${personagem.pontosDeVida}")
            
            // Verificar novas habilidades
            val novasHabilidades = personagem.classe.getHabilidades()
                .filter { it.getNivelRequerido() == personagem.nivel }
            
            if (novasHabilidades.isNotEmpty()) {
                appendLine()
                appendLine("Novas habilidades adquiridas:")
                novasHabilidades.forEach { habilidade ->
                    appendLine("• ${habilidade.getNome()}: ${habilidade.getDescricao()}")
                }
            }
            
            // Verificar melhorias específicas da classe
            when (personagem.classe.getNome().lowercase()) {
                "ladrao", "ladrão" -> {
                    if (personagem.nivel in listOf(3, 6, 10)) {
                        appendLine()
                        appendLine("Você recebe 2 pontos para distribuir em seus talentos de ladrão!")
                    }
                }
                "guerreiro" -> {
                    if (personagem.nivel == 3) {
                        appendLine()
                        appendLine("Sua maestria em armas evolui! Agora você pode ter maestria em 2 armas.")
                    }
                    if (personagem.nivel == 6) {
                        appendLine()
                        appendLine("Você adquire um ataque extra com armas nas quais possui maestria!")
                    }
                    if (personagem.nivel == 10) {
                        appendLine()
                        appendLine("Sua maestria se estende a todo um grupo de armas!")
                    }
                }
                "clerigo", "clérigo" -> {
                    if (personagem.nivel in listOf(3, 6, 10)) {
                        appendLine()
                        appendLine("Sua habilidade de Afastar Mortos-Vivos melhora!")
                    }
                    if (personagem.nivel in listOf(6, 10)) {
                        appendLine()
                        appendLine("Sua Cura Milagrosa evolui para um círculo superior!")
                    }
                }
                "mago" -> {
                    val novoCirculo = when (personagem.nivel) {
                        3 -> 2
                        5 -> 3
                        7 -> 4
                        9 -> 5
                        else -> null
                    }
                    if (novoCirculo != null) {
                        appendLine()
                        appendLine("Você agora pode aprender magias de ${novoCirculo}º círculo!")
                    }
                }
            }
            
            val proximoNivel = personagem.classe.getXpNecessario(personagem.nivel + 1)
            appendLine()
            appendLine("XP para próximo nível: ${personagem.experiencia}/$proximoNivel")
        }
    }
    
    fun calcularBonusXpRoleplay(
        personagem: Personagem,
        interpretacao: Int, // 1-5
        criatividade: Int,  // 1-5
        trabalhoEquipe: Int // 1-5
    ): Int {
        val bonusBase = (interpretacao + criatividade + trabalhoEquipe) * 10
        val bonusCarisma = personagem.atributos.getModificadorCarisma() * 5
        return maxOf(0, bonusBase + bonusCarisma)
    }
    
    fun calcularBonusXpExploracao(
        areasExploradas: Int,
        segredosDescobertos: Int,
        armadilhasEvitadas: Int
    ): Int {
        return (areasExploradas * 10) + (segredosDescobertos * 25) + (armadilhasEvitadas * 15)
    }
    
    fun gerarRelatorioXp(
        personagem: Personagem,
        ganhosRecentes: List<GanhoExperiencia>
    ): String {
        return buildString {
            appendLine("=== RELATÓRIO DE EXPERIÊNCIA ===")
            appendLine("Personagem: ${personagem.nome}")
            appendLine("Nível Atual: ${personagem.nivel}")
            appendLine("XP Atual: ${personagem.experiencia}")
            
            val proximoNivel = personagem.classe.getXpNecessario(personagem.nivel + 1)
            val xpRestante = proximoNivel - personagem.experiencia
            appendLine("XP para próximo nível: $xpRestante")
            
            val porcentagem = (personagem.experiencia.toDouble() / proximoNivel * 100).toInt()
            appendLine("Progresso: $porcentagem%")
            
            if (ganhosRecentes.isNotEmpty()) {
                appendLine()
                appendLine("=== GANHOS RECENTES ===")
                ganhosRecentes.forEach { ganho ->
                    appendLine("${ganho.fonte}: +${ganho.quantidade} XP - ${ganho.descricao}")
                }
                
                val totalGanho = ganhosRecentes.sumOf { it.quantidade }
                appendLine()
                appendLine("Total ganho: +$totalGanho XP")
            }
        }
    }
    
    fun criarTabelaProgressao(personagem: Personagem): String {
        return buildString {
            appendLine("=== TABELA DE PROGRESSÃO ===")
            appendLine("Classe: ${personagem.classe.getNome()}")
            appendLine()
            
            (1..10).forEach { nivel ->
                val xpNecessario = personagem.classe.getXpNecessario(nivel)
                val baseAtaque = personagem.classe.getBaseAtaque(nivel)
                val jogadaProtecao = personagem.classe.getJogadaProtecao(nivel)
                val pontosVida = personagem.classe.getPontosDeVidaAdicionais(nivel)
                
                val marcador = if (nivel == personagem.nivel) " ← ATUAL" else ""
                
                appendLine("Nível $nivel: $xpNecessario XP | BA: $baseAtaque | JP: $jogadaProtecao | PV: $pontosVida$marcador")
            }
        }
    }
    
    fun simularCombateXp(
        personagens: List<Personagem>,
        monstros: List<Monstro>,
        tesouroEncontrado: Int = 0
    ): String {
        val xpCombate = calcularXpCombate(monstros)
        val xpTesouro = calcularXpTesouro(tesouroEncontrado)
        
        val ganhosXp = mutableListOf<GanhoExperiencia>()
        
        if (xpCombate > 0) {
            ganhosXp.add(GanhoExperiencia(
                FonteExperiencia.COMBATE,
                xpCombate,
                "Derrotar ${monstros.size} monstro(s)"
            ))
        }
        
        if (xpTesouro > 0) {
            ganhosXp.add(GanhoExperiencia(
                FonteExperiencia.TESOURO,
                xpTesouro,
                "Tesouro encontrado: $tesouroEncontrado PO"
            ))
        }
        
        val distribuicao = distribuirExperiencia(personagens, ganhosXp)
        
        return buildString {
            appendLine("=== EXPERIÊNCIA GANHA ===")
            appendLine("XP de Combate: $xpCombate")
            if (xpTesouro > 0) appendLine("XP de Tesouro: $xpTesouro")
            
            val totalXp = ganhosXp.sumOf { it.quantidade }
            val xpPorPersonagem = totalXp / personagens.size
            appendLine("XP por personagem: $xpPorPersonagem")
            appendLine()
            
            personagens.forEach { personagem ->
                appendLine("${personagem.nome}: ${personagem.experiencia} XP total")
                if (verificarSubidaNivel(personagem)) {
                    appendLine("  ⭐ Pode subir de nível!")
                }
            }
        }
    }
}


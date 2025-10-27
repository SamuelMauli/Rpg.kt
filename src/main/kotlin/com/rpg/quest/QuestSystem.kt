package com.rpg.quest

import com.rpg.character.Personagem

/**
 * Sistema de Quests (Missões)
 * Gerencia missões, objetivos e recompensas
 */
class QuestSystem {
    
    data class Quest(
        val id: Long,
        val nome: String,
        val descricao: String,
        val tipo: TipoQuest,
        val npcId: Long?,
        val nivelMinimo: Int,
        val objetivos: List<Objetivo>,
        val recompensaXP: Int,
        val recompensaOuro: Int,
        val recompensaItens: List<Long>,
        val historia: String?,
        var status: StatusQuest = StatusQuest.DISPONIVEL
    )
    
    enum class TipoQuest {
        PRINCIPAL,      // Quest da história principal
        SECUNDARIA,     // Quest opcional
        DIARIA,         // Quest que reseta diariamente
        REPETICAO       // Quest que pode ser repetida
    }
    
    enum class StatusQuest {
        DISPONIVEL,     // Disponível para aceitar
        ATIVA,          // Aceita e em progresso
        COMPLETA,       // Completada
        FALHADA,        // Falhada
        EXPIRADA        // Expirada (para diárias)
    }
    
    data class Objetivo(
        val tipo: TipoObjetivo,
        val descricao: String,
        val alvo: String,
        val quantidadeNecessaria: Int,
        var quantidadeAtual: Int = 0
    ) {
        fun isCompleto() = quantidadeAtual >= quantidadeNecessaria
        
        fun getProgresso() = "$quantidadeAtual/$quantidadeNecessaria"
    }
    
    enum class TipoObjetivo {
        MATAR,          // Matar X criaturas
        COLETAR,        // Coletar X itens
        EXPLORAR,       // Explorar localização
        FALAR,          // Falar com NPC
        ENTREGAR,       // Entregar item
        PROTEGER,       // Proteger NPC
        DERROTAR_BOSS,  // Derrotar chefe específico
        USAR_ITEM,      // Usar item específico
        ALCANÇAR_NIVEL  // Alcançar nível X
    }
    
    private val questsAtivas = mutableMapOf<Long, Quest>()
    private val questsCompletas = mutableSetOf<Long>()
    
    /**
     * Aceita uma quest
     */
    fun aceitarQuest(quest: Quest, personagem: Personagem): ResultadoQuest {
        if (personagem.nivel < quest.nivelMinimo) {
            return ResultadoQuest(
                sucesso = false,
                mensagem = "Você precisa ser nível ${quest.nivelMinimo} para aceitar esta quest."
            )
        }
        
        if (questsAtivas.containsKey(quest.id)) {
            return ResultadoQuest(
                sucesso = false,
                mensagem = "Você já está fazendo esta quest."
            )
        }
        
        quest.status = StatusQuest.ATIVA
        questsAtivas[quest.id] = quest
        
        return ResultadoQuest(
            sucesso = true,
            mensagem = "Quest '${quest.nome}' aceita!",
            quest = quest
        )
    }
    
    /**
     * Atualiza progresso de uma quest
     */
    fun atualizarProgresso(
        questId: Long,
        tipoObjetivo: TipoObjetivo,
        alvo: String,
        quantidade: Int = 1
    ): ResultadoQuest {
        val quest = questsAtivas[questId] ?: return ResultadoQuest(
            sucesso = false,
            mensagem = "Quest não encontrada."
        )
        
        var atualizado = false
        val mensagens = mutableListOf<String>()
        
        for (objetivo in quest.objetivos) {
            if (objetivo.tipo == tipoObjetivo && objetivo.alvo == alvo && !objetivo.isCompleto()) {
                objetivo.quantidadeAtual += quantidade
                if (objetivo.quantidadeAtual > objetivo.quantidadeNecessaria) {
                    objetivo.quantidadeAtual = objetivo.quantidadeNecessaria
                }
                atualizado = true
                
                if (objetivo.isCompleto()) {
                    mensagens.add("✅ Objetivo completo: ${objetivo.descricao}")
                } else {
                    mensagens.add("📊 Progresso: ${objetivo.descricao} (${objetivo.getProgresso()})")
                }
            }
        }
        
        // Verificar se todos os objetivos foram completados
        if (quest.objetivos.all { it.isCompleto() }) {
            mensagens.add("🎉 Quest '${quest.nome}' completa! Retorne ao NPC para receber sua recompensa.")
        }
        
        return ResultadoQuest(
            sucesso = atualizado,
            mensagem = mensagens.joinToString("\n"),
            quest = quest
        )
    }
    
    /**
     * Completa uma quest e concede recompensas
     */
    fun completarQuest(questId: Long, personagem: Personagem): ResultadoQuest {
        val quest = questsAtivas[questId] ?: return ResultadoQuest(
            sucesso = false,
            mensagem = "Quest não encontrada."
        )
        
        if (!quest.objetivos.all { it.isCompleto() }) {
            return ResultadoQuest(
                sucesso = false,
                mensagem = "Você ainda não completou todos os objetivos desta quest."
            )
        }
        
        // Conceder recompensas
        personagem.experiencia += quest.recompensaXP
        personagem.dinheiro += quest.recompensaOuro
        
        // Adicionar itens de recompensa
        // TODO: Implementar adição de itens
        
        // Marcar quest como completa
        quest.status = StatusQuest.COMPLETA
        questsAtivas.remove(questId)
        questsCompletas.add(questId)
        
        val mensagem = buildString {
            appendLine("🎊 Quest '${quest.nome}' completa!")
            appendLine("Recompensas:")
            appendLine("  💫 +${quest.recompensaXP} XP")
            appendLine("  💰 +${quest.recompensaOuro} ouro")
            if (quest.recompensaItens.isNotEmpty()) {
                appendLine("  📦 ${quest.recompensaItens.size} item(ns)")
            }
        }
        
        return ResultadoQuest(
            sucesso = true,
            mensagem = mensagem,
            quest = quest,
            xpGanho = quest.recompensaXP,
            ouroGanho = quest.recompensaOuro
        )
    }
    
    /**
     * Abandona uma quest
     */
    fun abandonarQuest(questId: Long): ResultadoQuest {
        val quest = questsAtivas.remove(questId) ?: return ResultadoQuest(
            sucesso = false,
            mensagem = "Quest não encontrada."
        )
        
        quest.status = StatusQuest.DISPONIVEL
        
        return ResultadoQuest(
            sucesso = true,
            mensagem = "Quest '${quest.nome}' abandonada."
        )
    }
    
    /**
     * Obtém todas as quests ativas
     */
    fun getQuestsAtivas(): List<Quest> {
        return questsAtivas.values.toList()
    }
    
    /**
     * Obtém todas as quests completas
     */
    fun getQuestsCompletas(): Set<Long> {
        return questsCompletas.toSet()
    }
    
    /**
     * Verifica se uma quest está completa
     */
    fun isQuestCompleta(questId: Long): Boolean {
        return questsCompletas.contains(questId)
    }
    
    /**
     * Obtém detalhes de uma quest
     */
    fun getQuestDetalhes(quest: Quest): String {
        return buildString {
            appendLine("╔═══════════════════════════════════════╗")
            appendLine("║  ${quest.nome.padEnd(37)}║")
            appendLine("╚═══════════════════════════════════════╝")
            appendLine()
            appendLine("Tipo: ${quest.tipo}")
            appendLine("Nível Mínimo: ${quest.nivelMinimo}")
            appendLine("Status: ${quest.status}")
            appendLine()
            appendLine("Descrição:")
            appendLine(quest.descricao)
            appendLine()
            
            if (quest.historia != null) {
                appendLine("História:")
                appendLine(quest.historia)
                appendLine()
            }
            
            appendLine("Objetivos:")
            quest.objetivos.forEachIndexed { index, objetivo ->
                val status = if (objetivo.isCompleto()) "✅" else "⬜"
                appendLine("  $status ${index + 1}. ${objetivo.descricao} (${objetivo.getProgresso()})")
            }
            appendLine()
            
            appendLine("Recompensas:")
            appendLine("  💫 ${quest.recompensaXP} XP")
            appendLine("  💰 ${quest.recompensaOuro} ouro")
            if (quest.recompensaItens.isNotEmpty()) {
                appendLine("  📦 ${quest.recompensaItens.size} item(ns) especial(is)")
            }
        }
    }
    
    data class ResultadoQuest(
        val sucesso: Boolean,
        val mensagem: String,
        val quest: Quest? = null,
        val xpGanho: Int = 0,
        val ouroGanho: Int = 0
    )
}


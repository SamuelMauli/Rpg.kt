package com.rpg.core.states

import com.rpg.character.Personagem
import com.rpg.core.factories.Monstro

enum class EstadoJogo {
    MENU_PRINCIPAL,
    CRIACAO_PERSONAGEM,
    AVENTURA,
    COMBATE,
    INVENTARIO,
    DESCANSO,
    LOJA,
    GAME_OVER,
    VITORIA,
    AVENTURA_ESPECIAL,
    COMBATE_AVENTURA
}

data class ContextoJogo(
    var personagem: Personagem? = null,
    var partidaAtiva: Boolean = false,
    var localizacaoAtual: String = "",
    var monstrosAtivos: List<Monstro> = emptyList(),
    var mensagemStatus: String = "",
    var opcoesDiponiveis: List<String> = emptyList(),
    var dadosTemporarios: MutableMap<String, Any> = mutableMapOf()
)

abstract class GameState {
    abstract fun entrar(contexto: ContextoJogo): String
    abstract fun processar(contexto: ContextoJogo, entrada: String): GameState?
    abstract fun sair(contexto: ContextoJogo): String
    abstract fun getOpcoes(): List<String>
    abstract fun getDescricao(): String
    
    protected fun limparTela(): String = "\n".repeat(50)
    
    protected fun formatarOpcoes(opcoes: List<String>): String {
        return buildString {
            appendLine()
            appendLine("Opções disponíveis:")
            opcoes.forEachIndexed { index, opcao ->
                appendLine("${index + 1}. $opcao")
            }
            appendLine()
            append("Digite sua escolha: ")
        }
    }
    
    protected fun validarEscolha(entrada: String, maxOpcoes: Int): Int? {
        return try {
            val escolha = entrada.toInt()
            if (escolha in 1..maxOpcoes) escolha else null
        } catch (e: NumberFormatException) {
            null
        }
    }
}

class GameStateManager {
    private var estadoAtual: GameState = MenuPrincipalState()
    private val contexto = ContextoJogo()
    private val historicoEstados = mutableListOf<GameState>()
    
    fun iniciar(): String {
        return estadoAtual.entrar(contexto)
    }
    
    // --- FUNÇÃO CORRIGIDA ---
    fun processarEntrada(entrada: String): String {
        val novoEstado = estadoAtual.processar(contexto, entrada.trim())
        
        if (novoEstado != null) {
            // Caso 1: O estado mudou para um NOVO estado
            if (novoEstado != estadoAtual) {
                val mensagemSaida = estadoAtual.sair(contexto)
                historicoEstados.add(estadoAtual)
                estadoAtual = novoEstado
                val mensagemEntrada = estadoAtual.entrar(contexto)
                
                return buildString {
                    if (mensagemSaida.isNotEmpty()) {
                        appendLine(mensagemSaida)
                        appendLine()
                    }
                    append(mensagemEntrada)
                }
            } else {
                // Caso 2: O estado foi processado mas não mudou (retornou 'this').
                // Apenas redesenha a tela com as informações atualizadas.
                return estadoAtual.entrar(contexto)
            }
        } else {
            // Caso 3: A entrada foi inválida (retornou 'null').
            // Redesenha a tela e adiciona uma mensagem de erro.
            return buildString {
                append(estadoAtual.entrar(contexto))
                appendLine("\nOpção inválida. Tente novamente.")
            }
        }
    }
    
    fun voltarEstadoAnterior(): String? {
        return if (historicoEstados.isNotEmpty()) {
            estadoAtual.sair(contexto)
            estadoAtual = historicoEstados.removeLastOrNull() ?: estadoAtual
            estadoAtual.entrar(contexto)
        } else {
            null
        }
    }
    
    fun getEstadoAtual(): EstadoJogo {
        return when (estadoAtual) {
            is MenuPrincipalState -> EstadoJogo.MENU_PRINCIPAL
            is CriacaoPersonagemState -> EstadoJogo.CRIACAO_PERSONAGEM
            is AventuraState -> EstadoJogo.AVENTURA
            is AventuraEspecialState -> EstadoJogo.AVENTURA_ESPECIAL
            is CombateState -> EstadoJogo.COMBATE
            is CombateAventuraState -> EstadoJogo.COMBATE_AVENTURA
            is InventarioState -> EstadoJogo.INVENTARIO
            is DescansoState -> EstadoJogo.DESCANSO
            is LojaState -> EstadoJogo.LOJA
            is GameOverState -> EstadoJogo.GAME_OVER
            is VitoriaState -> EstadoJogo.VITORIA
            else -> EstadoJogo.MENU_PRINCIPAL
        }
    }
    
    fun getContexto(): ContextoJogo = contexto
    
    fun forcarEstado(novoEstado: GameState): String {
        estadoAtual.sair(contexto)
        historicoEstados.add(estadoAtual)
        estadoAtual = novoEstado
        return estadoAtual.entrar(contexto)
    }
    
    fun reiniciarJogo(): String {
        historicoEstados.clear()
        contexto.personagem = null
        contexto.partidaAtiva = false
        contexto.localizacaoAtual = ""
        contexto.monstrosAtivos = emptyList()
        contexto.mensagemStatus = ""
        contexto.opcoesDiponiveis = emptyList()
        contexto.dadosTemporarios.clear()
        
        estadoAtual = MenuPrincipalState()
        return estadoAtual.entrar(contexto)
    }
    
    fun salvarEstado(): Map<String, Any> {
        return mapOf(
            "estado_atual" to getEstadoAtual().name,
            "personagem" to (contexto.personagem?.nome ?: ""),
            "localizacao" to contexto.localizacaoAtual,
            "partida_ativa" to contexto.partidaAtiva,
            "dados_temporarios" to contexto.dadosTemporarios.toMap()
        )
    }
    
    fun carregarEstado(dadosSalvos: Map<String, Any>): String {
        return try {
            val estadoSalvo = EstadoJogo.valueOf(dadosSalvos["estado_atual"] as String)
            contexto.localizacaoAtual = dadosSalvos["localizacao"] as String
            contexto.partidaAtiva = dadosSalvos["partida_ativa"] as Boolean
            
            @Suppress("UNCHECKED_CAST")
            val dadosTemp = dadosSalvos["dados_temporarios"] as Map<String, Any>
            contexto.dadosTemporarios.putAll(dadosTemp)
            
            // Aqui seria necessário recriar o personagem e o estado apropriado
            // Por simplicidade, vamos apenas retornar ao menu principal
            reiniciarJogo()
        } catch (e: Exception) {
            "Erro ao carregar estado salvo. Retornando ao menu principal.\n" + reiniciarJogo()
        }
    }
    
    fun getStatusJogo(): String {
        return buildString {
            appendLine("=== STATUS DO JOGO ===")
            appendLine("Estado: ${getEstadoAtual()}")
            appendLine("Partida Ativa: ${contexto.partidaAtiva}")
            
            contexto.personagem?.let { personagem ->
                appendLine("Personagem: ${personagem.nome}")
                appendLine("Nível: ${personagem.nivel}")
                appendLine("PV: ${personagem.pontosDeVida}/${personagem.pontosDeVidaMaximos}")
                appendLine("Localização: ${contexto.localizacaoAtual}")
            }
            
            if (contexto.mensagemStatus.isNotEmpty()) {
                appendLine()
                appendLine("Status: ${contexto.mensagemStatus}")
            }
        }
    }
}
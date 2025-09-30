package com.rpg.core.states

class MenuPrincipalState : GameState() {
    
    override fun entrar(contexto: ContextoJogo): String {
        return buildString {
            append(limparTela())
            appendLine("╔══════════════════════════════════════╗")
            appendLine("║           OLD DRAGON 2 RPG           ║")
            appendLine("║        Terminal Adventure            ║")
            appendLine("╚══════════════════════════════════════╝")
            appendLine()
            appendLine("Bem-vindo ao mundo de Old Dragon!")
            appendLine("Um RPG baseado nas regras clássicas de fantasia medieval.")
            appendLine()
            
            if (contexto.partidaAtiva && contexto.personagem != null) {
                appendLine("Partida em andamento:")
                appendLine("Personagem: ${contexto.personagem!!.nome}")
                appendLine("Nível: ${contexto.personagem!!.nivel}")
                appendLine("Localização: ${contexto.localizacaoAtual}")
                appendLine()
            }
            
            append(formatarOpcoes(getOpcoes()))
        }
    }
    
    override fun processar(contexto: ContextoJogo, entrada: String): GameState? {
        val opcoes = getOpcoes()
        val escolha = validarEscolha(entrada, opcoes.size)
        
        return when (escolha) {
            1 -> {
                if (contexto.partidaAtiva && contexto.personagem != null) {
                    AventuraState()
                } else {
                    CriacaoPersonagemState()
                }
            }
            2 -> CriacaoPersonagemState()
            3 -> {
                if (contexto.personagem != null) {
                    InventarioState()
                } else {
                    null // Opção inválida se não há personagem
                }
            }
            4 -> {
                // Implementar carregamento de jogo salvo
                contexto.mensagemStatus = "Funcionalidade de carregar jogo ainda não implementada."
                this
            }
            5 -> {
                // Implementar salvamento
                if (contexto.partidaAtiva && contexto.personagem != null) {
                    contexto.mensagemStatus = "Jogo salvo com sucesso!"
                    this
                } else {
                    contexto.mensagemStatus = "Nenhuma partida ativa para salvar."
                    this
                }
            }
            6 -> {
                // Sair do jogo
                System.exit(0)
                null
            }
            else -> null
        }
    }
    
    override fun sair(contexto: ContextoJogo): String {
        return ""
    }
    
    override fun getOpcoes(): List<String> {
        return listOf(
            "Iniciar Nova Aventura / Continuar",
            "Criar Novo Personagem",
            "Ver Inventário",
            "Carregar Jogo",
            "Salvar Jogo",
            "Sair"
        )
    }
    
    override fun getDescricao(): String {
        return "Menu principal do jogo Old Dragon 2 RPG"
    }
}


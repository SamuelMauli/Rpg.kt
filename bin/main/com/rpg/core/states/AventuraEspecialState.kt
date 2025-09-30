package com.rpg.core.states

import com.rpg.adventure.*
import com.rpg.core.GerenciadorExperiencia
import com.rpg.core.GanhoExperiencia
import com.rpg.core.FonteExperiencia
import com.rpg.combat.GerenciadorDeCombate
import com.rpg.core.factories.MonstroFactory

class AventuraEspecialState : GameState() {
    
    private val gerenciadorAventura = GerenciadorAventura()
    private val gerenciadorXp = GerenciadorExperiencia()
    private val gerenciadorCombate = GerenciadorDeCombate()
    private val monstroFactory = MonstroFactory()
    private var aventuraIniciada = false
    
    override fun entrar(contexto: ContextoJogo): String {
        val personagem = contexto.personagem ?: return "Erro: Nenhum personagem ativo."
        
        if (!aventuraIniciada) {
            aventuraIniciada = true
            val aventura = SegredoMontanhaGelada()
            val inicioAventura = gerenciadorAventura.iniciarAventura(aventura)
            
            return buildString {
                append(limparTela())
                appendLine(inicioAventura)
                appendLine()
                appendLine(getEventoAtual())
            }
        } else {
            return buildString {
                append(limparTela())
                appendLine(gerenciadorAventura.getStatusAventura())
                appendLine()
                appendLine(getEventoAtual())
            }
        }
    }
    
    override fun processar(contexto: ContextoJogo, entrada: String): GameState? {
        val personagem = contexto.personagem!!
        val evento = gerenciadorAventura.getEventoAtual()
        
        if (evento == null) {
            // Aventura completada
            return AventuraState()
        }
        
        val escolha = validarEscolha(entrada, evento.opcoes.size)
        if (escolha == null) {
            contexto.mensagemStatus = "Opção inválida."
            return this
        }
        
        // Processar a escolha
        val resultado = gerenciadorAventura.processarEscolha(escolha)
        contexto.mensagemStatus = resultado
        
        // Aplicar consequências baseadas no tipo de evento
        when (evento.tipo) {
            TipoEvento.COMBATE, TipoEvento.CHEFE -> {
                return processarCombate(contexto, evento)
            }
            TipoEvento.TESOURO -> {
                processarTesouro(contexto, evento)
            }
            TipoEvento.ARMADILHA -> {
                processarArmadilha(contexto, evento, escolha)
            }
            else -> {
                // Eventos narrativos, puzzles, etc.
                if (evento.xpRecompensa > 0) {
                    val ganhoXp = GanhoExperiencia(
                        FonteExperiencia.EXPLORACAO,
                        evento.xpRecompensa,
                        evento.titulo
                    )
                    gerenciadorXp.distribuirExperiencia(listOf(personagem), listOf(ganhoXp))
                }
            }
        }
        
        // Verificar se a aventura foi completada
        if (gerenciadorAventura.isAventuraCompleta()) {
            contexto.mensagemStatus += "\n\n" + "🎉 AVENTURA COMPLETADA! 🎉"
            return VitoriaState()
        }
        
        return this
    }
    
    override fun sair(contexto: ContextoJogo): String {
        return "Saindo da aventura especial..."
    }
    
    override fun getOpcoes(): List<String> {
        val evento = gerenciadorAventura.getEventoAtual()
        return evento?.opcoes ?: listOf("Continuar")
    }
    
    override fun getDescricao(): String {
        val evento = gerenciadorAventura.getEventoAtual()
        return evento?.titulo ?: "Aventura Especial"
    }
    
    private fun getEventoAtual(): String {
        val evento = gerenciadorAventura.getEventoAtual()
        if (evento == null) {
            return "Aventura completada!"
        }
        
        return buildString {
            appendLine("╔══════════════════════════════════════╗")
            appendLine("║ ${evento.titulo.padEnd(36)} ║")
            appendLine("╚══════════════════════════════════════╝")
            appendLine()
            appendLine(evento.descricao)
            appendLine()
            
            if (evento.tipo == TipoEvento.COMBATE || evento.tipo == TipoEvento.CHEFE) {
                appendLine("⚔️ COMBATE IMINENTE!")
                if (evento.monstros.isNotEmpty()) {
                    appendLine("Inimigos: ${evento.monstros.joinToString(", ")}")
                }
                appendLine()
            } else if (evento.tipo == TipoEvento.TESOURO) {
                appendLine("💰 TESOURO ENCONTRADO!")
                if (evento.tesouro.isNotEmpty()) {
                    evento.tesouro.forEach { (item, quantidade) ->
                        appendLine("• $item ${if (quantidade > 1) "(${quantidade}x)" else ""}")
                    }
                }
                appendLine()
            } else if (evento.tipo == TipoEvento.ARMADILHA) {
                appendLine("⚠️ PERIGO! ARMADILHA DETECTADA!")
                appendLine()
            } else if (evento.tipo == TipoEvento.PUZZLE) {
                appendLine("🧩 ENIGMA PARA RESOLVER")
                appendLine()
            }
            
            append(formatarOpcoes(evento.opcoes))
        }
    }
    
    private fun processarCombate(contexto: ContextoJogo, evento: EventoAventura): GameState {
        val personagem = contexto.personagem!!
        
        // CORREÇÃO: Usar o método correto da MonstroFactory e flatMap
        val monstros = evento.monstros.flatMap { tipoMonstro ->
            monstroFactory.criarMonstro(tipoMonstro, 1)
        }
        
        contexto.monstrosAtivos = monstros
        
        // Iniciar combate
        val resultadoCombate = gerenciadorCombate.iniciarCombate(listOf(personagem), monstros)
        contexto.mensagemStatus += "\n\n$resultadoCombate"
        
        return CombateAventuraState(this, evento)
    }
    
    private fun processarTesouro(contexto: ContextoJogo, evento: EventoAventura) {
        val personagem = contexto.personagem!!
        
        evento.tesouro.forEach { (item, quantidade) ->
            when (item) {
                "Tesouro de Malachar" -> personagem.dinheiro += quantidade
                "Gemas Preciosas" -> personagem.dinheiro += quantidade * 50
                else -> {
                    repeat(quantidade) {
                        personagem.adicionarItem(item)
                    }
                }
            }
        }
        
        if (evento.xpRecompensa > 0) {
            val ganhoXp = GanhoExperiencia(
                FonteExperiencia.TESOURO,
                evento.xpRecompensa,
                "Tesouro encontrado: ${evento.titulo}"
            )
            gerenciadorXp.distribuirExperiencia(listOf(personagem), listOf(ganhoXp))
        }
    }
    
    private fun processarArmadilha(contexto: ContextoJogo, evento: EventoAventura, escolha: Int) {
        val personagem = contexto.personagem!!
        
        // Simular resultado da armadilha baseado na escolha
        val dano = when (escolha) {
            1 -> 2 // Esquiva - dano leve
            2 -> 0 // Escudo - sem dano
            3 -> 1 // Rolamento - dano mínimo
            else -> 3 // Escolha ruim - mais dano
        }
        
        if (dano > 0) {
            personagem.receberDano(dano)
            contexto.mensagemStatus += "\nVocê recebe $dano pontos de dano da armadilha!"
        }
        
        if (evento.xpRecompensa > 0) {
            val ganhoXp = GanhoExperiencia(
                FonteExperiencia.EXPLORACAO,
                evento.xpRecompensa,
                "Superar armadilha: ${evento.titulo}"
            )
            gerenciadorXp.distribuirExperiencia(listOf(personagem), listOf(ganhoXp))
        }
    }
}

class CombateAventuraState(
    private val aventuraState: AventuraEspecialState,
    private val eventoOriginal: EventoAventura
) : GameState() {
    
    private val gerenciadorCombate = GerenciadorDeCombate()
    private val gerenciadorXp = GerenciadorExperiencia()
    
    override fun entrar(contexto: ContextoJogo): String {
        return buildString {
            appendLine("╔══════════════════════════════════════╗")
            appendLine("║         COMBATE EM AVENTURA          ║")
            appendLine("╚══════════════════════════════════════╝")
            appendLine()
            appendLine(gerenciadorCombate.getStatusCombate())
            appendLine()
            append(formatarOpcoes(getOpcoes()))
        }
    }
    
    override fun processar(contexto: ContextoJogo, entrada: String): GameState? {
        val personagem = contexto.personagem!!
        val escolha = validarEscolha(entrada, getOpcoes().size)
        
        // Similar ao CombateState normal, mas retorna para a aventura
        val resultado = when (escolha) {
            1 -> {
                val alvo = contexto.monstrosAtivos.firstOrNull()?.nome ?: "Inimigo"
                gerenciadorCombate.executarAcao(personagem.nome, com.rpg.combat.TipoAcao.ATAQUE_CORPO_A_CORPO, alvo)
            }
            2 -> {
                val alvo = contexto.monstrosAtivos.firstOrNull()?.nome ?: "Inimigo"
                gerenciadorCombate.executarAcao(personagem.nome, com.rpg.combat.TipoAcao.ATAQUE_A_DISTANCIA, alvo)
            }
            3 -> gerenciadorCombate.executarAcao(personagem.nome, com.rpg.combat.TipoAcao.ATIVACAO)
            4 -> gerenciadorCombate.executarAcao(personagem.nome, com.rpg.combat.TipoAcao.FUGIR)
            else -> return null
        }
        
        contexto.mensagemStatus = resultado
        
        val fimRodada = gerenciadorCombate.finalizarRodada()
        contexto.mensagemStatus += "\n\n$fimRodada"
        
        if (fimRodada.contains("COMBATE FINALIZADO")) {
            return finalizarCombateAventura(contexto)
        }
        
        val proximaRodada = gerenciadorCombate.proximaRodada()
        contexto.mensagemStatus += "\n\n$proximaRodada"
        
        return this
    }
    
    override fun sair(contexto: ContextoJogo): String = ""
    
    override fun getOpcoes(): List<String> {
        return listOf(
            "Atacar Corpo a Corpo",
            "Atacar à Distância", 
            "Usar Habilidade",
            "Fugir"
        )
    }
    
    override fun getDescricao(): String = "Combate durante aventura"
    
    private fun finalizarCombateAventura(contexto: ContextoJogo): GameState {
        val personagem = contexto.personagem!!
        
        if (personagem.estaMorto()) {
            return GameOverState()
        }
        
        // Dar XP do combate + XP do evento
        val xpCombate = gerenciadorXp.calcularXpCombate(contexto.monstrosAtivos)
        val xpEvento = eventoOriginal.xpRecompensa
        
        val ganhosXp = mutableListOf<GanhoExperiencia>()
        
        if (xpCombate > 0) {
            ganhosXp.add(GanhoExperiencia(
                FonteExperiencia.COMBATE,
                xpCombate,
                "Combate em aventura: ${eventoOriginal.titulo}"
            ))
        }
        
        if (xpEvento > 0) {
            ganhosXp.add(GanhoExperiencia(
                FonteExperiencia.EXPLORACAO,
                xpEvento,
                "Completar evento: ${eventoOriginal.titulo}"
            ))
        }
        
        if (ganhosXp.isNotEmpty()) {
            gerenciadorXp.distribuirExperiencia(listOf(personagem), ganhosXp)
        }
        
        // Processar tesouro do evento se houver
        eventoOriginal.tesouro.forEach { (item, quantidade) ->
            repeat(quantidade) {
                personagem.adicionarItem(item)
            }
        }
        
        val relatorioXp = if (ganhosXp.isNotEmpty()) {
            gerenciadorXp.gerarRelatorioXp(personagem, ganhosXp)
        } else ""
        
        contexto.mensagemStatus = buildString {
            appendLine("=== VITÓRIA NO COMBATE! ===")
            if (relatorioXp.isNotEmpty()) {
                appendLine()
                appendLine(relatorioXp)
            }
            if (eventoOriginal.tesouro.isNotEmpty()) {
                appendLine()
                appendLine("Itens encontrados:")
                eventoOriginal.tesouro.forEach { (item, quantidade) ->
                    appendLine("• $item ${if (quantidade > 1) "(${quantidade}x)" else ""}")
                }
            }
        }
        
        contexto.monstrosAtivos = emptyList()
        
        return aventuraState
    }
}
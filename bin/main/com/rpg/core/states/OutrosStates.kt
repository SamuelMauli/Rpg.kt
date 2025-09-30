package com.rpg.core.states

import com.rpg.character.Personagem
import com.rpg.combat.GerenciadorDeCombate
import com.rpg.combat.TipoAcao
import com.rpg.core.GerenciadorExperiencia
import com.rpg.core.GanhoExperiencia
import com.rpg.core.FonteExperiencia
import com.rpg.items.*
import kotlin.random.Random

class CombateState : GameState() {
    
    private val gerenciadorCombate = GerenciadorDeCombate()
    private val gerenciadorXp = GerenciadorExperiencia()
    private var combateIniciado = false
    
    override fun entrar(contexto: ContextoJogo): String {
        val personagem = contexto.personagem ?: return "Erro: Nenhum personagem ativo."
        val monstros = contexto.monstrosAtivos
        
        if (!combateIniciado) {
            combateIniciado = true
            val resultadoInicio = gerenciadorCombate.iniciarCombate(listOf(personagem), monstros)
            val proximaRodada = gerenciadorCombate.proximaRodada()
            
            return buildString {
                append(limparTela())
                appendLine("╔══════════════════════════════════════╗")
                appendLine("║              COMBATE!                ║")
                appendLine("╚══════════════════════════════════════╝")
                appendLine()
                appendLine(resultadoInicio)
                appendLine()
                appendLine(proximaRodada)
                appendLine()
                append(formatarOpcoes(getOpcoes()))
            }
        } else {
            return buildString {
                appendLine(gerenciadorCombate.getStatusCombate())
                appendLine()
                append(formatarOpcoes(getOpcoes()))
            }
        }
    }
    
    override fun processar(contexto: ContextoJogo, entrada: String): GameState? {
        val personagem = contexto.personagem!!
        val escolha = validarEscolha(entrada, getOpcoes().size)
        
        val resultado = when (escolha) {
            1 -> {
                // Atacar corpo a corpo
                val alvo = contexto.monstrosAtivos.firstOrNull()?.nome ?: "Inimigo 1"
                gerenciadorCombate.executarAcao(personagem.nome, TipoAcao.ATAQUE_CORPO_A_CORPO, alvo)
            }
            2 -> {
                // Atacar à distância
                val alvo = contexto.monstrosAtivos.firstOrNull()?.nome ?: "Inimigo 1"
                gerenciadorCombate.executarAcao(personagem.nome, TipoAcao.ATAQUE_A_DISTANCIA, alvo)
            }
            3 -> {
                // Usar habilidade
                gerenciadorCombate.executarAcao(personagem.nome, TipoAcao.ATIVACAO)
            }
            4 -> {
                // Fugir
                gerenciadorCombate.executarAcao(personagem.nome, TipoAcao.FUGIR)
            }
            else -> return null
        }
        
        contexto.mensagemStatus = resultado
        
        // Finalizar rodada e verificar fim do combate
        val fimRodada = gerenciadorCombate.finalizarRodada()
        contexto.mensagemStatus += "\n\n$fimRodada"
        
        // Verificar se o combate terminou
        if (fimRodada.contains("COMBATE FINALIZADO")) {
            return finalizarCombate(contexto)
        }
        
        // Próxima rodada
        if (!fimRodada.contains("COMBATE FINALIZADO")) {
            val proximaRodada = gerenciadorCombate.proximaRodada()
            contexto.mensagemStatus += "\n\n$proximaRodada"
        }
        
        return this
    }
    
    override fun sair(contexto: ContextoJogo): String {
        return "Saindo do combate..."
    }
    
    override fun getOpcoes(): List<String> {
        return listOf(
            "Atacar Corpo a Corpo",
            "Atacar à Distância",
            "Usar Habilidade",
            "Fugir"
        )
    }
    
    override fun getDescricao(): String = "Em combate!"
    
    private fun finalizarCombate(contexto: ContextoJogo): GameState {
        val personagem = contexto.personagem!!
        
        if (personagem.estaMorto()) {
            return GameOverState()
        }
        
        // Calcular experiência ganha
        val xpCombate = gerenciadorXp.calcularXpCombate(contexto.monstrosAtivos)
        val tesouro = Random.nextInt(10, 51)
        val xpTesouro = gerenciadorXp.calcularXpTesouro(tesouro)
        
        val ganhosXp = listOf(
            GanhoExperiencia(FonteExperiencia.COMBATE, xpCombate, "Derrotar inimigos"),
            GanhoExperiencia(FonteExperiencia.TESOURO, xpTesouro, "Tesouro encontrado: $tesouro PO")
        )
        
        gerenciadorXp.distribuirExperiencia(listOf(personagem), ganhosXp)
        personagem.dinheiro += tesouro
        
        val relatorioXp = gerenciadorXp.gerarRelatorioXp(personagem, ganhosXp)
        
        // Verificar subida de nível
        var mensagemNivel = ""
        if (gerenciadorXp.verificarSubidaNivel(personagem)) {
            mensagemNivel = gerenciadorXp.subirNivel(personagem)
        }
        
        contexto.mensagemStatus = buildString {
            appendLine("=== VITÓRIA! ===")
            appendLine()
            appendLine(relatorioXp)
            if (mensagemNivel.isNotEmpty()) {
                appendLine()
                appendLine(mensagemNivel)
            }
        }
        
        contexto.monstrosAtivos = emptyList()
        combateIniciado = false
        
        return AventuraState()
    }
}

class InventarioState : GameState() {
    
    override fun entrar(contexto: ContextoJogo): String {
        val personagem = contexto.personagem ?: return "Erro: Nenhum personagem ativo."
        
        return buildString {
            append(limparTela())
            appendLine("╔══════════════════════════════════════╗")
            appendLine("║            INVENTÁRIO                ║")
            appendLine("╚══════════════════════════════════════╝")
            appendLine()
            
            // Mostrar itens do inventário (simplificado)
            appendLine("=== ITENS ===")
            if (personagem.inventario.isEmpty()) {
                appendLine("Inventário vazio.")
            } else {
                personagem.inventario.forEachIndexed { index, item ->
                    appendLine("${index + 1}. $item")
                }
            }
            
            appendLine()
            appendLine("=== EQUIPAMENTOS ===")
            personagem.equipamentos.forEach { (slot, item) ->
                appendLine("$slot: $item")
            }
            
            appendLine()
            appendLine("Dinheiro: ${personagem.dinheiro} PO")
            appendLine("Carga: ${personagem.getCargaAtual()}/${personagem.getCargaMaxima()}")
            
            appendLine()
            append(formatarOpcoes(getOpcoes()))
        }
    }
    
    override fun processar(contexto: ContextoJogo, entrada: String): GameState? {
        val escolha = validarEscolha(entrada, getOpcoes().size)
        
        return when (escolha) {
            1 -> {
                // Usar item (implementação simplificada)
                contexto.mensagemStatus = "Funcionalidade de usar item ainda não implementada."
                this
            }
            2 -> {
                // Equipar/Desequipar item
                contexto.mensagemStatus = "Funcionalidade de equipar item ainda não implementada."
                this
            }
            3 -> {
                // Descartar item
                contexto.mensagemStatus = "Funcionalidade de descartar item ainda não implementada."
                this
            }
            4 -> AventuraState()
            else -> null
        }
    }
    
    override fun sair(contexto: ContextoJogo): String = ""
    
    override fun getOpcoes(): List<String> {
        return listOf(
            "Usar Item",
            "Equipar/Desequipar",
            "Descartar Item",
            "Voltar"
        )
    }
    
    override fun getDescricao(): String = "Gerenciando inventário"
}

class DescansoState : GameState() {
    
    override fun entrar(contexto: ContextoJogo): String {
        return buildString {
            append(limparTela())
            appendLine("╔══════════════════════════════════════╗")
            appendLine("║             DESCANSO                 ║")
            appendLine("╚══════════════════════════════════════╝")
            appendLine()
            appendLine("Você encontra um local seguro para descansar.")
            appendLine("O descanso permite recuperar pontos de vida e magias.")
            appendLine()
            append(formatarOpcoes(getOpcoes()))
        }
    }
    
    override fun processar(contexto: ContextoJogo, entrada: String): GameState? {
        val personagem = contexto.personagem!!
        val escolha = validarEscolha(entrada, getOpcoes().size)
        
        return when (escolha) {
            1 -> {
                // Descanso curto (1 hora)
                val cura = Random.nextInt(1, 5) // 1d4
                personagem.curarDano(cura)
                contexto.mensagemStatus = "Você descansa por 1 hora e recupera $cura pontos de vida."
                AventuraState()
            }
            2 -> {
                // Descanso longo (8 horas)
                val curaTotal = personagem.pontosDeVidaMaximos - personagem.pontosDeVida
                personagem.curarDano(curaTotal)
                contexto.mensagemStatus = "Você descansa por 8 horas e recupera todos os pontos de vida e magias."
                AventuraState()
            }
            3 -> AventuraState()
            else -> null
        }
    }
    
    override fun sair(contexto: ContextoJogo): String = ""
    
    override fun getOpcoes(): List<String> {
        return listOf(
            "Descanso Curto (1 hora)",
            "Descanso Longo (8 horas)",
            "Cancelar"
        )
    }
    
    override fun getDescricao(): String = "Descansando para recuperar forças"
}

class LojaState : GameState() {
    
    private val itensVenda = listOf(
        "Poção de Cura" to 50,
        "Espada Longa" to 15,
        "Armadura de Couro" to 5,
        "Escudo Pequeno" to 3,
        "Corda (15 metros)" to 2,
        "Tocha (5 unidades)" to 1
    )
    
    override fun entrar(contexto: ContextoJogo): String {
        val personagem = contexto.personagem!!
        
        return buildString {
            append(limparTela())
            appendLine("╔══════════════════════════════════════╗")
            appendLine("║         LOJA GERAL                   ║")
            appendLine("╚══════════════════════════════════════╝")
            appendLine()
            appendLine("Bem-vindo à loja! O que deseja?")
            appendLine()
            appendLine("Seu dinheiro: ${personagem.dinheiro} PO")
            appendLine()
            appendLine("=== ITENS À VENDA ===")
            itensVenda.forEachIndexed { index, (item, preco) ->
                appendLine("${index + 1}. $item - $preco PO")
            }
            appendLine()
            append(formatarOpcoes(getOpcoes()))
        }
    }
    
    override fun processar(contexto: ContextoJogo, entrada: String): GameState? {
        val personagem = contexto.personagem!!
        val escolha = validarEscolha(entrada, getOpcoes().size)
        
        return when (escolha) {
            in 1..itensVenda.size -> {
                val (item, preco) = itensVenda[escolha!! - 1]
                if (personagem.dinheiro >= preco) {
                    personagem.dinheiro -= preco
                    personagem.adicionarItem(item)
                    contexto.mensagemStatus = "Você comprou: $item por $preco PO"
                } else {
                    contexto.mensagemStatus = "Dinheiro insuficiente para comprar $item"
                }
                this
            }
            itensVenda.size + 1 -> AventuraState()
            else -> null
        }
    }
    
    override fun sair(contexto: ContextoJogo): String = ""
    
    override fun getOpcoes(): List<String> {
        return itensVenda.map { "${it.first} (${it.second} PO)" } + "Sair da Loja"
    }
    
    override fun getDescricao(): String = "Fazendo compras na loja"
}

class GameOverState : GameState() {
    
    override fun entrar(contexto: ContextoJogo): String {
        return buildString {
            append(limparTela())
            appendLine("╔══════════════════════════════════════╗")
            appendLine("║            GAME OVER                 ║")
            appendLine("╚══════════════════════════════════════╝")
            appendLine()
            appendLine("Seu aventureiro pereceu em sua jornada...")
            appendLine()
            
            contexto.personagem?.let { personagem ->
                appendLine("${personagem.nome} - ${personagem.raca.getNome()} ${personagem.classe.getNome()}")
                appendLine("Nível alcançado: ${personagem.nivel}")
                appendLine("Experiência total: ${personagem.experiencia}")
                appendLine("Localização final: ${contexto.localizacaoAtual}")
            }
            
            appendLine()
            appendLine("Que sua próxima aventura seja mais bem-sucedida!")
            appendLine()
            append(formatarOpcoes(getOpcoes()))
        }
    }
    
    override fun processar(contexto: ContextoJogo, entrada: String): GameState? {
        val escolha = validarEscolha(entrada, getOpcoes().size)
        
        return when (escolha) {
            1 -> CriacaoPersonagemState()
            2 -> MenuPrincipalState()
            3 -> {
                System.exit(0)
                null
            }
            else -> null
        }
    }
    
    override fun sair(contexto: ContextoJogo): String = ""
    
    override fun getOpcoes(): List<String> {
        return listOf(
            "Criar Novo Personagem",
            "Menu Principal",
            "Sair do Jogo"
        )
    }
    
    override fun getDescricao(): String = "Fim de jogo"
}

class VitoriaState : GameState() {
    
    override fun entrar(contexto: ContextoJogo): String {
        return buildString {
            append(limparTela())
            appendLine("╔══════════════════════════════════════╗")
            appendLine("║             VITÓRIA!                 ║")
            appendLine("╚══════════════════════════════════════╝")
            appendLine()
            appendLine("Parabéns! Você completou a aventura!")
            appendLine()
            
            contexto.personagem?.let { personagem ->
                appendLine("${personagem.nome} - ${personagem.raca.getNome()} ${personagem.classe.getNome()}")
                appendLine("Nível final: ${personagem.nivel}")
                appendLine("Experiência total: ${personagem.experiencia}")
                appendLine("Dinheiro acumulado: ${personagem.dinheiro} PO")
            }
            
            appendLine()
            appendLine("Sua coragem e determinação salvaram o reino!")
            appendLine("Lendas serão contadas sobre seus feitos heroicos.")
            appendLine()
            append(formatarOpcoes(getOpcoes()))
        }
    }
    
    override fun processar(contexto: ContextoJogo, entrada: String): GameState? {
        val escolha = validarEscolha(entrada, getOpcoes().size)
        
        return when (escolha) {
            1 -> CriacaoPersonagemState()
            2 -> MenuPrincipalState()
            3 -> {
                System.exit(0)
                null
            }
            else -> null
        }
    }
    
    override fun sair(contexto: ContextoJogo): String = ""
    
    override fun getOpcoes(): List<String> {
        return listOf(
            "Nova Aventura",
            "Menu Principal",
            "Sair do Jogo"
        )
    }
    
    override fun getDescricao(): String = "Aventura completada com sucesso!"
}
package com.rpg.core.states

import com.rpg.core.factories.MonstroFactory
import kotlin.random.Random

class AventuraState : GameState() {
    
    private val monstroFactory = MonstroFactory()
    
    override fun entrar(contexto: ContextoJogo): String {
        val personagem = contexto.personagem ?: return "Erro: Nenhum personagem ativo."
        
        return buildString {
            append(limparTela())
            appendLine("╔══════════════════════════════════════╗")
            appendLine("║             AVENTURA                 ║")
            appendLine("╚══════════════════════════════════════╝")
            appendLine()
            appendLine("Localização: ${contexto.localizacaoAtual}")
            appendLine()
            appendLine("=== STATUS DO PERSONAGEM ===")
            appendLine("${personagem.nome} - ${personagem.raca.getNome()} ${personagem.classe.getNome()}")
            appendLine("Nível: ${personagem.nivel}")
            appendLine("PV: ${personagem.pontosDeVida}/${personagem.pontosDeVidaMaximos}")
            appendLine("XP: ${personagem.experiencia}/${personagem.getXpNecessarioProximoNivel()}")
            appendLine("Dinheiro: ${personagem.dinheiro} PO")
            appendLine()
            
            if (contexto.mensagemStatus.isNotEmpty()) {
                appendLine("Status: ${contexto.mensagemStatus}")
                appendLine()
            }
            
            appendLine(getDescricaoLocalizacao(contexto.localizacaoAtual))
            appendLine()
            append(formatarOpcoes(getOpcoes()))
        }
    }
    
    override fun processar(contexto: ContextoJogo, entrada: String): GameState? {
        val escolha = validarEscolha(entrada, getOpcoes().size)
        
        return when (escolha) {
            1 -> explorarArea(contexto)
            2 -> AventuraEspecialState()
            3 -> InventarioState()
            4 -> DescansoState()
            5 -> {
                // Ir para loja (se disponível na localização)
                if (contexto.localizacaoAtual == "Vila de Pedravale") {
                    LojaState()
                } else {
                    contexto.mensagemStatus = "Não há lojas nesta localização."
                    this
                }
            }
            6 -> {
                // Ver status detalhado
                contexto.mensagemStatus = contexto.personagem?.getResumoPersonagem() ?: "Erro"
                this
            }
            7 -> MenuPrincipalState()
            else -> null
        }
    }
    
    override fun sair(contexto: ContextoJogo): String {
        return ""
    }
    
    override fun getOpcoes(): List<String> {
        return listOf(
            "Explorar Área",
            "Iniciar Aventura Especial",
            "Ver Inventário",
            "Descansar",
            "Visitar Loja",
            "Ver Status",
            "Menu Principal"
        )
    }
    
    override fun getDescricao(): String {
        return "Explorando o mundo de Old Dragon"
    }
    
    private fun getDescricaoLocalizacao(localizacao: String): String {
        return when (localizacao) {
            "Vila de Pedravale" -> {
                "Você está na pacata Vila de Pedravale, um pequeno povoado cercado por " +
                "campos verdejantes. As casas de pedra e madeira se alinham ao longo de " +
                "uma estrada de terra batida. Há uma taverna, uma loja geral e alguns " +
                "camponeses trabalhando nos campos próximos."
            }
            "Floresta Sombria" -> {
                "Uma floresta densa e misteriosa se estende à sua frente. As árvores " +
                "antigas bloqueiam a maior parte da luz solar, criando sombras profundas " +
                "entre os troncos. Você pode ouvir ruídos estranhos vindos das profundezas " +
                "da mata."
            }
            "Ruínas Antigas" -> {
                "Estruturas de pedra cobertas por musgo e hera se erguem diante de você. " +
                "Estas ruínas parecem ter séculos de idade, e uma aura mágica sinistra " +
                "paira no ar. Passagens escuras levam para o interior das construções."
            }
            "Cavernas Profundas" -> {
                "Você se encontra na entrada de um sistema de cavernas que se estende " +
                "profundamente na montanha. O ar é úmido e frio, e ecos distantes " +
                "sugerem que estas cavernas são habitadas por criaturas perigosas."
            }
            else -> "Uma localização desconhecida e misteriosa."
        }
    }
    
    private fun explorarArea(contexto: ContextoJogo): GameState {
        val personagem = contexto.personagem!!
        
        // Determinar tipo de encontro
        val tipoEncontro = Random.nextInt(1, 101)
        
        return when {
            tipoEncontro <= 40 -> {
                // Encontro com monstros
                val nivelDesafio = minOf(5, maxOf(1, personagem.nivel))
                val monstros = monstroFactory.criarGrupoAleatorio(nivelDesafio)
                contexto.monstrosAtivos = monstros
                
                contexto.mensagemStatus = buildString {
                    appendLine("Você encontra inimigos!")
                    monstros.forEach { monstro ->
                        appendLine("• ${monstro.nome} (${monstro.pontosDeVida} PV)")
                    }
                }
                
                CombateState()
            }
            tipoEncontro <= 60 -> {
                // Encontrar tesouro
                val ouroEncontrado = Random.nextInt(10, 51)
                personagem.dinheiro += ouroEncontrado
                contexto.mensagemStatus = "Você encontra um baú com $ouroEncontrado moedas de ouro!"
                this
            }
            tipoEncontro <= 75 -> {
                // Encontrar item
                encontrarItem(contexto)
                this
            }
            tipoEncontro <= 85 -> {
                // Evento especial
                eventoEspecial(contexto)
                this
            }
            else -> {
                // Nada encontrado
                contexto.mensagemStatus = "Você explora a área mas não encontra nada de interessante."
                this
            }
        }
    }
    
    private fun encontrarItem(contexto: ContextoJogo) {
        val personagem = contexto.personagem!!
        val itensEncontrados = listOf(
            "Poção de Cura",
            "Corda (15 metros)",
            "Tocha",
            "Ração de Viagem",
            "Adaga",
            "Escudo Pequeno"
        )
        
        val itemEncontrado = itensEncontrados.random()
        personagem.adicionarItem(itemEncontrado)
        contexto.mensagemStatus = "Você encontra: $itemEncontrado"
    }
    
    private fun eventoEspecial(contexto: ContextoJogo): GameState {
        val eventos = listOf(
            "Você encontra um viajante ferido que lhe dá informações valiosas sobre a região.",
            "Uma fonte mágica restaura alguns de seus pontos de vida.",
            "Você descobre uma passagem secreta que leva a uma nova área.",
            "Um comerciante ambulante oferece seus serviços.",
            "Você encontra pegadas estranhas no chão..."
        )
        
        val evento = eventos.random()
        contexto.mensagemStatus = evento
        
        // Aplicar efeitos do evento
        when (evento) {
            eventos[1] -> {
                // Fonte mágica - curar personagem
                val personagem = contexto.personagem!!
                val cura = Random.nextInt(5, 16) // 5-15 PV
                personagem.curarDano(cura)
                contexto.mensagemStatus += "\nVocê recupera $cura pontos de vida!"
            }
            eventos[2] -> {
                // Passagem secreta - nova localização
                if (contexto.localizacaoAtual == "Vila de Pedravale") {
                    contexto.localizacaoAtual = "Floresta Sombria"
                    contexto.mensagemStatus += "\nVocê descobriu a Floresta Sombria!"
                }
            }
        }
        
        return this
    }
}


package com.rpg.core.states

import com.rpg.core.factories.PersonagemFactory
import com.rpg.character.races.Alinhamento

class CriacaoPersonagemState : GameState() {
    
    private val factory = PersonagemFactory()
    private var etapa = 1
    private var nomePersonagem = ""
    private var racaEscolhida = ""
    private var classeEscolhida = ""
    private var metodoRolagem = "classico"
    private var atributosPersonalizados: List<Int>? = null
    
    // Flag para controlar a inicialização
    private var primeiraEntrada = true
    
    override fun entrar(contexto: ContextoJogo): String {
        // CORREÇÃO: Mover a lógica de reset para só rodar uma vez
        if (primeiraEntrada) {
            etapa = 1
            nomePersonagem = ""
            racaEscolhida = ""
            classeEscolhida = ""
            metodoRolagem = "classico"
            atributosPersonalizados = null
            primeiraEntrada = false
        }
        
        return buildString {
            append(limparTela())
            appendLine("╔══════════════════════════════════════╗")
            appendLine("║        CRIAÇÃO DE PERSONAGEM         ║")
            appendLine("╚══════════════════════════════════════╝")
            appendLine()
            appendLine("Vamos criar seu aventureiro!")
            appendLine()
            append(getProximaEtapa())
        }
    }
    
    override fun processar(contexto: ContextoJogo, entrada: String): GameState? {
        return when (etapa) {
            1 -> processarNome(entrada)
            2 -> processarRaca(entrada)
            3 -> processarClasse(entrada)
            4 -> processarMetodoRolagem(entrada)
            5 -> finalizarCriacao(contexto, entrada)
            else -> null
        }
    }
    
    override fun sair(contexto: ContextoJogo): String {
        primeiraEntrada = true // Reseta a flag ao sair do estado
        return "Saindo da criação de personagem..."
    }
    
    override fun getOpcoes(): List<String> {
        return when (etapa) {
            1 -> listOf("Digite o nome do personagem")
            2 -> factory.getRacasDisponiveis() + "Voltar"
            3 -> factory.getClassesDisponiveis() + "Voltar"
            4 -> factory.getMetodosRolagemDisponiveis() + "Voltar"
            5 -> listOf("Confirmar", "Refazer", "Voltar ao Menu")
            else -> emptyList()
        }
    }
    
    override fun getDescricao(): String {
        return "Criação de novo personagem - Etapa $etapa"
    }
    
    private fun getProximaEtapa(): String {
        return when (etapa) {
            1 -> {
                "Etapa 1/4: Nome do Personagem\n\n" +
                "Digite o nome do seu aventureiro: "
            }
            2 -> {
                "Etapa 2/4: Escolha da Raça\n\n" +
                "Nome: $nomePersonagem\n\n" +
                "Escolha a raça do seu personagem:\n" +
                formatarOpcoes(getOpcoes())
            }
            3 -> {
                "Etapa 3/4: Escolha da Classe\n\n" +
                "Nome: $nomePersonagem\n" +
                "Raça: $racaEscolhida\n\n" +
                "Escolha a classe do seu personagem:\n" +
                formatarOpcoes(getOpcoes())
            }
            4 -> {
                "Etapa 4/4: Método de Rolagem de Atributos\n\n" +
                "Nome: $nomePersonagem\n" +
                "Raça: $racaEscolhida\n" +
                "Classe: $classeEscolhida\n\n" +
                "Escolha o método de geração de atributos:\n" +
                "• Clássico: 3d6 para cada atributo\n" +
                "• Aventureiro: 4d6, descarta menor\n" +
                "• Heróico: Valores fixos altos\n\n" +
                formatarOpcoes(getOpcoes())
            }
            5 -> {
                mostrarResumoPersonagem()
            }
            else -> "Erro na criação do personagem."
        }
    }
    
    private fun processarNome(entrada: String): GameState? {
        if (entrada.isBlank()) {
            return null
        }
        
        nomePersonagem = entrada.trim()
        etapa = 2
        return this
    }
    
    private fun processarRaca(entrada: String): GameState? {
        val opcoes = getOpcoes()
        val escolha = validarEscolha(entrada, opcoes.size)
        
        return when (escolha) {
            in 1..factory.getRacasDisponiveis().size -> {
                racaEscolhida = factory.getRacasDisponiveis()[escolha!! - 1]
                etapa = 3
                this
            }
            opcoes.size -> {
                // Voltar
                etapa = 1
                this
            }
            else -> null
        }
    }
    
    private fun processarClasse(entrada: String): GameState? {
        val opcoes = getOpcoes()
        val escolha = validarEscolha(entrada, opcoes.size)
        
        return when (escolha) {
            in 1..factory.getClassesDisponiveis().size -> {
                classeEscolhida = factory.getClassesDisponiveis()[escolha!! - 1]
                etapa = 4
                this
            }
            opcoes.size -> {
                // Voltar
                etapa = 2
                this
            }
            else -> null
        }
    }
    
    private fun processarMetodoRolagem(entrada: String): GameState? {
        val opcoes = getOpcoes()
        val escolha = validarEscolha(entrada, opcoes.size)
        
        return when (escolha) {
            in 1..factory.getMetodosRolagemDisponiveis().size -> {
                metodoRolagem = factory.getMetodosRolagemDisponiveis()[escolha!! - 1].lowercase()
                etapa = 5
                this
            }
            opcoes.size -> {
                // Voltar
                etapa = 3
                this
            }
            else -> null
        }
    }
    
    private fun finalizarCriacao(contexto: ContextoJogo, entrada: String): GameState? {
        val escolha = validarEscolha(entrada, 3)
        
        return when (escolha) {
            1 -> {
                // Confirmar criação
                try {
                    val personagem = factory.criarPersonagemCompleto(
                        nome = nomePersonagem,
                        tipoRaca = racaEscolhida,
                        tipoClasse = classeEscolhida,
                        metodoRolagem = metodoRolagem,
                        atributosPersonalizados = atributosPersonalizados,
                        alinhamento = null // Será escolhido aleatoriamente
                    )
                    
                    contexto.personagem = personagem
                    contexto.partidaAtiva = true
                    contexto.localizacaoAtual = "Vila de Pedravale"
                    contexto.mensagemStatus = "Personagem criado com sucesso!"
                    
                    AventuraState()
                } catch (e: Exception) {
                    contexto.mensagemStatus = "Erro ao criar personagem: ${e.message}"
                    this
                }
            }
            2 -> {
                // Refazer
                etapa = 1
                this
            }
            3 -> {
                // Voltar ao menu
                MenuPrincipalState()
            }
            else -> null
        }
    }
    
    private fun mostrarResumoPersonagem(): String {
        return buildString {
            appendLine("=== RESUMO DO PERSONAGEM ===")
            appendLine()
            appendLine("Nome: $nomePersonagem")
            appendLine("Raça: $racaEscolhida")
            appendLine("Classe: $classeEscolhida")
            appendLine("Método de Atributos: $metodoRolagem")
            appendLine()
            
            // Mostrar preview dos atributos (simulado)
            try {
                val personagemTemp = factory.criarPersonagemCompleto(
                    nome = nomePersonagem,
                    tipoRaca = racaEscolhida,
                    tipoClasse = classeEscolhida,
                    metodoRolagem = metodoRolagem
                )
                
                appendLine("=== ATRIBUTOS GERADOS ===")
                appendLine("Força: ${personagemTemp.atributos.forca}")
                appendLine("Destreza: ${personagemTemp.atributos.destreza}")
                appendLine("Constituição: ${personagemTemp.atributos.constituicao}")
                appendLine("Inteligência: ${personagemTemp.atributos.inteligencia}")
                appendLine("Sabedoria: ${personagemTemp.atributos.sabedoria}")
                appendLine("Carisma: ${personagemTemp.atributos.carisma}")
                appendLine()
                appendLine("Pontos de Vida: ${personagemTemp.pontosDeVida}")
                appendLine("Classe de Armadura: ${personagemTemp.getCA()}")
                appendLine("Alinhamento: ${personagemTemp.alinhamento}")
                appendLine("Dinheiro Inicial: ${personagemTemp.dinheiro} PO")
                
            } catch (e: Exception) {
                appendLine("Erro ao gerar preview: ${e.message}")
            }
            
            appendLine()
            append(formatarOpcoes(getOpcoes()))
        }
    }
}
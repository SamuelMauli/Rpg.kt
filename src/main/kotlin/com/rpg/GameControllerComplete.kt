package com.rpg

import com.rpg.character.Personagem
import com.rpg.character.attributes.Atributos
import com.rpg.character.races.*
import com.rpg.character.classes.*
import com.rpg.database.DatabaseManager
import com.rpg.combat.CombatSystem
import com.rpg.core.ExperienceSystem
import com.rpg.world.WorldExploration
import com.rpg.quest.QuestSystem
import com.rpg.magic.MagicSystem
import com.rpg.ui.enhanced.EnhancedUI
import java.util.*
import kotlin.random.Random

/**
 * Controlador Principal do Jogo - Versão Completa
 * Integra todos os sistemas: combate, exploração, quests, magias, etc.
 */
class GameControllerComplete {
    
    private val scanner = Scanner(System.`in`)
    private val ui = EnhancedUI()
    private val experienceSystem = ExperienceSystem()
    private val combatSystem = CombatSystem()
    private val worldExploration = WorldExploration()
    private val questSystem = QuestSystem()
    private val magicSystem = MagicSystem()
    
    private var personagemAtual: Personagem? = null
    private var localizacaoAtual = "Vila Inicial"
    private val grimorio = MagicSystem.Grimorio()
    
    init {
        DatabaseManager.init()
    }
    
    fun iniciar() {
        ui.limparTela()
        ui.mostrarLogo()
        
        Thread.sleep(2000)
        
        while (true) {
            ui.limparTela()
            when (mostrarMenuPrincipal()) {
                1 -> criarPersonagem()
                2 -> carregarPersonagem()
                3 -> if (personagemAtual != null) menuAventura()
                4 -> if (personagemAtual != null) menuPersonagem()
                5 -> if (personagemAtual != null) menuQuests()
                6 -> if (personagemAtual != null) menuMagias()
                7 -> {
                    ui.mostrarInfo("Obrigado por jogar Old Dragon 2 RPG!")
                    break
                }
            }
        }
    }
    
    private fun mostrarMenuPrincipal(): Int {
        println(ui.mostrarMenu(
            "MENU PRINCIPAL",
            buildList {
                add("🆕 Criar Novo Personagem")
                add("📂 Carregar Personagem")
                if (personagemAtual != null) {
                    add("🗺️  Menu de Aventura")
                    add("👤 Menu do Personagem")
                    add("📜 Menu de Quests")
                    add("✨ Menu de Magias")
                }
                add("🚪 Sair")
            }
        ))
        
        print("Escolha uma opção: ")
        return scanner.nextLine().toIntOrNull() ?: 0
    }
    
    private fun criarPersonagem() {
        ui.limparTela()
        ui.mostrarDivisor()
        println(EnhancedUI.Colors.BRIGHT_YELLOW + "       CRIAÇÃO DE PERSONAGEM" + EnhancedUI.Colors.RESET)
        ui.mostrarDivisor()
        println()
        
        print("Nome do personagem: ")
        val nome = scanner.nextLine()
        
        // Escolher raça
        println(ui.mostrarMenu(
            "ESCOLHA SUA RAÇA",
            listOf(
                "👨 Humano - Versátil, +1 em todos os atributos",
                "🧝 Elfo - Ágil e mágico, resistência a sono e encantamentos",
                "🧔 Anão - Resistente, bônus contra venenos e magias",
                "🧒 Halfling - Pequeno e sortudo, bônus em furtividade"
            )
        ))
        
        val raca = when (scanner.nextLine().toIntOrNull()) {
            1 -> Humano()
            2 -> Elfo()
            3 -> Anao()
            4 -> Halfling()
            else -> Humano()
        }
        
        // Escolher classe
        println(ui.mostrarMenu(
            "ESCOLHA SUA CLASSE",
            listOf(
                "⚔️  Guerreiro - Especialista em combate, maestria em armas",
                "🔮 Mago - Conjurador arcano, magias poderosas",
                "✝️  Clérigo - Curandeiro divino, afasta mortos-vivos",
                "🗡️  Ladrão - Furtivo e habilidoso, talentos especiais"
            )
        ))
        
        val classe = when (scanner.nextLine().toIntOrNull()) {
            1 -> Guerreiro()
            2 -> Mago()
            3 -> Clerigo()
            4 -> Ladrao()
            else -> Guerreiro()
        }
        
        // Gerar atributos
        ui.mostrarInfo("Gerando atributos...")
        val atributos = Atributos(
            forca = rolarAtributo(),
            destreza = rolarAtributo(),
            constituicao = rolarAtributo(),
            inteligencia = rolarAtributo(),
            sabedoria = rolarAtributo(),
            carisma = rolarAtributo()
        )
        
        // Calcular PV inicial
        val pvInicial = classe.getDadoDeVida() + atributos.getModificadorConstituicao()
        
        // Criar personagem
        personagemAtual = Personagem(
            nome = nome,
            raca = raca,
            classe = classe,
            nivel = 1,
            atributos = atributos,
            pontosDeVida = pvInicial,
            pontosDeVidaMaximos = pvInicial,
            experiencia = 0,
            alinhamento = Alinhamento.NEUTRO,
            dinheiro = 100
        )
        
        // Salvar no banco de dados
        DatabaseManager.salvarPersonagem(
            nome = nome,
            raca = raca.getNome(),
            classe = classe.getNome(),
            nivel = 1,
            forca = atributos.forca,
            destreza = atributos.destreza,
            constituicao = atributos.constituicao,
            inteligencia = atributos.inteligencia,
            sabedoria = atributos.sabedoria,
            carisma = atributos.carisma,
            pontosVida = pvInicial,
            pontosVidaMaximos = pvInicial,
            experiencia = 0,
            dinheiro = 100,
            localizacaoAtual = localizacaoAtual
        )
        
        ui.limparTela()
        ui.mostrarSucesso("Personagem criado com sucesso!")
        println()
        println(ui.mostrarTelaPersonagem(personagemAtual!!))
        
        ui.aguardarEnter()
    }
    
    private fun carregarPersonagem() {
        ui.limparTela()
        ui.mostrarDivisor()
        println(EnhancedUI.Colors.BRIGHT_YELLOW + "       CARREGAR PERSONAGEM" + EnhancedUI.Colors.RESET)
        ui.mostrarDivisor()
        println()
        
        // TODO: Implementar carregamento do banco de dados
        ui.mostrarAviso("Sistema de carregamento em desenvolvimento")
        ui.aguardarEnter()
    }
    
    private fun menuAventura() {
        while (true) {
            ui.limparTela()
            println(ui.mostrarMenu(
                "MENU DE AVENTURA",
                listOf(
                    "🗺️  Viajar para outra localização",
                    "🔍 Explorar localização atual ($localizacaoAtual)",
                    "⚔️  Procurar combate",
                    "🏪 Visitar loja",
                    "💬 Falar com NPCs",
                    "🎲 Evento aleatório",
                    "🔙 Voltar ao menu principal"
                )
            ))
            
            when (scanner.nextLine().toIntOrNull()) {
                1 -> viajar()
                2 -> explorar()
                3 -> procurarCombate()
                4 -> visitarLoja()
                5 -> falarComNPCs()
                6 -> eventoAleatorio()
                7 -> break
            }
        }
    }
    
    private fun viajar() {
        ui.limparTela()
        ui.mostrarInfo("Localização atual: $localizacaoAtual")
        println()
        
        val destinos = worldExploration.getLocalizacoesDisponiveis(localizacaoAtual)
        
        println(ui.mostrarMenu(
            "ESCOLHA SEU DESTINO",
            destinos + "Cancelar"
        ))
        
        val escolha = scanner.nextLine().toIntOrNull() ?: 0
        if (escolha in 1..destinos.size) {
            val destino = destinos[escolha - 1]
            ui.mostrarInfo("Viajando para $destino...")
            Thread.sleep(1000)
            
            val resultado = worldExploration.viajar(personagemAtual!!, destino)
            
            if (resultado.sucesso) {
                localizacaoAtual = destino
                ui.mostrarSucesso(resultado.mensagem)
                ui.mostrarInfo("Você ganhou ${resultado.xpGanho} XP pela viagem!")
                
                // Processar eventos
                if (resultado.eventos.isNotEmpty()) {
                    ui.mostrarAviso("Eventos ocorreram durante a viagem!")
                    resultado.eventos.forEach { evento ->
                        processarEvento(evento)
                    }
                }
            } else {
                ui.mostrarErro("Não foi possível viajar para $destino")
            }
            
            ui.aguardarEnter()
        }
    }
    
    private fun explorar() {
        ui.limparTela()
        ui.mostrarInfo("Explorando $localizacaoAtual...")
        Thread.sleep(1500)
        
        val resultado = worldExploration.explorar(personagemAtual!!, localizacaoAtual)
        
        println()
        ui.mostrarSucesso(resultado.mensagem)
        println()
        
        resultado.descobertas.forEach { descoberta ->
            println("  🔍 $descoberta")
            Thread.sleep(500)
        }
        
        if (resultado.xpGanho > 0) {
            println()
            ui.mostrarInfo("Você ganhou ${resultado.xpGanho} XP!")
        }
        
        ui.aguardarEnter()
    }
    
    private fun procurarCombate() {
        ui.limparTela()
        ui.mostrarInfo("Procurando por inimigos...")
        Thread.sleep(1500)
        
        // Gerar combate aleatório
        val monstros = listOf("Goblin", "Kobold", "Esqueleto", "Lobo", "Orc")
        val monstro = monstros.random()
        
        ui.mostrarAviso("Um $monstro aparece!")
        Thread.sleep(1000)
        
        iniciarCombate(monstro)
    }
    
    private fun iniciarCombate(nomeInimigo: String) {
        // Simulação de combate
        val inimigoHP = Random.nextInt(10, 30)
        var inimigoHPAtual = inimigoHP
        
        while (personagemAtual!!.pontosDeVida > 0 && inimigoHPAtual > 0) {
            ui.limparTela()
            println(ui.mostrarTelaCombate(personagemAtual!!, nomeInimigo, inimigoHPAtual, inimigoHP))
            
            println(ui.mostrarMenu(
                "AÇÕES DE COMBATE",
                listOf(
                    "⚔️  Atacar",
                    "🛡️  Defender",
                    "✨ Usar Magia",
                    "🧪 Usar Item",
                    "🏃 Fugir"
                )
            ))
            
            when (scanner.nextLine().toIntOrNull()) {
                1 -> {
                    val dano = Random.nextInt(5, 15)
                    inimigoHPAtual -= dano
                    ui.mostrarSucesso("Você causou $dano de dano!")
                }
                2 -> ui.mostrarInfo("Você se defende!")
                3 -> ui.mostrarAviso("Sistema de magias em combate em desenvolvimento")
                4 -> ui.mostrarAviso("Uso de itens em desenvolvimento")
                5 -> {
                    ui.mostrarInfo("Você fugiu do combate!")
                    return
                }
            }
            
            if (inimigoHPAtual > 0) {
                Thread.sleep(1000)
                val danoInimigo = Random.nextInt(3, 10)
                personagemAtual!!.pontosDeVida -= danoInimigo
                ui.mostrarErro("$nomeInimigo causou $danoInimigo de dano!")
            }
            
            Thread.sleep(1500)
        }
        
        ui.limparTela()
        if (personagemAtual!!.pontosDeVida > 0) {
            ui.mostrarVitoria()
            val xp = Random.nextInt(50, 150)
            val ouro = Random.nextInt(10, 50)
            ui.mostrarSucesso("Você derrotou $nomeInimigo!")
            ui.mostrarInfo("Recompensas: $xp XP, $ouro ouro")
            personagemAtual!!.experiencia += xp
            personagemAtual!!.dinheiro += ouro
        } else {
            ui.mostrarDerrota()
            ui.mostrarErro("Você foi derrotado!")
            personagemAtual!!.pontosDeVida = 1 // Não morre permanentemente
        }
        
        ui.aguardarEnter()
    }
    
    private fun visitarLoja() {
        ui.limparTela()
        ui.mostrarInfo("Visitando loja em $localizacaoAtual...")
        ui.mostrarAviso("Sistema de lojas em desenvolvimento")
        ui.aguardarEnter()
    }
    
    private fun falarComNPCs() {
        ui.limparTela()
        ui.mostrarInfo("NPCs em $localizacaoAtual...")
        ui.mostrarAviso("Sistema de diálogos em desenvolvimento")
        ui.aguardarEnter()
    }
    
    private fun eventoAleatorio() {
        ui.limparTela()
        ui.mostrarInfo("Gerando evento aleatório...")
        Thread.sleep(1500)
        
        val eventos = listOf(
            "Você encontrou um baú com 50 moedas de ouro!",
            "Um mercador oferece vender uma poção com desconto.",
            "Você encontrou uma poção de cura menor!",
            "Nada de interessante aconteceu."
        )
        
        val evento = eventos.random()
        ui.mostrarInfo(evento)
        
        if (evento.contains("50 moedas")) {
            personagemAtual!!.dinheiro += 50
        }
        
        ui.aguardarEnter()
    }
    
    private fun processarEvento(evento: WorldExploration.EventoAleatorio) {
        ui.limparTela()
        ui.mostrarAviso(evento.nome)
        println(evento.descricao)
        println()
        
        println(ui.mostrarMenu(
            "O QUE VOCÊ FAZ?",
            evento.opcoes.map { it.texto }
        ))
        
        val escolha = scanner.nextLine().toIntOrNull() ?: 0
        if (escolha in 1..evento.opcoes.size) {
            val opcao = evento.opcoes[escolha - 1]
            ui.mostrarInfo("Você escolheu: ${opcao.texto}")
            // TODO: Processar resultado
        }
        
        ui.aguardarEnter()
    }
    
    private fun menuPersonagem() {
        while (true) {
            ui.limparTela()
            println(ui.mostrarTelaPersonagem(personagemAtual!!))
            println()
            
            println(ui.mostrarMenu(
                "MENU DO PERSONAGEM",
                listOf(
                    "📊 Ver estatísticas detalhadas",
                    "🎒 Gerenciar inventário",
                    "⬆️  Distribuir pontos de atributo",
                    "🎯 Ver talentos",
                    "🔙 Voltar"
                )
            ))
            
            when (scanner.nextLine().toIntOrNull()) {
                1 -> verEstatisticas()
                2 -> gerenciarInventario()
                3 -> distribuirPontos()
                4 -> verTalentos()
                5 -> break
            }
        }
    }
    
    private fun verEstatisticas() {
        ui.limparTela()
        ui.mostrarInfo("Estatísticas detalhadas em desenvolvimento")
        ui.aguardarEnter()
    }
    
    private fun gerenciarInventario() {
        ui.limparTela()
        ui.mostrarInfo("Sistema de inventário em desenvolvimento")
        ui.aguardarEnter()
    }
    
    private fun distribuirPontos() {
        ui.limparTela()
        ui.mostrarInfo("Distribuição de pontos em desenvolvimento")
        ui.aguardarEnter()
    }
    
    private fun verTalentos() {
        ui.limparTela()
        ui.mostrarInfo("Sistema de talentos em desenvolvimento")
        ui.aguardarEnter()
    }
    
    private fun menuQuests() {
        while (true) {
            ui.limparTela()
            println(ui.mostrarMenu(
                "MENU DE QUESTS",
                listOf(
                    "📜 Ver quests ativas",
                    "✅ Ver quests completas",
                    "🆕 Quests disponíveis",
                    "🔙 Voltar"
                )
            ))
            
            when (scanner.nextLine().toIntOrNull()) {
                1 -> verQuestsAtivas()
                2 -> verQuestsCompletas()
                3 -> verQuestsDisponiveis()
                4 -> break
            }
        }
    }
    
    private fun verQuestsAtivas() {
        ui.limparTela()
        val quests = questSystem.getQuestsAtivas()
        
        if (quests.isEmpty()) {
            ui.mostrarInfo("Você não tem quests ativas no momento.")
        } else {
            quests.forEach { quest ->
                println(questSystem.getQuestDetalhes(quest))
                ui.mostrarDivisor()
            }
        }
        
        ui.aguardarEnter()
    }
    
    private fun verQuestsCompletas() {
        ui.limparTela()
        ui.mostrarInfo("Sistema de quests completas em desenvolvimento")
        ui.aguardarEnter()
    }
    
    private fun verQuestsDisponiveis() {
        ui.limparTela()
        ui.mostrarInfo("Sistema de quests disponíveis em desenvolvimento")
        ui.aguardarEnter()
    }
    
    private fun menuMagias() {
        while (true) {
            ui.limparTela()
            println(ui.mostrarMenu(
                "MENU DE MAGIAS",
                listOf(
                    "📖 Ver grimório",
                    "✨ Preparar magias",
                    "🎓 Aprender nova magia",
                    "🔙 Voltar"
                )
            ))
            
            when (scanner.nextLine().toIntOrNull()) {
                1 -> verGrimorio()
                2 -> prepararMagias()
                3 -> aprenderMagia()
                4 -> break
            }
        }
    }
    
    private fun verGrimorio() {
        ui.limparTela()
        ui.mostrarInfo("Grimório em desenvolvimento")
        ui.aguardarEnter()
    }
    
    private fun prepararMagias() {
        ui.limparTela()
        ui.mostrarInfo("Preparação de magias em desenvolvimento")
        ui.aguardarEnter()
    }
    
    private fun aprenderMagia() {
        ui.limparTela()
        ui.mostrarInfo("Aprendizado de magias em desenvolvimento")
        ui.aguardarEnter()
    }
    
    private fun rolarAtributo(): Int {
        // Rola 4d6 e descarta o menor
        val rolagens = List(4) { Random.nextInt(1, 7) }
        return rolagens.sorted().drop(1).sum()
    }
}


package com.rpg

import com.rpg.character.Personagem
import com.rpg.character.attributes.Atributos
import com.rpg.character.races.*
import com.rpg.character.classes.*
import com.rpg.database.DatabaseManager
import com.rpg.database.MonstroData
import com.rpg.combat.CombatSystem
import com.rpg.core.ExperienceSystem
import com.rpg.core.GerenciadorAtributos
import com.rpg.items.GerenciadorLoot
import com.rpg.ui.BattleUI
import com.rpg.ui.CharacterUI
import java.util.*
import kotlin.random.Random

class GameController {
    
    private val scanner = Scanner(System.`in`)
    private val experienceSystem = ExperienceSystem()
    private val gerenciadorAtributos = GerenciadorAtributos()
    private val combatSystem = CombatSystem()
    private val gerenciadorLoot = GerenciadorLoot()
    private val battleUI = BattleUI()
    private val characterUI = CharacterUI()
    
    private var personagemAtual: Personagem? = null
    
    init {
        DatabaseManager.init()
    }
    
    fun iniciar() {
        mostrarTitulo()
        
        while (true) {
            when (mostrarMenuPrincipal()) {
                1 -> criarPersonagem()
                2 -> carregarPersonagem()
                3 -> if (personagemAtual != null) jogar()
                4 -> if (personagemAtual != null) menuPersonagem()
                5 -> {
                    println("\nObrigado por jogar Old Dragon 2 RPG!")
                    break
                }
            }
        }
    }
    
    private fun mostrarTitulo() {
        println("""
            ╔═══════════════════════════════════════════════════════════════╗
            ║                    OLD DRAGON 2 RPG                           ║
            ║                  Sistema Completo de Jogo                     ║
            ║                                                               ║
            ║              Aventure-se em um mundo de fantasia!             ║
            ╚═══════════════════════════════════════════════════════════════╝
        """.trimIndent())
        println()
    }
    
    private fun mostrarMenuPrincipal(): Int {
        println("╔═══════════════════════════════════════╗")
        println("║          MENU PRINCIPAL               ║")
        println("╚═══════════════════════════════════════╝")
        println()
        println("1. 🆕 Criar Novo Personagem")
        println("2. 📂 Carregar Personagem")
        
        if (personagemAtual != null) {
            println("3. ⚔️  Iniciar Aventura")
            println("4. 👤 Menu do Personagem")
        }
        
        println("5. 🚪 Sair")
        println()
        print("Escolha uma opção: ")
        
        return scanner.nextLine().toIntOrNull() ?: 0
    }
    
    private fun criarPersonagem() {
        println("\n╔═══════════════════════════════════════╗")
        println("║       CRIAÇÃO DE PERSONAGEM           ║")
        println("╚═══════════════════════════════════════╝\n")
        
        print("Nome do personagem: ")
        val nome = scanner.nextLine()
        
        // Escolher raça
        println("\nEscolha a raça:")
        println("1. Humano")
        println("2. Elfo")
        println("3. Anão")
        println("4. Halfling")
        print("Opção: ")
        val raca = when (scanner.nextLine().toIntOrNull()) {
            1 -> Humano()
            2 -> Elfo()
            3 -> Anao()
            4 -> Halfling()
            else -> Humano()
        }
        
        // Escolher classe
        println("\nEscolha a classe:")
        println("1. Guerreiro")
        println("2. Mago")
        println("3. Clérigo")
        println("4. Ladrão")
        print("Opção: ")
        val classe = when (scanner.nextLine().toIntOrNull()) {
            1 -> Guerreiro()
            2 -> Mago()
            3 -> Clerigo()
            4 -> Ladrao()
            else -> Guerreiro()
        }
        
        // Gerar atributos
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
        
        // Escolher alinhamento
        println("\nEscolha o alinhamento:")
        println("1. Leal e Bom")
        println("2. Neutro")
        println("3. Caótico e Mau")
        print("Opção: ")
        val alinhamento = when (scanner.nextLine().toIntOrNull()) {
            1 -> Alinhamento.LEAL_E_BOM
            2 -> Alinhamento.NEUTRO
            3 -> Alinhamento.CAOTICO_E_MAL
            else -> Alinhamento.NEUTRO
        }
        
        personagemAtual = Personagem(
            nome = nome,
            raca = raca,
            classe = classe,
            nivel = 1,
            atributos = atributos,
            pontosDeVida = pvInicial,
            pontosDeVidaMaximos = pvInicial,
            experiencia = 0,
            alinhamento = alinhamento,
            dinheiro = 100 // Dinheiro inicial
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
            dinheiro = 100
        )
        
        println("\n✅ Personagem criado com sucesso!")
        println(characterUI.mostrarTelaPersonagem(personagemAtual!!))
        
        print("\nPressione ENTER para continuar...")
        scanner.nextLine()
    }
    
    private fun carregarPersonagem() {
        println("\n╔═══════════════════════════════════════╗")
        println("║       CARREGAR PERSONAGEM             ║")
        println("╚═══════════════════════════════════════╝\n")
        
        val personagens = DatabaseManager.listarPersonagens()
        
        if (personagens.isEmpty()) {
            println("Nenhum personagem salvo encontrado.")
            print("\nPressione ENTER para continuar...")
            scanner.nextLine()
            return
        }
        
        println("Personagens salvos:")
        personagens.forEachIndexed { index, p ->
            println("${index + 1}. ${p.nome} - ${p.raca} ${p.classe} (Nível ${p.nivel})")
        }
        
        print("\nEscolha um personagem (0 para cancelar): ")
        val escolha = scanner.nextLine().toIntOrNull() ?: 0
        
        if (escolha in 1..personagens.size) {
            val dados = personagens[escolha - 1]
            
            // Reconstruir personagem
            val raca = when (dados.raca) {
                "Humano" -> Humano()
                "Elfo" -> Elfo()
                "Anão" -> Anao()
                "Halfling" -> Halfling()
                else -> Humano()
            }
            
            val classe = when (dados.classe) {
                "Guerreiro" -> Guerreiro()
                "Mago" -> Mago()
                "Clérigo" -> Clerigo()
                "Ladrão" -> Ladrao()
                else -> Guerreiro()
            }
            
            val atributos = Atributos(
                forca = dados.forca,
                destreza = dados.destreza,
                constituicao = dados.constituicao,
                inteligencia = dados.inteligencia,
                sabedoria = dados.sabedoria,
                carisma = dados.carisma
            )
            
            personagemAtual = Personagem(
                nome = dados.nome,
                raca = raca,
                classe = classe,
                nivel = dados.nivel,
                atributos = atributos,
                pontosDeVida = dados.pontosVida,
                pontosDeVidaMaximos = dados.pontosVidaMaximos,
                experiencia = dados.experiencia,
                alinhamento = Alinhamento.NEUTRO,
                dinheiro = dados.dinheiro
            )
            
            println("\n✅ Personagem carregado com sucesso!")
            println(characterUI.mostrarTelaPersonagem(personagemAtual!!))
        }
        
        print("\nPressione ENTER para continuar...")
        scanner.nextLine()
    }
    
    private fun jogar() {
        val personagem = personagemAtual ?: return
        
        println("\n╔═══════════════════════════════════════╗")
        println("║            AVENTURA                   ║")
        println("╚═══════════════════════════════════════╝\n")
        
        println("Você entra em uma masmorra sombria...")
        println("De repente, um inimigo aparece!")
        println()
        
        // Selecionar monstro baseado no nível do personagem
        val monstros = DatabaseManager.obterMonstrosPorNivel(
            maxOf(1, personagem.nivel - 1),
            personagem.nivel + 2
        )
        
        if (monstros.isEmpty()) {
            println("Nenhum inimigo encontrado!")
            return
        }
        
        val monstro = monstros.random()
        
        // Mostrar tela de batalha
        println(battleUI.mostrarTelaBatalha(personagem, monstro))
        
        print("Pressione ENTER para iniciar o combate...")
        scanner.nextLine()
        
        // Executar combate
        val resultado = combatSystem.iniciarCombate(personagem, monstro)
        
        // Mostrar log do combate
        resultado.log.forEach { println(it) }
        
        // Processar resultado
        if (resultado.vencedor == "PERSONAGEM") {
            println(battleUI.mostrarVitoria(resultado.xpGanho, resultado.ouroGanho, resultado.itensGanhos))
            
            // Adicionar recompensas
            personagem.dinheiro += resultado.ouroGanho
            resultado.itensGanhos.forEach { personagem.adicionarItem(it) }
            
            // Adicionar XP
            val resultadoXP = experienceSystem.adicionarExperiencia(personagem, resultado.xpGanho)
            println(resultadoXP.getMensagem())
            
            // Se ganhou pontos de atributo
            if (resultadoXP.pontosAtributoGanhos > 0) {
                gerenciadorAtributos.adicionarPontosAtributo(personagem.nome, resultadoXP.pontosAtributoGanhos)
            }
            
            // Salvar progresso
            salvarPersonagem()
            
        } else {
            println(battleUI.mostrarDerrota())
            
            // Penalidade por derrota
            personagem.dinheiro = personagem.dinheiro / 2
            personagem.pontosDeVida = personagem.pontosDeVidaMaximos
            
            salvarPersonagem()
        }
        
        print("\nPressione ENTER para continuar...")
        scanner.nextLine()
    }
    
    private fun menuPersonagem() {
        val personagem = personagemAtual ?: return
        
        while (true) {
            println(characterUI.mostrarMenuPersonagem())
            
            when (scanner.nextLine().toIntOrNull()) {
                1 -> {
                    println(characterUI.mostrarTelaPersonagem(personagem))
                    print("\nPressione ENTER para continuar...")
                    scanner.nextLine()
                }
                2 -> {
                    println(characterUI.mostrarInventario(personagem))
                    print("\nPressione ENTER para continuar...")
                    scanner.nextLine()
                }
                3 -> menuDistribuirPontos()
                4 -> {
                    val progresso = experienceSystem.getProgressoNivel(personagem)
                    println("\n═══ PROGRESSÃO ═══")
                    println("Nível: ${progresso.nivel}")
                    println("XP: ${progresso.xpAtual}/${progresso.xpProximoNivel}")
                    println(experienceSystem.gerarBarraProgresso(progresso))
                    val xpRestante = progresso.xpProximoNivel - progresso.xpAtual
                    println("\nFaltam $xpRestante XP para o próximo nível")
                    print("\nPressione ENTER para continuar...")
                    scanner.nextLine()
                }
                5 -> {
                    salvarPersonagem()
                    println("\n✅ Personagem salvo com sucesso!")
                    print("\nPressione ENTER para continuar...")
                    scanner.nextLine()
                }
                0 -> break
            }
        }
    }
    
    private fun menuDistribuirPontos() {
        val personagem = personagemAtual ?: return
        val pontosDisponiveis = gerenciadorAtributos.getPontosDisponiveis(personagem.nome)
        
        if (pontosDisponiveis <= 0) {
            println("\n❌ Você não tem pontos de atributo disponíveis!")
            print("\nPressione ENTER para continuar...")
            scanner.nextLine()
            return
        }
        
        while (gerenciadorAtributos.getPontosDisponiveis(personagem.nome) > 0) {
            println(gerenciadorAtributos.mostrarMenuDistribuicao(personagem))
            
            val escolha = scanner.nextLine().toIntOrNull() ?: 0
            
            if (escolha == 0) break
            
            val atributo = when (escolha) {
                1 -> "FORCA"
                2 -> "DESTREZA"
                3 -> "CONSTITUICAO"
                4 -> "INTELIGENCIA"
                5 -> "SABEDORIA"
                6 -> "CARISMA"
                else -> ""
            }
            
            if (atributo.isNotEmpty()) {
                val resultado = gerenciadorAtributos.distribuirPonto(personagem, atributo)
                println("\n${resultado.mensagem}")
                
                if (resultado.sucesso) {
                    salvarPersonagem()
                }
                
                Thread.sleep(1000)
            }
        }
    }
    
    private fun salvarPersonagem() {
        val personagem = personagemAtual ?: return
        
        DatabaseManager.atualizarPersonagem(
            nome = personagem.nome,
            nivel = personagem.nivel,
            pontosVida = personagem.pontosDeVida,
            pontosVidaMaximos = personagem.pontosDeVidaMaximos,
            experiencia = personagem.experiencia,
            dinheiro = personagem.dinheiro
        )
    }
    
    private fun rolarAtributo(): Int {
        // Rola 4d6 e descarta o menor
        val rolagens = List(4) { Random.nextInt(1, 7) }
        return rolagens.sorted().drop(1).sum()
    }
}


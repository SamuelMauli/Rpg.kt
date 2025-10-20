package com.rpg

import com.rpg.core.states.GameStateManager
import java.util.*

class OldDragonRPG {
    private val gameStateManager = GameStateManager()
    private val scanner = Scanner(System.`in`)
    
    fun iniciar() {
        println(gameStateManager.iniciar())
        
        while (true) {
            try {
                print("> ")
                val entrada = scanner.nextLine()
                
                // Comandos especiais
                when (entrada.lowercase().trim()) {
                    "quit", "sair", "exit" -> {
                        println("Obrigado por jogar Old Dragon 2 RPG!")
                        break
                    }
                    "help", "ajuda" -> {
                        mostrarAjuda()
                        continue
                    }
                    "status" -> {
                        println(gameStateManager.getStatusJogo())
                        continue
                    }
                    "clear", "limpar" -> {
                        limparTela()
                        continue
                    }
                    "voltar", "back" -> {
                        val resultado = gameStateManager.voltarEstadoAnterior()
                        if (resultado != null) {
                            println(resultado)
                        } else {
                            println("Não é possível voltar ao estado anterior.")
                        }
                        continue
                    }
                    "reiniciar", "restart" -> {
                        println("Reiniciando o jogo...")
                        println(gameStateManager.reiniciarJogo())
                        continue
                    }
                }
                
                // Processar entrada normal
                val resultado = gameStateManager.processarEntrada(entrada)
                println(resultado)
                
            } catch (e: Exception) {
                println("Erro inesperado: ${e.message}")
                println("Digite 'help' para ver os comandos disponíveis.")
            }
        }
        
        scanner.close()
    }
    
    private fun mostrarAjuda() {
        println("""
            ╔══════════════════════════════════════╗
            ║               AJUDA                  ║
            ╚══════════════════════════════════════╝
            
            COMANDOS ESPECIAIS:
            • help/ajuda     - Mostra esta ajuda
            • status         - Mostra status atual do jogo
            • clear/limpar   - Limpa a tela
            • voltar/back    - Volta ao estado anterior
            • reiniciar      - Reinicia o jogo
            • quit/sair/exit - Sai do jogo
            
            NAVEGAÇÃO:
            • Digite o número da opção desejada
            • Use os comandos especiais a qualquer momento
            
            DICAS:
            • Leia as descrições cuidadosamente
            • Explore diferentes opções
            • Gerencie seus recursos (PV, dinheiro, itens)
            • Descanse quando necessário
            
            Boa aventura!
        """.trimIndent())
    }
    
    private fun limparTela() {
        // Limpar tela no terminal
        print("\u001b[2J\u001b[H")
        // Alternativa para diferentes sistemas
        repeat(50) { println() }
    }
}

fun main() {
    println("""
        ╔══════════════════════════════════════╗
        ║           OLD DRAGON 2 RPG           ║
        ║        Terminal Adventure            ║
        ║                                      ║
        ║    Baseado nas regras clássicas      ║
        ║      de RPG de fantasia medieval     ║
        ╚══════════════════════════════════════╝
        
        Carregando o jogo...
    """.trimIndent())
    
    // Pequena pausa para efeito
    Thread.sleep(2000)
    
    try {
        val jogo = OldDragonRPG()
        jogo.iniciar()
    } catch (e: Exception) {
        println("Erro fatal ao iniciar o jogo: ${e.message}")
        e.printStackTrace()
    }
}


package com.rpg

fun main() {
    println("""
        ╔═══════════════════════════════════════════════════════════════╗
        ║                    OLD DRAGON 2 RPG                           ║
        ║                  Sistema Completo de Jogo                     ║
        ║                                                               ║
        ║              Aventure-se em um mundo de fantasia!             ║
        ║                                                               ║
        ║  Características:                                             ║
        ║  • Sistema completo de combate com modificadores              ║
        ║  • Banco de dados SQLite para persistência                    ║
        ║  • Sistema de XP e progressão de níveis                       ║
        ║  • Distribuição de pontos de atributo                         ║
        ║  • Sistema de itens e loot                                    ║
        ║  • Interface visual aprimorada                                ║
        ║  • Múltiplos oponentes com diferentes níveis                  ║
        ╚═══════════════════════════════════════════════════════════════╝
        
        Carregando o jogo...
    """.trimIndent())
    
    // Pequena pausa para efeito
    Thread.sleep(2000)
    
    try {
        val game = GameController()
        game.iniciar()
    } catch (e: Exception) {
        println("Erro fatal ao iniciar o jogo: ${e.message}")
        e.printStackTrace()
    }
}


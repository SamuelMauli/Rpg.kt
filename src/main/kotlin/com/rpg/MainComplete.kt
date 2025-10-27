package com.rpg

/**
 * Ponto de entrada principal do jogo RPG Old Dragon 2
 * Versão completa com todos os sistemas implementados
 */
fun main() {
    try {
        val game = GameControllerComplete()
        game.iniciar()
    } catch (e: Exception) {
        println("\n❌ Erro fatal ao iniciar o jogo: ${e.message}")
        e.printStackTrace()
    }
}


package com.rpg.ui.enhanced

import com.rpg.character.Personagem

/**
 * Interface de Usuário Aprimorada
 * Arte ASCII, cores ANSI e formatação visual
 */
class EnhancedUI {
    
    // Códigos de cor ANSI
    object Colors {
        const val RESET = "\u001B[0m"
        const val BLACK = "\u001B[30m"
        const val RED = "\u001B[31m"
        const val GREEN = "\u001B[32m"
        const val YELLOW = "\u001B[33m"
        const val BLUE = "\u001B[34m"
        const val PURPLE = "\u001B[35m"
        const val CYAN = "\u001B[36m"
        const val WHITE = "\u001B[37m"
        
        // Cores brilhantes
        const val BRIGHT_BLACK = "\u001B[90m"
        const val BRIGHT_RED = "\u001B[91m"
        const val BRIGHT_GREEN = "\u001B[92m"
        const val BRIGHT_YELLOW = "\u001B[93m"
        const val BRIGHT_BLUE = "\u001B[94m"
        const val BRIGHT_PURPLE = "\u001B[95m"
        const val BRIGHT_CYAN = "\u001B[96m"
        const val BRIGHT_WHITE = "\u001B[97m"
        
        // Estilos
        const val BOLD = "\u001B[1m"
        const val UNDERLINE = "\u001B[4m"
        const val REVERSED = "\u001B[7m"
    }
    
    /**
     * Limpa a tela
     */
    fun limparTela() {
        print("\u001B[2J\u001B[H")
    }
    
    /**
     * Mostra logo do jogo
     */
    fun mostrarLogo() {
        println(Colors.BRIGHT_YELLOW + """
            ╔═══════════════════════════════════════════════════════════════════════╗
            ║                                                                       ║
            ║     ██████╗ ██╗     ██████╗     ██████╗ ██████╗  █████╗  ██████╗     ║
            ║    ██╔═══██╗██║     ██╔══██╗    ██╔══██╗██╔══██╗██╔══██╗██╔════╝     ║
            ║    ██║   ██║██║     ██║  ██║    ██║  ██║██████╔╝███████║██║  ███╗    ║
            ║    ██║   ██║██║     ██║  ██║    ██║  ██║██╔══██╗██╔══██║██║   ██║    ║
            ║    ╚██████╔╝███████╗██████╔╝    ██████╔╝██║  ██║██║  ██║╚██████╔╝    ║
            ║     ╚═════╝ ╚══════╝╚═════╝     ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ║
            ║                                                                       ║
            ║                    ⚔️  SISTEMA COMPLETO DE RPG  ⚔️                    ║
            ║                                                                       ║
            ╚═══════════════════════════════════════════════════════════════════════╝
        """.trimIndent() + Colors.RESET)
    }
    
    /**
     * Mostra tela de personagem aprimorada
     */
    fun mostrarTelaPersonagem(personagem: Personagem): String {
        val barraVida = criarBarraProgresso(
            personagem.pontosDeVida,
            personagem.pontosDeVidaMaximos,
            Colors.BRIGHT_RED
        )
        
        val barraXP = criarBarraProgresso(
            personagem.experiencia % 1000,
            1000,
            Colors.BRIGHT_CYAN
        )
        
        return buildString {
            appendLine(Colors.BRIGHT_YELLOW + "╔═══════════════════════════════════════════════════════════════╗" + Colors.RESET)
            appendLine(Colors.BRIGHT_YELLOW + "║" + Colors.RESET + Colors.BOLD + "  FICHA DO PERSONAGEM".padEnd(63) + Colors.RESET + Colors.BRIGHT_YELLOW + "║" + Colors.RESET)
            appendLine(Colors.BRIGHT_YELLOW + "╚═══════════════════════════════════════════════════════════════╝" + Colors.RESET)
            appendLine()
            
            // Informações básicas
            appendLine(Colors.BRIGHT_WHITE + "Nome: " + Colors.BRIGHT_CYAN + personagem.nome + Colors.RESET)
            appendLine(Colors.BRIGHT_WHITE + "Raça: " + Colors.GREEN + personagem.raca.getNome() + Colors.RESET + 
                       "  |  " + Colors.BRIGHT_WHITE + "Classe: " + Colors.GREEN + personagem.classe.getNome() + Colors.RESET)
            appendLine(Colors.BRIGHT_WHITE + "Nível: " + Colors.BRIGHT_YELLOW + personagem.nivel + Colors.RESET)
            appendLine()
            
            // Barras de status
            appendLine(Colors.BRIGHT_WHITE + "Vida: " + Colors.RESET + barraVida + 
                       Colors.BRIGHT_RED + " ${personagem.pontosDeVida}/${personagem.pontosDeVidaMaximos}" + Colors.RESET)
            appendLine(Colors.BRIGHT_WHITE + "XP:   " + Colors.RESET + barraXP + 
                       Colors.BRIGHT_CYAN + " ${personagem.experiencia % 1000}/1000" + Colors.RESET)
            appendLine()
            
            // Atributos
            appendLine(Colors.BRIGHT_YELLOW + "╔═══════════════════════════════════════════════════════════════╗" + Colors.RESET)
            appendLine(Colors.BRIGHT_YELLOW + "║" + Colors.RESET + "  ATRIBUTOS".padEnd(63) + Colors.BRIGHT_YELLOW + "║" + Colors.RESET)
            appendLine(Colors.BRIGHT_YELLOW + "╠═══════════════════════════════════════════════════════════════╣" + Colors.RESET)
            
            val atributos = listOf(
                "Força" to personagem.atributos.forca,
                "Destreza" to personagem.atributos.destreza,
                "Constituição" to personagem.atributos.constituicao,
                "Inteligência" to personagem.atributos.inteligencia,
                "Sabedoria" to personagem.atributos.sabedoria,
                "Carisma" to personagem.atributos.carisma
            )
            
            atributos.chunked(3).forEach { linha ->
                val texto = linha.joinToString("  |  ") { (nome, valor) ->
                    val mod = calcularModificador(valor)
                    val modStr = if (mod >= 0) "+$mod" else "$mod"
                    "${Colors.BRIGHT_WHITE}$nome: ${Colors.BRIGHT_CYAN}$valor ${Colors.BRIGHT_GREEN}($modStr)${Colors.RESET}"
                }
                appendLine(Colors.BRIGHT_YELLOW + "║" + Colors.RESET + "  $texto".padEnd(63) + Colors.BRIGHT_YELLOW + "║" + Colors.RESET)
            }
            
            appendLine(Colors.BRIGHT_YELLOW + "╚═══════════════════════════════════════════════════════════════╝" + Colors.RESET)
            appendLine()
            
            // Recursos
            appendLine(Colors.BRIGHT_WHITE + "💰 Ouro: " + Colors.BRIGHT_YELLOW + personagem.dinheiro + Colors.RESET)
            appendLine(Colors.BRIGHT_WHITE + "🎒 Itens: " + Colors.BRIGHT_CYAN + personagem.inventario.size + Colors.RESET)
        }
    }
    
    /**
     * Cria uma barra de progresso visual
     */
    private fun criarBarraProgresso(atual: Int, maximo: Int, cor: String, largura: Int = 20): String {
        val porcentagem = if (maximo > 0) (atual.toDouble() / maximo * 100).toInt() else 0
        val preenchido = (largura * porcentagem / 100).coerceIn(0, largura)
        val vazio = largura - preenchido
        
        return buildString {
            append("[")
            append(cor)
            repeat(preenchido) { append("█") }
            append(Colors.BRIGHT_BLACK)
            repeat(vazio) { append("░") }
            append(Colors.RESET)
            append("]")
        }
    }
    
    /**
     * Calcula modificador de atributo
     */
    private fun calcularModificador(valor: Int): Int {
        return (valor - 10) / 2
    }
    
    /**
     * Mostra tela de combate aprimorada
     */
    fun mostrarTelaCombate(personagem: Personagem, inimigo: String, inimigoHP: Int, inimigoMaxHP: Int): String {
        val barraVidaPersonagem = criarBarraProgresso(
            personagem.pontosDeVida,
            personagem.pontosDeVidaMaximos,
            Colors.BRIGHT_GREEN
        )
        
        val barraVidaInimigo = criarBarraProgresso(
            inimigoHP,
            inimigoMaxHP,
            Colors.BRIGHT_RED
        )
        
        return buildString {
            appendLine(Colors.BRIGHT_RED + """
                ╔═══════════════════════════════════════════════════════════════╗
                ║                        ⚔️  COMBATE  ⚔️                         ║
                ╚═══════════════════════════════════════════════════════════════╝
            """.trimIndent() + Colors.RESET)
            appendLine()
            
            // Personagem
            appendLine(Colors.BRIGHT_CYAN + "👤 ${personagem.nome}" + Colors.RESET + " (Nível ${personagem.nivel})")
            appendLine("   " + barraVidaPersonagem + Colors.BRIGHT_GREEN + " ${personagem.pontosDeVida}/${personagem.pontosDeVidaMaximos}" + Colors.RESET)
            appendLine()
            
            appendLine(Colors.BRIGHT_WHITE + "        VS" + Colors.RESET)
            appendLine()
            
            // Inimigo
            appendLine(Colors.BRIGHT_RED + "👹 $inimigo" + Colors.RESET)
            appendLine("   " + barraVidaInimigo + Colors.BRIGHT_RED + " $inimigoHP/$inimigoMaxHP" + Colors.RESET)
            appendLine()
            
            appendLine(Colors.BRIGHT_YELLOW + "═══════════════════════════════════════════════════════════════" + Colors.RESET)
        }
    }
    
    /**
     * Mostra menu estilizado
     */
    fun mostrarMenu(titulo: String, opcoes: List<String>): String {
        return buildString {
            appendLine(Colors.BRIGHT_YELLOW + "╔═══════════════════════════════════════╗" + Colors.RESET)
            appendLine(Colors.BRIGHT_YELLOW + "║" + Colors.RESET + Colors.BOLD + "  $titulo".padEnd(41) + Colors.RESET + Colors.BRIGHT_YELLOW + "║" + Colors.RESET)
            appendLine(Colors.BRIGHT_YELLOW + "╚═══════════════════════════════════════╝" + Colors.RESET)
            appendLine()
            
            opcoes.forEachIndexed { index, opcao ->
                val numero = Colors.BRIGHT_CYAN + "${index + 1}." + Colors.RESET
                appendLine("$numero ${Colors.BRIGHT_WHITE}$opcao${Colors.RESET}")
            }
            appendLine()
        }
    }
    
    /**
     * Mostra mensagem de sucesso
     */
    fun mostrarSucesso(mensagem: String) {
        println(Colors.BRIGHT_GREEN + "✅ $mensagem" + Colors.RESET)
    }
    
    /**
     * Mostra mensagem de erro
     */
    fun mostrarErro(mensagem: String) {
        println(Colors.BRIGHT_RED + "❌ $mensagem" + Colors.RESET)
    }
    
    /**
     * Mostra mensagem de informação
     */
    fun mostrarInfo(mensagem: String) {
        println(Colors.BRIGHT_CYAN + "ℹ️  $mensagem" + Colors.RESET)
    }
    
    /**
     * Mostra mensagem de aviso
     */
    fun mostrarAviso(mensagem: String) {
        println(Colors.BRIGHT_YELLOW + "⚠️  $mensagem" + Colors.RESET)
    }
    
    /**
     * Mostra arte ASCII de vitória
     */
    fun mostrarVitoria() {
        println(Colors.BRIGHT_GREEN + """
            
            ██╗   ██╗██╗████████╗ ██████╗ ██████╗ ██╗ █████╗ ██╗
            ██║   ██║██║╚══██╔══╝██╔═══██╗██╔══██╗██║██╔══██╗██║
            ██║   ██║██║   ██║   ██║   ██║██████╔╝██║███████║██║
            ╚██╗ ██╔╝██║   ██║   ██║   ██║██╔══██╗██║██╔══██║╚═╝
             ╚████╔╝ ██║   ██║   ╚██████╔╝██║  ██║██║██║  ██║██╗
              ╚═══╝  ╚═╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚═╝╚═╝  ╚═╝╚═╝
            
        """.trimIndent() + Colors.RESET)
    }
    
    /**
     * Mostra arte ASCII de derrota
     */
    fun mostrarDerrota() {
        println(Colors.BRIGHT_RED + """
            
            ██████╗ ███████╗██████╗ ██████╗  ██████╗ ████████╗ █████╗ 
            ██╔══██╗██╔════╝██╔══██╗██╔══██╗██╔═══██╗╚══██╔══╝██╔══██╗
            ██║  ██║█████╗  ██████╔╝██████╔╝██║   ██║   ██║   ███████║
            ██║  ██║██╔══╝  ██╔══██╗██╔══██╗██║   ██║   ██║   ██╔══██║
            ██████╔╝███████╗██║  ██║██║  ██║╚██████╔╝   ██║   ██║  ██║
            ╚═════╝ ╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝    ╚═╝   ╚═╝  ╚═╝
            
        """.trimIndent() + Colors.RESET)
    }
    
    /**
     * Mostra arte ASCII de level up
     */
    fun mostrarLevelUp() {
        println(Colors.BRIGHT_YELLOW + """
            
            ██╗     ███████╗██╗   ██╗███████╗██╗         ██╗   ██╗██████╗ ██╗
            ██║     ██╔════╝██║   ██║██╔════╝██║         ██║   ██║██╔══██╗██║
            ██║     █████╗  ██║   ██║█████╗  ██║         ██║   ██║██████╔╝██║
            ██║     ██╔══╝  ╚██╗ ██╔╝██╔══╝  ██║         ██║   ██║██╔═══╝ ╚═╝
            ███████╗███████╗ ╚████╔╝ ███████╗███████╗    ╚██████╔╝██║     ██╗
            ╚══════╝╚══════╝  ╚═══╝  ╚══════╝╚══════╝     ╚═════╝ ╚═╝     ╚═╝
            
        """.trimIndent() + Colors.RESET)
    }
    
    /**
     * Mostra divisor decorativo
     */
    fun mostrarDivisor() {
        println(Colors.BRIGHT_YELLOW + "═══════════════════════════════════════════════════════════════" + Colors.RESET)
    }
    
    /**
     * Aguarda entrada do usuário
     */
    fun aguardarEnter(mensagem: String = "Pressione ENTER para continuar...") {
        print(Colors.BRIGHT_BLACK + mensagem + Colors.RESET)
        readLine()
    }
}


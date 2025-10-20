package com.rpg.ui

import com.rpg.character.Personagem
import com.rpg.combat.Monstro
import com.rpg.database.MonstroData

class BattleUI {
    
    fun mostrarTelaBatalha(personagem: Personagem, monstro: MonstroData): String {
        return buildString {
            appendLine()
            appendLine("╔═══════════════════════════════════════════════════════════════╗")
            appendLine("║                        BATALHA!                               ║")
            appendLine("╚═══════════════════════════════════════════════════════════════╝")
            appendLine()
            
            // Informações do personagem
            appendLine("┌─────────────────────────────────┐")
            appendLine("│ ${centralizarTexto(personagem.nome, 31)} │")
            appendLine("│ ${centralizarTexto("${personagem.raca.getNome()} ${personagem.classe.getNome()} - Nível ${personagem.nivel}", 31)} │")
            appendLine("├─────────────────────────────────┤")
            appendLine("│ ${formatarLinha("PV:", "${personagem.pontosDeVida}/${personagem.pontosDeVidaMaximos}", 31)} │")
            appendLine("│ ${formatarBarraVida(personagem.pontosDeVida, personagem.pontosDeVidaMaximos, 29)} │")
            appendLine("│ ${formatarLinha("CA:", "${personagem.getCA()}", 31)} │")
            appendLine("│ ${formatarLinha("Ataque:", "+${personagem.getBAC()}", 31)} │")
            appendLine("└─────────────────────────────────┘")
            
            appendLine()
            appendLine("                    ⚔️  VS  ⚔️")
            appendLine()
            
            // Informações do monstro
            appendLine("┌─────────────────────────────────┐")
            appendLine("│ ${centralizarTexto(monstro.nome, 31)} │")
            appendLine("│ ${centralizarTexto("Nível ${monstro.nivel}", 31)} │")
            appendLine("├─────────────────────────────────┤")
            appendLine("│ ${formatarLinha("PV:", "${monstro.pontosVida}", 31)} │")
            appendLine("│ ${formatarBarraVida(monstro.pontosVida, monstro.pontosVida, 29)} │")
            appendLine("│ ${formatarLinha("CA:", "${monstro.ca}", 31)} │")
            appendLine("│ ${formatarLinha("Ataque:", "+${monstro.baseAtaque}", 31)} │")
            appendLine("│ ${formatarLinha("Dano:", monstro.dano, 31)} │")
            appendLine("└─────────────────────────────────┘")
            
            appendLine()
            appendLine(monstro.descricao)
            appendLine()
        }
    }
    
    fun mostrarStatusBatalha(
        personagem: Personagem, 
        monstroNome: String,
        monstroPV: Int,
        monstroPVMax: Int,
        rodada: Int
    ): String {
        return buildString {
            appendLine()
            appendLine("═══ RODADA $rodada ═══")
            appendLine()
            appendLine("${personagem.nome}: ${gerarBarraVida(personagem.pontosDeVida, personagem.pontosDeVidaMaximos)} ${personagem.pontosDeVida}/${personagem.pontosDeVidaMaximos} PV")
            appendLine("$monstroNome: ${gerarBarraVida(monstroPV, monstroPVMax)} $monstroPV/$monstroPVMax PV")
            appendLine()
        }
    }
    
    fun mostrarMenuBatalha(): String {
        return buildString {
            appendLine("╔═══════════════════════════════════════╗")
            appendLine("║          O QUE FAZER?                 ║")
            appendLine("╚═══════════════════════════════════════╝")
            appendLine()
            appendLine("1. ⚔️  Atacar")
            appendLine("2. 🛡️  Defender (aumenta CA temporariamente)")
            appendLine("3. 🧪 Usar Item")
            appendLine("4. 🏃 Fugir")
            appendLine()
            append("Escolha uma opção: ")
        }
    }
    
    fun mostrarResultadoAtaque(
        atacante: String,
        alvo: String,
        acertou: Boolean,
        dano: Int,
        critico: Boolean,
        esquivou: Boolean,
        pvRestante: Int,
        pvMaximo: Int
    ): String {
        return buildString {
            appendLine()
            append("$atacante ataca $alvo... ")
            
            when {
                esquivou -> {
                    appendLine("↔️  ESQUIVOU!")
                }
                critico -> {
                    appendLine("⚡ CRÍTICO! ⚡")
                    appendLine("💥 Dano devastador: $dano")
                    appendLine("$alvo: ${gerarBarraVida(pvRestante, pvMaximo)} $pvRestante/$pvMaximo PV")
                }
                acertou -> {
                    appendLine("✓ ACERTOU!")
                    appendLine("💥 Dano: $dano")
                    appendLine("$alvo: ${gerarBarraVida(pvRestante, pvMaximo)} $pvRestante/$pvMaximo PV")
                }
                else -> {
                    appendLine("✗ ERROU!")
                }
            }
        }
    }
    
    fun mostrarVitoria(xpGanho: Int, ouroGanho: Int, itens: List<String>): String {
        return buildString {
            appendLine()
            appendLine("╔═══════════════════════════════════════╗")
            appendLine("║             VITÓRIA!                  ║")
            appendLine("╚═══════════════════════════════════════╝")
            appendLine()
            appendLine("🎉 Você venceu a batalha!")
            appendLine()
            appendLine("Recompensas:")
            appendLine("  ✨ XP: +$xpGanho")
            appendLine("  💰 Ouro: +$ouroGanho PO")
            
            if (itens.isNotEmpty()) {
                appendLine()
                appendLine("  📦 Itens encontrados:")
                itens.forEach { item ->
                    appendLine("     • $item")
                }
            }
            appendLine()
        }
    }
    
    fun mostrarDerrota(): String {
        return buildString {
            appendLine()
            appendLine("╔═══════════════════════════════════════╗")
            appendLine("║             DERROTA...                ║")
            appendLine("╚═══════════════════════════════════════╝")
            appendLine()
            appendLine("💀 Você foi derrotado...")
            appendLine()
            appendLine("Você acorda em uma taverna próxima,")
            appendLine("tendo sido resgatado por aventureiros.")
            appendLine("Você perdeu metade do seu ouro.")
            appendLine()
        }
    }
    
    fun mostrarFuga(sucesso: Boolean): String {
        return if (sucesso) {
            buildString {
                appendLine()
                appendLine("🏃 Você conseguiu fugir da batalha!")
                appendLine()
            }
        } else {
            buildString {
                appendLine()
                appendLine("❌ Você não conseguiu fugir!")
                appendLine("O inimigo bloqueia sua saída!")
                appendLine()
            }
        }
    }
    
    private fun gerarBarraVida(pvAtual: Int, pvMaximo: Int, largura: Int = 20): String {
        val porcentagem = if (pvMaximo > 0) (pvAtual.toDouble() / pvMaximo.toDouble()) else 0.0
        val preenchido = (porcentagem * largura).toInt()
        val vazio = largura - preenchido
        
        val cor = when {
            porcentagem >= 0.7 -> "🟩"
            porcentagem >= 0.4 -> "🟨"
            porcentagem >= 0.2 -> "🟧"
            else -> "🟥"
        }
        
        return buildString {
            append("[")
            repeat(preenchido) { append("█") }
            repeat(vazio) { append("░") }
            append("]")
        }
    }
    
    private fun formatarBarraVida(pvAtual: Int, pvMaximo: Int, largura: Int): String {
        val porcentagem = if (pvMaximo > 0) (pvAtual.toDouble() / pvMaximo.toDouble()) else 0.0
        val preenchido = (porcentagem * largura).toInt()
        val vazio = largura - preenchido
        
        return buildString {
            append("[")
            repeat(preenchido) { append("█") }
            repeat(vazio) { append("░") }
            append("]")
        }
    }
    
    private fun centralizarTexto(texto: String, largura: Int): String {
        val espacos = (largura - texto.length) / 2
        val espacosRestantes = largura - texto.length - espacos
        return " ".repeat(espacos) + texto + " ".repeat(espacosRestantes)
    }
    
    private fun formatarLinha(label: String, valor: String, largura: Int): String {
        val espacos = largura - label.length - valor.length
        return "$label${" ".repeat(espacos)}$valor"
    }
}

class CharacterUI {
    
    fun mostrarTelaPersonagem(personagem: Personagem): String {
        return buildString {
            appendLine()
            appendLine("╔═══════════════════════════════════════════════════════════════╗")
            appendLine("║                    FICHA DO PERSONAGEM                        ║")
            appendLine("╚═══════════════════════════════════════════════════════════════╝")
            appendLine()
            
            // Informações básicas
            appendLine("┌─────────────────────────────────────────────────────────────┐")
            appendLine("│ Nome: ${personagem.nome.padEnd(52)} │")
            appendLine("│ Raça: ${personagem.raca.getNome().padEnd(52)} │")
            appendLine("│ Classe: ${personagem.classe.getNome().padEnd(50)} │")
            appendLine("│ Nível: ${personagem.nivel.toString().padEnd(51)} │")
            appendLine("│ Alinhamento: ${personagem.alinhamento.toString().padEnd(45)} │")
            appendLine("└─────────────────────────────────────────────────────────────┘")
            
            appendLine()
            
            // Atributos
            appendLine("┌─────────────────────────────────────────────────────────────┐")
            appendLine("│                         ATRIBUTOS                           │")
            appendLine("├─────────────────────────────────────────────────────────────┤")
            appendLine("│ Força:        ${formatarAtributo(personagem.atributos.forca, personagem.atributos.getModificadorForca())} │")
            appendLine("│ Destreza:     ${formatarAtributo(personagem.atributos.destreza, personagem.atributos.getModificadorDestreza())} │")
            appendLine("│ Constituição: ${formatarAtributo(personagem.atributos.constituicao, personagem.atributos.getModificadorConstituicao())} │")
            appendLine("│ Inteligência: ${formatarAtributo(personagem.atributos.inteligencia, personagem.atributos.getModificadorInteligencia())} │")
            appendLine("│ Sabedoria:    ${formatarAtributo(personagem.atributos.sabedoria, personagem.atributos.getModificadorSabedoria())} │")
            appendLine("│ Carisma:      ${formatarAtributo(personagem.atributos.carisma, personagem.atributos.getModificadorCarisma())} │")
            appendLine("└─────────────────────────────────────────────────────────────┘")
            
            appendLine()
            
            // Combate
            appendLine("┌─────────────────────────────────────────────────────────────┐")
            appendLine("│                          COMBATE                            │")
            appendLine("├─────────────────────────────────────────────────────────────┤")
            appendLine("│ Pontos de Vida:    ${personagem.pontosDeVida}/${personagem.pontosDeVidaMaximos}".padEnd(62) + "│")
            appendLine("│ ${gerarBarraVidaCompleta(personagem.pontosDeVida, personagem.pontosDeVidaMaximos)} │")
            appendLine("│ Classe de Armadura: ${personagem.getCA().toString().padEnd(42)} │")
            appendLine("│ Base de Ataque (Corpo a Corpo): +${personagem.getBAC().toString().padEnd(26)} │")
            appendLine("│ Base de Ataque (À Distância): +${personagem.getBAD().toString().padEnd(28)} │")
            appendLine("│ Jogada de Proteção: ${personagem.getJP().toString().padEnd(38)} │")
            appendLine("└─────────────────────────────────────────────────────────────┘")
            
            appendLine()
            
            // Progressão
            appendLine("┌─────────────────────────────────────────────────────────────┐")
            appendLine("│                        PROGRESSÃO                           │")
            appendLine("├─────────────────────────────────────────────────────────────┤")
            appendLine("│ Experiência: ${personagem.experiencia}/${personagem.getXpNecessarioProximoNivel()}".padEnd(62) + "│")
            appendLine("│ Dinheiro: ${personagem.dinheiro} PO".padEnd(62) + "│")
            appendLine("│ Movimento: ${personagem.getMovimento()} metros".padEnd(62) + "│")
            appendLine("│ Carga: ${personagem.getCargaAtual()}/${personagem.getCargaMaxima()}".padEnd(62) + "│")
            appendLine("└─────────────────────────────────────────────────────────────┘")
            
            appendLine()
        }
    }
    
    fun mostrarMenuPersonagem(): String {
        return buildString {
            appendLine("╔═══════════════════════════════════════╗")
            appendLine("║        MENU DO PERSONAGEM             ║")
            appendLine("╚═══════════════════════════════════════╝")
            appendLine()
            appendLine("1. 📊 Ver Ficha Completa")
            appendLine("2. 🎒 Inventário")
            appendLine("3. ⭐ Distribuir Pontos de Atributo")
            appendLine("4. 📈 Ver Progressão")
            appendLine("5. 💾 Salvar Personagem")
            appendLine("0. ↩️  Voltar")
            appendLine()
            append("Escolha uma opção: ")
        }
    }
    
    fun mostrarInventario(personagem: Personagem): String {
        return buildString {
            appendLine()
            appendLine("╔═══════════════════════════════════════════════════════════════╗")
            appendLine("║                         INVENTÁRIO                            ║")
            appendLine("╚═══════════════════════════════════════════════════════════════╝")
            appendLine()
            
            if (personagem.inventario.isEmpty()) {
                appendLine("Inventário vazio")
            } else {
                personagem.inventario.forEachIndexed { index, item ->
                    appendLine("${index + 1}. $item")
                }
            }
            
            appendLine()
            appendLine("Carga: ${personagem.getCargaAtual()}/${personagem.getCargaMaxima()}")
            appendLine("Dinheiro: ${personagem.dinheiro} PO")
            appendLine()
        }
    }
    
    private fun formatarAtributo(valor: Int, modificador: Int): String {
        val modStr = if (modificador >= 0) "+$modificador" else "$modificador"
        return "$valor ($modStr)".padEnd(50)
    }
    
    private fun gerarBarraVidaCompleta(pvAtual: Int, pvMaximo: Int, largura: Int = 57): String {
        val porcentagem = if (pvMaximo > 0) (pvAtual.toDouble() / pvMaximo.toDouble()) else 0.0
        val preenchido = (porcentagem * largura).toInt()
        val vazio = largura - preenchido
        
        return buildString {
            append("[")
            repeat(preenchido) { append("█") }
            repeat(vazio) { append("░") }
            append("]")
        }
    }
}


package com.rpg.combat

import com.rpg.character.Personagem
import com.rpg.database.MonstroData
import com.rpg.items.ItemEquipavel
import kotlin.random.Random
import kotlin.math.max

data class Monstro(
    val nome: String,
    val nivel: Int,
    var pontosVida: Int,
    val pontosVidaMaximos: Int,
    val ca: Int,
    val baseAtaque: Int,
    val dano: String,
    val xpRecompensa: Int,
    val ouroMin: Int,
    val ouroMax: Int,
    val descricao: String,
    val modificadorForca: Int = 0,
    val modificadorDestreza: Int = 0
)

data class ResultadoCombate(
    val vencedor: String,
    val xpGanho: Int,
    val ouroGanho: Int,
    val itensGanhos: List<String>,
    val log: List<String>
)

data class ModificadoresCombate(
    val bonusAtaque: Int = 0,
    val bonusDano: Int = 0,
    val bonusCA: Int = 0,
    val bonusEsquiva: Int = 0,
    val chanceCritico: Int = 5, // Porcentagem
    val multiplicadorCritico: Double = 2.0
)

class CombatSystem {
    
    fun iniciarCombate(personagem: Personagem, monstroData: MonstroData): ResultadoCombate {
        val monstro = Monstro(
            nome = monstroData.nome,
            nivel = monstroData.nivel,
            pontosVida = monstroData.pontosVida,
            pontosVidaMaximos = monstroData.pontosVida,
            ca = monstroData.ca,
            baseAtaque = monstroData.baseAtaque,
            dano = monstroData.dano,
            xpRecompensa = monstroData.xpRecompensa,
            ouroMin = monstroData.ouroMin,
            ouroMax = monstroData.ouroMax,
            descricao = monstroData.descricao,
            modificadorForca = calcularModificadorPorNivel(monstroData.nivel),
            modificadorDestreza = calcularModificadorPorNivel(monstroData.nivel)
        )
        
        val log = mutableListOf<String>()
        log.add("╔═══════════════════════════════════════╗")
        log.add("║          COMBATE INICIADO!            ║")
        log.add("╚═══════════════════════════════════════╝")
        log.add("")
        log.add("${personagem.nome} (Nível ${personagem.nivel}) vs ${monstro.nome} (Nível ${monstro.nivel})")
        log.add("${personagem.nome}: ${personagem.pontosDeVida}/${personagem.pontosDeVidaMaximos} PV | CA ${personagem.getCA()}")
        log.add("${monstro.nome}: ${monstro.pontosVida}/${monstro.pontosVidaMaximos} PV | CA ${monstro.ca}")
        log.add("")
        
        var rodada = 1
        
        while (personagem.pontosDeVida > 0 && monstro.pontosVida > 0) {
            log.add("═══ RODADA $rodada ═══")
            
            // Determinar iniciativa
            val iniciativaPersonagem = Random.nextInt(1, 21) + personagem.atributos.getModificadorDestreza()
            val iniciativaMonstro = Random.nextInt(1, 21) + monstro.modificadorDestreza
            
            if (iniciativaPersonagem >= iniciativaMonstro) {
                // Personagem ataca primeiro
                executarAtaquePersonagem(personagem, monstro, log)
                if (monstro.pontosVida > 0) {
                    executarAtaqueMonstro(monstro, personagem, log)
                }
            } else {
                // Monstro ataca primeiro
                executarAtaqueMonstro(monstro, personagem, log)
                if (personagem.pontosDeVida > 0) {
                    executarAtaquePersonagem(personagem, monstro, log)
                }
            }
            
            log.add("")
            rodada++
            
            // Limite de rodadas para evitar loops infinitos
            if (rodada > 50) {
                log.add("O combate se arrasta por muito tempo... Ambos recuam!")
                return ResultadoCombate(
                    vencedor = "EMPATE",
                    xpGanho = 0,
                    ouroGanho = 0,
                    itensGanhos = emptyList(),
                    log = log
                )
            }
        }
        
        // Determinar vencedor
        return if (personagem.pontosDeVida > 0) {
            val ouroGanho = Random.nextInt(monstro.ouroMin, monstro.ouroMax + 1)
            val itensGanhos = gerarLoot(monstro)
            
            log.add("╔═══════════════════════════════════════╗")
            log.add("║             VITÓRIA!                  ║")
            log.add("╚═══════════════════════════════════════╝")
            log.add("")
            log.add("${personagem.nome} derrotou ${monstro.nome}!")
            log.add("XP ganho: ${monstro.xpRecompensa}")
            log.add("Ouro ganho: $ouroGanho PO")
            if (itensGanhos.isNotEmpty()) {
                log.add("Itens encontrados:")
                itensGanhos.forEach { log.add("  • $it") }
            }
            
            ResultadoCombate(
                vencedor = "PERSONAGEM",
                xpGanho = monstro.xpRecompensa,
                ouroGanho = ouroGanho,
                itensGanhos = itensGanhos,
                log = log
            )
        } else {
            log.add("╔═══════════════════════════════════════╗")
            log.add("║             DERROTA!                  ║")
            log.add("╚═══════════════════════════════════════╝")
            log.add("")
            log.add("${personagem.nome} foi derrotado por ${monstro.nome}...")
            
            ResultadoCombate(
                vencedor = "MONSTRO",
                xpGanho = 0,
                ouroGanho = 0,
                itensGanhos = emptyList(),
                log = log
            )
        }
    }
    
    private fun executarAtaquePersonagem(personagem: Personagem, monstro: Monstro, log: MutableList<String>) {
        val modificadores = calcularModificadoresPersonagem(personagem)
        
        // Rolagem de ataque
        val d20 = Random.nextInt(1, 21)
        val baseAtaque = personagem.getBAC()
        val totalAtaque = d20 + baseAtaque + modificadores.bonusAtaque
        
        log.add("${personagem.nome} ataca! (d20: $d20 + BA: $baseAtaque + Bonus: ${modificadores.bonusAtaque} = $totalAtaque)")
        
        // Verificar crítico
        val critico = d20 >= (20 - modificadores.chanceCritico / 5)
        
        if (critico) {
            log.add("  ⚡ ACERTO CRÍTICO! ⚡")
            val danoBase = rolarDano("1d8") + personagem.atributos.getModificadorForca()
            val danoTotal = (danoBase * modificadores.multiplicadorCritico).toInt() + modificadores.bonusDano
            monstro.pontosVida = max(0, monstro.pontosVida - danoTotal)
            log.add("  💥 Dano: $danoTotal (${monstro.pontosVida}/${monstro.pontosVidaMaximos} PV restantes)")
        } else if (totalAtaque >= monstro.ca) {
            // Verificar esquiva do monstro
            val chanceEsquiva = calcularChanceEsquiva(monstro.modificadorDestreza)
            val rolagemEsquiva = Random.nextInt(1, 101)
            
            if (rolagemEsquiva <= chanceEsquiva) {
                log.add("  ↔ ${monstro.nome} esquivou do ataque!")
            } else {
                val danoBase = rolarDano("1d8") + personagem.atributos.getModificadorForca()
                val danoTotal = max(1, danoBase + modificadores.bonusDano)
                monstro.pontosVida = max(0, monstro.pontosVida - danoTotal)
                log.add("  ✓ Acertou! Dano: $danoTotal (${monstro.pontosVida}/${monstro.pontosVidaMaximos} PV restantes)")
            }
        } else {
            log.add("  ✗ Errou!")
        }
    }
    
    private fun executarAtaqueMonstro(monstro: Monstro, personagem: Personagem, log: MutableList<String>) {
        // Rolagem de ataque
        val d20 = Random.nextInt(1, 21)
        val totalAtaque = d20 + monstro.baseAtaque + monstro.modificadorForca
        
        log.add("${monstro.nome} ataca! (d20: $d20 + BA: ${monstro.baseAtaque} + Mod: ${monstro.modificadorForca} = $totalAtaque)")
        
        // Verificar crítico
        val critico = d20 == 20
        
        if (critico) {
            log.add("  ⚡ ACERTO CRÍTICO! ⚡")
            val danoBase = rolarDano(monstro.dano) + monstro.modificadorForca
            val danoTotal = (danoBase * 2)
            personagem.receberDano(danoTotal)
            log.add("  💥 Dano: $danoTotal (${personagem.pontosDeVida}/${personagem.pontosDeVidaMaximos} PV restantes)")
        } else if (totalAtaque >= personagem.getCA()) {
            // Verificar esquiva do personagem
            val chanceEsquiva = calcularChanceEsquiva(personagem.atributos.getModificadorDestreza())
            val rolagemEsquiva = Random.nextInt(1, 101)
            
            if (rolagemEsquiva <= chanceEsquiva) {
                log.add("  ↔ ${personagem.nome} esquivou do ataque!")
            } else {
                val danoBase = rolarDano(monstro.dano) + monstro.modificadorForca
                val danoTotal = max(1, danoBase)
                personagem.receberDano(danoTotal)
                log.add("  ✓ Acertou! Dano: $danoTotal (${personagem.pontosDeVida}/${personagem.pontosDeVidaMaximos} PV restantes)")
            }
        } else {
            log.add("  ✗ Errou!")
        }
    }
    
    private fun calcularModificadoresPersonagem(personagem: Personagem): ModificadoresCombate {
        var bonusAtaque = 0
        var bonusDano = 0
        var bonusCA = 0
        var bonusEsquiva = 0
        var chanceCritico = 5
        
        // Bonus baseado em equipamentos (simplificado)
        // TODO: Implementar sistema completo de equipamentos
        
        // Bonus baseado em atributos
        bonusEsquiva = max(0, personagem.atributos.getModificadorDestreza() * 2)
        
        return ModificadoresCombate(
            bonusAtaque = bonusAtaque,
            bonusDano = bonusDano,
            bonusCA = bonusCA,
            bonusEsquiva = bonusEsquiva,
            chanceCritico = chanceCritico
        )
    }
    
    private fun calcularChanceEsquiva(modificadorDestreza: Int): Int {
        // Base: 5% + (modificador de destreza * 2)%
        return max(0, 5 + (modificadorDestreza * 2))
    }
    
    private fun calcularModificadorPorNivel(nivel: Int): Int {
        return when {
            nivel <= 2 -> 0
            nivel <= 4 -> 1
            nivel <= 6 -> 2
            nivel <= 8 -> 3
            else -> 4
        }
    }
    
    private fun rolarDano(dano: String): Int {
        // Parse formato "XdY" ou "XdY+Z"
        val partes = dano.split("+", "-")
        val dadoParte = partes[0].trim()
        val modificador = if (partes.size > 1) {
            val mod = partes[1].trim().toIntOrNull() ?: 0
            if (dano.contains("-")) -mod else mod
        } else 0
        
        val (quantidade, lados) = dadoParte.split("d").map { it.toInt() }
        
        var total = 0
        repeat(quantidade) {
            total += Random.nextInt(1, lados + 1)
        }
        
        return total + modificador
    }
    
    private fun gerarLoot(monstro: Monstro): List<String> {
        val itens = mutableListOf<String>()
        
        // Chance de drop baseada no nível do monstro
        val chanceDrop = when {
            monstro.nivel <= 2 -> 20
            monstro.nivel <= 5 -> 35
            monstro.nivel <= 8 -> 50
            else -> 70
        }
        
        if (Random.nextInt(1, 101) <= chanceDrop) {
            val itemPossivel = when (monstro.nivel) {
                1 -> listOf("Poção de Cura Menor", "Adaga", "Armadura de Couro")
                2 -> listOf("Poção de Cura Menor", "Espada Curta", "Escudo")
                3, 4 -> listOf("Poção de Cura", "Espada Longa", "Cota de Malha")
                5, 6 -> listOf("Poção de Cura", "Machado de Batalha", "Anel de Proteção +1")
                7, 8 -> listOf("Poção de Cura Maior", "Arco Longo", "Armadura de Placas")
                else -> listOf("Poção de Cura Maior", "Amuleto da Sorte", "Armadura de Placas")
            }
            
            itens.add(itemPossivel.random())
        }
        
        return itens
    }
}

